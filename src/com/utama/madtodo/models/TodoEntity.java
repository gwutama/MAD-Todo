package com.utama.madtodo.models;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;


/**
 * The Class TodoEntity is an abstract class that represents a task.
 */
abstract public class TodoEntity {

  /** The id of the task. */
  protected long id;

  /** The remote id of the task. */
  protected long remoteId;

  /** The name of the task. */
  protected String name;

  /** The description of the task. */
  protected String description;

  /** The expiry date of the task. */
  protected Date expiry;

  /** Whether the task is important. */
  protected Boolean isImportant;

  /** Whether the task was marked as done. */
  protected Boolean isMarkedDone;


  /**
   * Instantiates a new todo entity.
   */
  public TodoEntity() {
    id = -1;
    remoteId = -1;
    expiry = new Date(0);
  }


  /**
   * Instantiates a new todo entity.
   *
   * @param todo the todo.
   */
  public TodoEntity(TodoEntity todo) {
    this();
    setTodo(todo);
  }


  /**
   * Sets the todo.
   *
   * @param todo the new todo.
   */
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


  /**
   * Gets the id.
   *
   * @return The id.
   */
  public long getId() {
    return id;
  }


  /**
   * Sets the id.
   *
   * @param id the new id.
   */
  public void setId(long id) {
    this.id = id;
  }


  /**
   * Gets the remote id.
   *
   * @return The remote id.
   */
  public long getRemoteId() {
    return remoteId;
  }


  /**
   * Sets the remote id.
   *
   * @param remoteId The new remote id.
   */
  public void setRemoteId(long remoteId) {
    this.remoteId = remoteId;
  }


  /**
   * Gets the name.
   *
   * @return The name.
   */
  public String getName() {
    return name;
  }


  /**
   * Sets the name.
   *
   * @param name The new name.
   */
  public void setName(String name) {
    this.name = name;
  }


  /**
   * Gets the description.
   *
   * @return The description.
   */
  public String getDescription() {
    return description;
  }


  /**
   * Sets the description.
   *
   * @param description The new description.
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * Gets the expiry date.
   *
   * @return The expiry date.
   */
  public Date getExpiry() {
    return expiry;
  }


  /**
   * Sets the expiry date.
   *
   * @param expiry The new expiry date.
   */
  public void setExpiry(Date expiry) {
    this.expiry = expiry;
  }


  /**
   * Checks if is important.
   *
   * @return True whether this task is important. False otherwise.
   */
  public Boolean isImportant() {
    return isImportant;
  }


  /**
   * Sets the priority of the task.
   *
   * @param isImportant Whether the task is important.
   */
  public void setImportant(Boolean isImportant) {
    this.isImportant = isImportant;
  }


  /**
   * Checks whether the task was marked as done.
   *
   * @return True if the task was marked as done. False otherwise.
   */
  public Boolean isMarkedDone() {
    return isMarkedDone;
  }


  /**
   * Sets the status of this task.
   *
   * @param isMarkedDone Whether the task is done.
   */
  public void setMarkedDone(Boolean isMarkedDone) {
    this.isMarkedDone = isMarkedDone;
  }


  /**
   * Create a new entity.
   *
   * @return The id of created task on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws IOException Thrown on network error.
   * @throws JSONException Thrown on invalid JSON response from server.
   */
  abstract protected long create() throws IllegalArgumentException, IOException, JSONException;

  /**
   * Update the entity.
   *
   * @return The id of updated task on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws IOException Thrown on network error.
   * @throws JSONException Thrown on invalid JSON response from server.
   */
  abstract protected long update() throws IllegalArgumentException, IOException, JSONException;

  /**
   * Delete the entity.
   *
   * @return The id of deleted task on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws IOException Thrown on network error.
   * @throws JSONException Thrown on invalid JSON response from server.
   */
  abstract public long delete() throws IllegalArgumentException, IOException, JSONException;


  /**
   * Saves the entity. If the id equals to -1, the entity will be created, otherwise it will be
   * updated.
   *
   * @return The id of saved (created or updated) task on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws IOException Thrown on network error.
   * @throws JSONException Thrown on invalid JSON response from server.
   */
  public long save() throws IllegalArgumentException, IOException, JSONException {
    if (id == -1)
      return create();
    else
      return update();
  }


  /**
   * Return a string representation of this object.
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return String.format(Locale.US,
        "id: %d\n" + "remote id: %d\n" + "name: %s\n" + "description: %s\n" + "expiry: %s\n"
            + "isImportant: %b\n" + "isMarkedDone: %b\n",
        id, remoteId, name, description, expiry.toString(), isImportant, isMarkedDone);
  }

}
