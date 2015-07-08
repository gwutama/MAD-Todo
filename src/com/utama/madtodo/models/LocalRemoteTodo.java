package com.utama.madtodo.models;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.utama.madtodo.R;
import com.utama.madtodo.SettingsActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class LocalRemoteTodo extends TodoEntity {

  private static boolean offlineMode;
  
  
  public LocalRemoteTodo() {
    super();
  }


  public LocalRemoteTodo(TodoEntity todo) {
    super(todo);
  }


  public static LocalRemoteTodo findOne(long id) {
    LocalTodo local = LocalTodo.findOne(id);    
    LocalRemoteTodo ret = new LocalRemoteTodo(local);
    return ret;
  }


  public static boolean isOfflineMode() {
    return offlineMode;
  }


  public static List<LocalRemoteTodo> findAll() {
    return LocalRemoteTodo.findAll(null);
  }
  

  public static List<LocalRemoteTodo> findAll(String sortOrder) {
    List<LocalTodo> locals = LocalTodo.findAll(sortOrder);
    List<LocalRemoteTodo> ret = new ArrayList<LocalRemoteTodo>();

    for (LocalTodo local : locals) {
      LocalRemoteTodo todo = new LocalRemoteTodo(local);
      ret.add(todo);
    }
    
    return ret;
  }


  @Override
  protected long create() throws IOException, JSONException {
    LocalTodo local = buildLocalTodo();
    long localRowId = local.create();

    if (!offlineMode && localRowId > -1) {
      RemoteTodo remote = new RemoteTodo(local);
      long remoteId = remote.create();
      local.setRemoteId(remoteId);
      local.update();
    }

    return localRowId;
  }


  @Override
  protected long update() throws IOException, JSONException {
    LocalTodo localOrig = LocalTodo.findOne(id);
    LocalTodo local = buildLocalTodo();
    local.setId(localOrig.getId());
    local.setRemoteId(localOrig.getRemoteId());
    long localRowId = local.update();

    if (!offlineMode && localRowId > -1) {
      RemoteTodo remote = RemoteTodo.findOne(local.getRemoteId());
      if (remote != null) {
        remote.setTodo(local);
        remote.update();
      }
    }

    return localRowId;
  }


  @Override
  public long delete() throws IOException {
    LocalTodo local = LocalTodo.findOne(id);
    long remoteId = local.getRemoteId();
    long localCount = local.delete();

    if (!offlineMode) {
      try {
        RemoteTodo remote = RemoteTodo.findOne(remoteId);
        remote.delete();      
      } catch (JSONException e) {
      }
    }

    return localCount;
  }

  
  public static void sync() throws IOException, JSONException {
    if (offlineMode)
      throw new IOException();
    
    String sortOrder = DbConsts.Column.ID + " ASC";
    List<LocalTodo> locals = LocalTodo.findAll(sortOrder);

    if (locals.size() > 0) {
      RemoteTodo.purge();
      for (LocalTodo local : locals) {
        RemoteTodo remote = new RemoteTodo(local);
        remote.setId(-1);
        remote.setRemoteId(-1);
        long remoteId = remote.save();
        local.setRemoteId(remoteId);
        local.save();
      }
    } else {
      List<RemoteTodo> remotes = RemoteTodo.findAll();
      for (RemoteTodo remote : remotes) {
        long remoteId = remote.getRemoteId();
        LocalTodo local = new LocalTodo(remote);
        local.setId(-1);
        local.setRemoteId(remoteId);
        local.save();
      }
    }
  }


  private LocalTodo buildLocalTodo() {
    LocalTodo todo = new LocalTodo();
    todo.setName(name);
    todo.setDescription(description);
    todo.setExpiry(expiry);
    todo.setImportant(isImportant);
    todo.setMarkedDone(isMarkedDone);
    return todo;
  }


  public static final boolean setupPersistence(Activity activity) {
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
  
    boolean offlineMode = pref.getBoolean("offlineMode", false);
    LocalRemoteTodo.offlineMode = offlineMode;
  
    if (!offlineMode) {
      try {
        URL apiRoot = new URL(pref.getString("apiRoot", ""));
        RemoteTodo.setApiRoot(apiRoot);
        RemoteUser.setApiRoot(apiRoot);
      } catch (MalformedURLException e) {
        e.printStackTrace();
        Toast.makeText(activity, R.string.api_root_error, Toast.LENGTH_LONG).show();
        
        if (activity instanceof SettingsActivity == false)
          activity.startActivity(new Intent(activity, SettingsActivity.class));
        
        return false;
      }
    }
  
    LocalTodo.setDbHelper(new DbHelper(activity));
  
    return true;
  }
  
  
  public static final void switchToOfflineMode(Activity activity) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    Editor editor = prefs.edit();
    editor.putBoolean("offlineMode", true);
    editor.commit();
    LocalRemoteTodo.setupPersistence(activity);    
  }
}
