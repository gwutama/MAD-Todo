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


public class RemoteTodo extends TodoEntity {

  private static final String RESOURCE_PATH = "/todos";
  private static URL apiRoot;


  public RemoteTodo() {
    super();
  }


  public RemoteTodo(TodoEntity todo) {
    super(todo);
  }


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


  public RemoteTodo(JSONObject obj) {
    super();
    setFromJsonObject(obj);
  }


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


  public static URL getApiRoot() {
    return apiRoot;
  }


  public static void setApiRoot(URL apiRoot) {
    RemoteTodo.apiRoot = apiRoot;
  }


  public static List<RemoteTodo> findAll() throws IOException, JSONException {
    SimpleRestClient rest = new SimpleRestClient(apiRoot, "GET");
    List<RemoteTodo> todos;

    try {
      rest.setPath(RESOURCE_PATH);      
      rest.open();
      String resp = rest.read();
      todos = buildEntities(resp);
    } finally {
      rest.close();
    }

    return todos;
  }


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


  public static RemoteTodo findOne(long remoteId) throws IOException, JSONException {
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
    } finally {
      rest.close();
    }

    return remoteId;
  }


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
    } finally {
      rest.close();
    }

    return remoteId;
  }


  @Override
  public long delete() throws IOException {
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

    return response.equals("true") ? 1 : 0;
  }


  public static long purge() throws JSONException, IOException {
    long deletedRows = 0;

    List<RemoteTodo> todos = findAll();
    for (RemoteTodo todo : todos) {
      if (todo.delete() > 0)
        deletedRows++;
    }

    return deletedRows;
  }


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
