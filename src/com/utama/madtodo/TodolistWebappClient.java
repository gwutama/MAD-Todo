package com.utama.madtodo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;


public class TodolistWebappClient {

  private static final String TAG = "TodolistWebappClient";
  private String apiRoot;


  public TodolistWebappClient(String apiRoot) {
    setApiRoot(apiRoot);
  }


  public String getApiRoot() {
    return apiRoot;
  }


  public void setApiRoot(String apiRoot) {
    this.apiRoot = apiRoot;
  }


  public String readAllTodos() throws MalformedURLException, IOException {
    HttpURLConnection conn = null;
    String resp = null;

    conn = buildHttpConnection("GET", new URL(apiRoot));
    resp = readHttpResponse(conn);

    if (conn != null) {
      conn.disconnect();
    }

    return resp;
  }


  public String createTodo(String summary, String description, Date dueDateTime)
      throws MalformedURLException, IOException, IllegalArgumentException {
    if (TextUtils.isEmpty(summary)) {
      throw new IllegalArgumentException("Task summary cannot be empty");
    }

    HttpURLConnection conn = null;
    String resp = null;

    conn = buildHttpConnection("POST", new URL(apiRoot));

    JSONObject body = new JSONObject();
    try {
      body.put("name", summary);
      body.put("description", description);
      body.put("expiry", dueDateTime.getTime() / 1000);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Log.d(TAG, body.toString());

    writeRequestBody(conn, body.toString());
    resp = readHttpResponse(conn);

    if (conn != null) {
      conn.disconnect();
    }

    return resp;
  }


  public String deleteTodo(long id) throws MalformedURLException, IOException {
    HttpURLConnection conn = null;
    String resp = null;

    conn = buildHttpConnection("DELETE", new URL(apiRoot + "/" + Long.toString(id)));
    resp = readHttpResponse(conn);

    if (conn != null) {
      conn.disconnect();
    }

    return resp;
  }


  private HttpURLConnection buildHttpConnection(String requestMethod, URL apiRoot)
      throws IOException {
    HttpURLConnection conn = null;

    Log.d(TAG, apiRoot.toString());
    
    conn = (HttpURLConnection) apiRoot.openConnection();
    conn.setRequestMethod(requestMethod);
    conn.addRequestProperty("Accept", "application/json");
    conn.addRequestProperty("Content-type", "application/json; charset=UTF-8");

    return conn;
  }


  private String readHttpResponse(HttpURLConnection conn) throws IOException {
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
}
