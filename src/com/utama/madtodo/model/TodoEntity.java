package com.utama.madtodo.model;

import java.util.Date;
import java.util.Locale;


abstract public class TodoEntity {

  protected long id;
  protected String name;
  protected String description;
  protected Date expiry;
  protected Boolean isImportant;
  protected Boolean isMarkedDone;


  public TodoEntity() {
    id = -1;
    expiry = new Date(0);
  }
  
  
  public TodoEntity(TodoEntity todo) {
    id = todo.getId();
    name = todo.getName();
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
  
  
  abstract protected long create();  
  abstract protected long update();
  abstract public long delete();
  
  
  public long save() {
    if (id == -1)
      return create();
    else
      return update();
  }
  

  public String toString() {
    return String.format(Locale.US,
        "id: %d\nname: %s\ndescription: %s\nexpiry: %s\nisImportant: %b\nisMarkedDone: %b", id,
        name, description, expiry.toString(), isImportant, isMarkedDone);
  }

}
