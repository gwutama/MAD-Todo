package com.utama.madtodo.models;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.models.DbConsts.Table.Tasks;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.widget.Toast;


/**
 * The Class LocalRemoteTodo represents a class for working with both local and remote todo tasks.
 * This class executes CRUD operations for local tasks then executes them on the remote web service
 * levels, making sure that tasks are synchronized with the remote web service. Internally, this
 * class uses operations in {@link LocalTodo} and {@link RemoteTodo}.
 * 
 * If the web service is not accessible, this class will just ignore the web service operations on
 * the remote side. Thus, it is recommended to use this class instead of using the lower level
 * LocalTodo and RemoteTodo for manipulating tasks because this class will make sure that operations
 * are done on local and remote web service.
 */
public class LocalRemoteTodo extends TodoEntity {

  /**
   * Whether we are working offline or not. This should only be set by the instance of this class
   * and not from somewhere else.
   */
  private static boolean offlineMode;


  /**
   * Instantiates a new local remote todo.
   */
  public LocalRemoteTodo() {
    super();
  }


  /**
   * Instantiates a new local remote todo using another todo entity.
   *
   * @param todo A todo entity.
   */
  public LocalRemoteTodo(TodoEntity todo) {
    super(todo);
  }


  /**
   * Find one record by (local) task id.
   *
   * @param id The id of the local task. Please not that this is NOT the remote task id, since they
   *        serve for different purposes.
   * @return A {@link LocalRemoteTodo} todo. If record cannot be found, this will return an instance
   *         which id equals to -1.
   * @throws IllegalArgumentException Thrown when id is invalid (< 0).
   */
  public static LocalRemoteTodo findOne(long id) throws IllegalArgumentException {
    LocalTodo local = LocalTodo.findOne(id);
    LocalRemoteTodo ret = new LocalRemoteTodo(local);
    return ret;
  }


  /**
   * Checks if offline mode is enabled.
   *
   * @return True, if offline mode is enabled. False otherwise.
   */
  public static boolean isOfflineMode() {
    return offlineMode;
  }


  /**
   * Find all task records. The results are sorted by {@link DbConsts.DEFAULT_SORT}.
   *
   * @return A list of {@link LocalRemoteTodo} instance.
   */
  public static List<LocalRemoteTodo> findAll() {
    return LocalRemoteTodo.findAll(null);
  }


  /**
   * Find all task records with a specific sorting order.
   *
   * @param sortOrder The sort order. You can supply other "WHERE" clauses other than the ones
   *        specified by {@link DbConsts.SORT_IMPORTANCE_DATE} or
   *        {@link DbConsts.SORT_DATE_IMPORTANCE}.
   * @return A list of {@link LocalRemoteTodo} instance.
   */
  public static List<LocalRemoteTodo> findAll(String sortOrder) {
    List<LocalTodo> locals = LocalTodo.findAll(sortOrder);
    List<LocalRemoteTodo> ret = new ArrayList<LocalRemoteTodo>();

    for (LocalTodo local : locals) {
      LocalRemoteTodo todo = new LocalRemoteTodo(local);
      ret.add(todo);
    }

    return ret;
  }


