package com.utama.madtodo.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;


public class RemoteTodo extends TodoEntity {

  private static final String TAG = "RemoteTodo";
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


  public static List<RemoteTodo> findAll() throws JSONException, IOException {
    HttpURLConnection conn = null;
    conn = openConnection("GET", apiRoot);
    String resp = readResponse(conn);
    List<RemoteTodo> todos = buildEntitiesFromJson(resp);

    if (conn != null) {
      conn.disconnect();
    }

    return todos;
  }


  private static List<RemoteTodo> buildEntitiesFromJson(String json) throws JSONException {
    JSONArray jsonArr = new JSONArray(json);
    List<RemoteTodo> ret = new ArrayList<RemoteTodo>();

    for (int i = 0; i < jsonArr.length(); i++) {
      JSONObject jsonObj = jsonArr.getJSONObject(i);
      RemoteTodo todo = new RemoteTodo(jsonObj);
      ret.add(todo);
    }

    return ret;
  }


  public static RemoteTodo findOne(long remoteId) throws IOException {
    HttpURLConnection conn = null;
    RemoteTodo todo = null;

    try {
      URL url = new URL(apiRoot + "/" + Long.toString(remoteId));
      conn = openConnection("GET", url);
      String resp = readResponse(conn);
      todo = new RemoteTodo(resp);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IOException("Network error. Malformed URL?");  
    } finally {
      if (conn != null)
        conn.disconnect();
    }

    return todo;
  }


  @Override
  protected long create() throws IOException, JSONException {
    if (TextUtils.isEmpty(name)) {
      throw new IllegalArgumentException("Task name cannot be empty");
    }

    HttpURLConnection conn = null;

    try {
      conn = openConnection("POST", apiRoot);
      writeRequestBody(conn, buildRequestPayload().toString());
      String resp = readResponse(conn);
      setFromJsonObject(new JSONObject(resp));
    } finally {
      if (conn != null)
        conn.disconnect();
    }

    return remoteId;
  }


  @Override
  protected long update() throws IOException, JSONException {
    if (TextUtils.isEmpty(name)) {
      throw new IllegalArgumentException("Task name cannot be empty");
    }

    HttpURLConnection conn = null;

    try {
      conn = openConnection("PUT", apiRoot);
      writeRequestBody(conn, buildRequestPayload().toString());
      String resp = readResponse(conn);
      setFromJsonObject(new JSONObject(resp));
    } finally {
      if (conn != null)
        conn.disconnect();
    }

    return remoteId;
  }


  @Override
  public long delete() throws IOException {
    HttpURLConnection conn = null;
    String resp;

    try {
      URL url = new URL(apiRoot + "/" + Long.toString(remoteId));
      Log.d(TAG, "delete: " + url.toString());      
      conn = openConnection("DELETE", url);
      resp = readResponse(conn);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IOException("Network error. Malformed URL?");
    } finally {
      if (conn != null)
        conn.disconnect();
    }

    if (resp.equals("true"))
      return 1;
    else
      return 0;
  }


  public static long purge() throws JSONException, IOException {
    long deletedRows = 0;

    List<RemoteTodo> todos = findAll();
    for (RemoteTodo todo : todos) {
      if (todo.delete() > 0) {
        deletedRows++;
      }
    }

    return deletedRows;
  }


  private static HttpURLConnection openConnection(String requestMethod, URL url)
      throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod(requestMethod);
    conn.addRequestProperty("Accept", "application/json");
    conn.addRequestProperty("Content-type", "application/json; charset=UTF-8");

    return conn;
  }


  private static String readResponse(HttpURLConnection conn) throws IOException {
    if (conn == null)
      throw new IOException("HttpURLConnection is null");

    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String inputLine = null;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }

    in.close();

    return response.toString();
  }


  private void writeRequestBody(HttpURLConnection conn, String body) throws IOException {
    if (conn == null)
      throw new IOException("HttpURLConnection is null");

    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    wr.write(body.toString());
    wr.flush();
  }


  private JSONObject buildRequestPayload() throws JSONException {
    JSONObject payload = new JSONObject();
    payload.put("id", remoteId);
    payload.put("name", name);
    payload.put("description", description);
    payload.put("expiry", expiry.getTime());
    return payload;
  }

}
