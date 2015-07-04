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
  
  
  public RemoteTodo(String jsonStr) {
    super();
    try {
      JSONObject jsonObj = new JSONObject(jsonStr);
      setFromJsonObject(jsonObj);
    } catch (JSONException e) {
      id = -1;
      e.printStackTrace();
    }
  }


  public RemoteTodo(JSONObject jsonObj) {
    super();
    setFromJsonObject(jsonObj);
  }


  private void setFromJsonObject(JSONObject jsonObj) {
    try {
      id = jsonObj.getLong("id");
      name = jsonObj.getString("name");
      description = jsonObj.getString("description");
      expiry = new Date(jsonObj.getLong("expiry"));
    } catch (JSONException e) {
      id = -1;
      e.printStackTrace();
    }
  }


  public static URL getApiRoot() {
    return apiRoot;
  }


  public static void setApiRoot(URL apiRoot) {
    RemoteTodo.apiRoot = apiRoot;
  }


  public static List<RemoteTodo> findAll()
      throws MalformedURLException, IOException, JSONException {
    HttpURLConnection conn = null;
    conn = openConnection("GET", apiRoot);
    String resp = readResponse(conn);
    List<RemoteTodo> todos = buildEntitiesFromJsonString(resp);

    if (conn != null) {
      conn.disconnect();
    }

    return todos;
  }


  private static List<RemoteTodo> buildEntitiesFromJsonString(String jsonStr) throws JSONException {
    JSONArray jsonArr = new JSONArray(jsonStr);
    List<RemoteTodo> ret = new ArrayList<RemoteTodo>();

    for (int i = 0; i < jsonArr.length(); i++) {
      JSONObject jsonObj = jsonArr.getJSONObject(i);
      RemoteTodo todo = new RemoteTodo(jsonObj);
      ret.add(todo);
    }

    return ret;
  }


  public static RemoteTodo findOne(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  
  // TODO This is just temporary until update() is implemented
  @Override
  public long save() {
    return create();
  }
  

  @Override
  protected long create() {
    if (TextUtils.isEmpty(name)) {
      throw new IllegalArgumentException("Task name cannot be empty");
    }

    HttpURLConnection conn = null;
    String resp;

    try {
      conn = openConnection("POST", apiRoot);
      writeRequestBody(conn, buildRequestPayload().toString());
      resp = readResponse(conn);
      setFromJsonObject(new JSONObject(resp));
    } catch (JSONException e) {
      e.printStackTrace();
      return -1;
    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }

    if (conn != null) {
      conn.disconnect();
    }

    return id;
  }


  @Override
  protected long update() {
    // TODO Auto-generated method stub
    return 0;
  }


  @Override
  public long delete() {
    HttpURLConnection conn = null;
    URL url;
    String resp;

    try {
      url = new URL(apiRoot + "/" + Long.toString(id));
      conn = openConnection("DELETE", url);
      resp = readResponse(conn);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return 0;
    } catch (IOException e) {
      e.printStackTrace();
      return 0;
    }

    if (conn != null) {
      conn.disconnect();
    }

    if (resp.equals("true"))
      return 1;
    else
      return 0;
  }


  private static HttpURLConnection openConnection(String requestMethod, URL url)
      throws IOException {
    Log.d(TAG, url.toString());

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
    payload.put("id", id);
    payload.put("name", name);
    payload.put("description", description);
    payload.put("expiry", expiry.getTime());
    return payload;
  }

}