  /**
   * Create a task record on device local and web service levels.
   *
   * @return The newly inserted record id on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws SQLiteException Thrown when the {@link LocalTodo#DbHelper} instance is not set. Use
   *         {@link LocalTodo#setDsetDbHelper(DbHelper)} to do it before executing this method.
   * @throws IOException Thrown on network error.
   * @throws JSONException Thrown on invalid JSON response from server.
   * @see com.utama.madtodo.models.TodoEntity#create()
   */
  @Override
  protected long create()
      throws IllegalArgumentException, SQLiteException, IOException, JSONException {
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


  /**
   * Update a task on local device and web service levels.
   *
   * @return The updated record id on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws SQLiteException Thrown when the {@link LocalTodo#DbHelper} instance is not set. Use
   *         {@link LocalTodo#setDsetDbHelper(DbHelper)} to do it before executing this method.
   * @throws IOException Thrown on network error.
   * @throws JSONException Thrown on invalid JSON response from server.
   * @see com.utama.madtodo.models.TodoEntity#update()
   */
  @Override
  protected long update()
      throws IllegalArgumentException, SQLiteException, IOException, JSONException {
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


  /**
   * Delete a task on local device and web service levels.
   *
   * @return The deleted record id on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws SQLiteException Thrown when the {@link LocalTodo#DbHelper} instance is not set. Use
   *         {@link LocalTodo#setDsetDbHelper(DbHelper)} to do it before executing this method.
   * @throws IOException Thrown on network error.
   * @see com.utama.madtodo.models.TodoEntity#delete()
   */
  @Override
  public long delete() throws IllegalArgumentException, SQLiteException, IOException {
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


  /**
   * Synchronize data between local device and web service using a simple sync method: If there is
   * records on the local device, then all remote tasks will be deleted and tasks from local device
   * will be copied to the web service. Otherwise all tasks from the remote web service will be
   * copied to the local device.
   *
   * @throws IOException Thrown when there is a network issue, as well as when the offline mode is
   *         active.
   * @throws JSONException Thrown when the reply from the web service is not a valid JSON string.
   */
  public static void sync() throws IOException, JSONException {
    if (offlineMode)
      throw new IOException();

    String sortOrder = Tasks.Column.ID + " ASC";
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


  /**
   * Builds the local todo instance.
   *
   * @return the local todo
   */
  private LocalTodo buildLocalTodo() {
    LocalTodo todo = new LocalTodo();
    todo.setName(name);
    todo.setDescription(description);
    todo.setExpiry(expiry);
    todo.setImportant(isImportant);
    todo.setMarkedDone(isMarkedDone);
    return todo;
  }


  /**
   * Setup static constants for {@link LocalRemoteTodo}, {@link RemoteTodo} and {@link RemoteUser}.
   * This method should be executed once on activity creation.
   * 
   * This method will retrieve from the preference manager whether offline mode is active. If it's
   * enabled, then it will retry to retrieve and check the API root URL from the preference manager
   * whether the URL is valid and is not empty. If it is invalid, then this method will assume
   * offline mode and starts the {@link TodoListActivity} automatically.
   *
   * @param activity The activity instance.
   * @return True if local and remote persistence can be set (means that we can work both local and
   *         on the web service). False otherwise.
   */
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

        switchToOfflineMode(activity);
        activity.startActivity(new Intent(activity, TodoListActivity.class));

        return false;
      }
    }

    LocalTodo.setDbHelper(new DbHelper(activity));

    return true;
  }


  /**
   * This method makes sure that we are working offline only. Note that it is safer to let
   * {@link LocalRemoteTodo#setupPersistence(Activity) to decide whether offline mode should be
   * enabled or not.
   * 
   * This method will setup static constants for {@link LocalRemoteTodo}, {@link RemoteTodo} and
   * {@link RemoteUser}.
   * 
   * @param activity The activity instance.
   */
  public static final void switchToOfflineMode(Activity activity) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    Editor editor = prefs.edit();
    editor.putBoolean("offlineMode", true);
    editor.commit();
    LocalRemoteTodo.setupPersistence(activity);
  }


  /**
   * This method makes sure that we are working both locally and on the web service. Note that it is
   * safer to let {@link LocalRemoteTodo#setupPersistence(Activity) to decide whether offline mode
   * should be enabled or not.
   * 
   * This method will setup static constants for {@link LocalRemoteTodo}, {@link RemoteTodo} and
   * {@link RemoteUser}.
   * 
   * @param activity The activity instance.
   */
  public static final void switchToOnlineMode(Activity activity) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    Editor editor = prefs.edit();
    editor.putBoolean("offlineMode", false);
    editor.commit();
    LocalRemoteTodo.setupPersistence(activity);
  }
}
