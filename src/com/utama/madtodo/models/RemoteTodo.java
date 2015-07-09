package com.utama.madtodo.models;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.utama.madtodo.utils.SimpleRestClient;

import android.text.TextUtils;


/**
 * The Class RemoteTodo represents a class for working with remote todo tasks. This method will NOT
 * take care of a synchronized operations with the local database.
 * 
 * This is a lower level class for executing CRUD operations and managing records that are saved on
 * the web service.
 */
public class RemoteTodo extends TodoEntity {

  /** The Constant RESOURCE_PATH represents the web service API path for managing todo. */
  private static final String RESOURCE_PATH = "/todos";

  /** The API root to the web service. */
  private static URL apiRoot;


  /**
   * Instantiates a new remote todo.
   */
  public RemoteTodo() {
    super();
  }


  /**
   * Instantiates a new remote todo using a todo entity.
   *
   * @param todo A todo entity.
   */
  public RemoteTodo(TodoEntity todo) {
    super(todo);
  }


  /**
   * Instantiates a new remote todo using a JSON string.
   *
   * @param json A JSON string.
   */
  public RemoteTodo(String json) {
    super();
    try {
      JSONObject jsonObj = new JSONObject(json);
      setFromJsonObject(jsonObj);
    } catch (JSONException e) {
      id = -1;
      remoteId = -1;
      e.printStackTrace();
    }
  }


  /**
   * Instantiates a new remote todo using a JSON object.
   *
   * @param obj A JSON object.
   */
  public RemoteTodo(JSONObject obj) {
    super();
    setFromJsonObject(obj);
  }


  /**
   * Sets the entity from a JSON object.
   *
   * @param obj A JSON object.
   */
  private void setFromJsonObject(JSONObject obj) {
    try {
      id = -1;
      remoteId = obj.getLong("id");
      name = obj.getString("name");
      description = obj.getString("description");

      try {
        expiry = new Date(obj.getLong("expiry"));
      } catch (JSONException e) {
        expiry = new Date(0);
      }
    } catch (JSONException e) {
      id = -1;
      remoteId = -1;
      e.printStackTrace();
    }
  }


  /**
   * Gets the API root.
   *
   * @return The API root.
   */
  public static URL getApiRoot() {
    return apiRoot;
  }


  /**
   * Sets the API root.
   *
   * @param apiRoot The new API root
   */
  public static void setApiRoot(URL apiRoot) {
    RemoteTodo.apiRoot = apiRoot;
  }


  /**
   * Find all task records. The results are sorted by {@link DbConsts.DEFAULT_SORT}.
   *
   * @return A list of {@link RemoteTodo} instances.
   * @throws IOException Thrown when there is a network issue, as well as when the offline mode is
   *         active.
   * @throws JSONException Thrown when the reply from the web service is not a valid JSON string.
   */
  public static List<RemoteTodo> findAll() throws IOException, JSONException {
    SimpleRestClient rest = new SimpleRestClient(apiRoot, "GET");
    List<RemoteTodo> todos;

    try {
      rest.setPath(RESOURCE_PATH);
      rest.open();
      String resp = rest.read();
      todos = buildEntities(resp);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IOException("Network error. Malformed API root or resource path?");
    } finally {
      rest.close();
    }

    return todos;
  }


  /**
   * Helper method to builds todo entities from a JSON array format in string.
   *
   * @param json A string of JSON array.
   * @return A list of {@link RemoteTodo} instances.
   * @throws JSONException Thrown when the input is not a valid JSON string.
   */
  private static List<RemoteTodo> buildEntities(String json) throws JSONException {
    JSONArray jsonArr = new JSONArray(json);
    List<RemoteTodo> ret = new ArrayList<RemoteTodo>();

    for (int i = 0; i < jsonArr.length(); i++) {
      JSONObject jsonObj = jsonArr.getJSONObject(i);
      RemoteTodo todo = new RemoteTodo(jsonObj);
      ret.add(todo);
    }

    return ret;
  }


