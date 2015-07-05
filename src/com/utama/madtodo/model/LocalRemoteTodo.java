package com.utama.madtodo.model;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;


public class LocalRemoteTodo extends TodoEntity {

  public LocalRemoteTodo() {
    super();
  }


  public LocalRemoteTodo(TodoEntity todo) {
    super(todo);
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
    LocalTodo local = buildLocalTodo();
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
        long remoteId = remote.getId();
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
