package com.utama.madtodo.models;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;


abstract public class TodoEntity {

  protected long id;
  protected long remoteId;
  protected String name;
  protected String description;
  protected Date expiry;
  protected Boolean isImportant;
  protected Boolean isMarkedDone;


  public TodoEntity() {
    id = -1;
    remoteId = -1;
    expiry = new Date(0);
  }


  public TodoEntity(TodoEntity todo) {
    this();
    setTodo(todo);
  }
  
  
  public void setTodo(TodoEntity todo) {
    id = todo.getId();
    remoteId = todo.getRemoteId();
    name = todo.getName();
    remoteId = todo.getRemoteId();
    description = todo.getDescription();
    expiry = todo.getExpiry();
    isImportant = todo.isImportant();
    isMarkedDone = todo.isMarkedDone();    
  }


  public long getId() {
    return id;
  }


  public void setId(long id) {
    this.id = id;
  }


  public long getRemoteId() {
    return remoteId;
  }


  public void setRemoteId(long remoteId) {
    this.remoteId = remoteId;
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public String getDescription() {
    return description;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public Date getExpiry() {
    return expiry;
  }


  public void setExpiry(Date expiry) {
    this.expiry = expiry;
  }


  public Boolean isImportant() {
    return isImportant;
  }


  public void setImportant(Boolean isImportant) {
    this.isImportant = isImportant;
  }


  public Boolean isMarkedDone() {
    return isMarkedDone;
  }


  public void setMarkedDone(Boolean isMarkedDone) {
    this.isMarkedDone = isMarkedDone;
  }


  abstract protected long create() throws IOException, JSONException;

  abstract protected long update() throws IOException, JSONException;

  abstract public long delete() throws IOException, JSONException;


  public long save() throws IOException, JSONException {
    if (id == -1)
      return create();
    else
      return update();
  }


  public String toString() {
    return String.format(Locale.US,
        "id: %d\n"
        + "remote id: %d\n"
        + "name: %s\n"
        + "description: %s\n"
        + "expiry: %s\n"
        + "isImportant: %b\n"
        + "isMarkedDone: %b\n",
        id, remoteId, name, description, expiry.toString(), isImportant, isMarkedDone);
  }

}
