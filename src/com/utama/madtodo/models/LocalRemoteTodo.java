package com.utama.madtodo.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;


public class LocalRemoteTodo extends TodoEntity {

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

    if (localRowId > -1) {
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

    if (localRowId > -1) {
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
    RemoteTodo remote = RemoteTodo.findOne(remoteId);

    if (remote != null)
      remote.delete();

    return localCount;
  }

  
  public static void sync() throws IOException, JSONException {
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
}