  /**
   * Find one record by (remote) task id.
   *
   * @param remoteId The remote id.
   * @return A {@link RemoteTodo} todo. If record cannot be found, this will return an instance
   *         which id and remote id equal to -1.
   * @throws IllegalArgumentException Thrown when remote id is invalid.
   * @throws IOException Thrown when there is a network issue, as well as when the offline mode is
   *         active.
   * @throws JSONException Thrown when the reply from the web service is not a valid JSON string.
   */
  public static RemoteTodo findOne(long remoteId)
      throws IllegalArgumentException, IOException, JSONException {
    if (remoteId < 0)
      throw new IllegalArgumentException("Invalid id");

    SimpleRestClient rest = new SimpleRestClient(apiRoot, "GET");
    RemoteTodo todo = null;

    try {
      rest.setPath(RESOURCE_PATH + "/" + Long.toString(remoteId));
      rest.open();
      todo = new RemoteTodo(rest.readJson());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IOException("Network error. Malformed API root or resource path?");
    } finally {
      rest.close();
    }

    return todo;
  }


  /**
   * Create a task in the remote web service.
   *
   * @return The remote id of the created record on success. Otherwise -1.
   * @throws IOException Thrown when there is a network issue, as well as when the offline mode is
   *         active.
   * @throws JSONException Thrown when the reply from the web service is not a valid JSON string.
   * @see com.utama.madtodo.models.TodoEntity#create()
   */
  @Override
  protected long create() throws IOException, JSONException {
    if (TextUtils.isEmpty(name))
      throw new IllegalArgumentException("Task name cannot be empty");

    SimpleRestClient rest = new SimpleRestClient(apiRoot, "POST");

    try {
      rest.setPath(RESOURCE_PATH);
      rest.open();
      rest.write(buildRequestPayload());
      setFromJsonObject(rest.readJson());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IOException("Network error. Malformed API root or resource path?");
    } finally {
      rest.close();
    }

    return remoteId;
  }


  /**
   * Update a task in the remote web service.
   *
   * @return The remote id of the updated record on success. Otherwise -1.
   * @throws IOException Thrown when there is a network issue, as well as when the offline mode is
   *         active.
   * @throws JSONException Thrown when the reply from the web service is not a valid JSON string.
   * @see com.utama.madtodo.models.TodoEntity#update()
   */
  @Override
  protected long update() throws IOException, JSONException {
    if (TextUtils.isEmpty(name))
      throw new IllegalArgumentException("Task name cannot be empty");

    SimpleRestClient rest = new SimpleRestClient(apiRoot, "PUT");

    try {
      rest.setPath(RESOURCE_PATH);
      rest.open();
      rest.write(buildRequestPayload());
      setFromJsonObject(rest.readJson());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IOException("Network error. Malformed API root or resource path?");
    } finally {
      rest.close();
    }

    return remoteId;
  }


  /**
   * Delete a task in the remote web service.
   *
   * @return The remote id of the deleted record on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when remote id is invalid.
   * @throws IOException Thrown when there is a network issue, as well as when the offline mode is
   *         active.
   * @see com.utama.madtodo.models.TodoEntity#delete()
   */
  @Override
  public long delete() throws IllegalArgumentException, IOException {
    if (remoteId < 0)
      throw new IllegalArgumentException("Invalid id");

    SimpleRestClient rest = new SimpleRestClient(apiRoot, "DELETE");
    String response;

    try {
      rest.setPath(RESOURCE_PATH + "/" + Long.toString(remoteId));
      rest.open();
      response = rest.read();
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IOException("Network error. Malformed API root or resource path?");
    } finally {
      rest.close();
    }

    return response.equals("true") ? remoteId : -1;
  }


  /**
   * Purge all tasks in the remote web service.
   *
   * @return The number of deleted rows. 0 if no records were deleted.
   * @throws IOException Thrown when there is a network issue, as well as when the offline mode is
   *         active.
   * @throws JSONException Thrown when the reply from the web service is not a valid JSON string.
   */
  public static long purge() throws JSONException, IOException {
    long deletedRows = 0;

    List<RemoteTodo> todos = findAll();
    for (RemoteTodo todo : todos) {
      if (todo.delete() > 0)
        deletedRows++;
    }

    return deletedRows;
  }


  /**
   * Builds the JSON request payload to send to the web service.
   *
   * @return The JSON object to send to the web service.
   */
  private JSONObject buildRequestPayload() {
    JSONObject payload = new JSONObject();

    try {
      payload.put("id", remoteId);
      payload.put("name", name);
      payload.put("description", description);
      payload.put("expiry", expiry.getTime());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return payload;
  }

}
