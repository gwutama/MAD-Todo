package com.utama.madtodo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


public class SimpleRestClient {

  private HttpURLConnection conn;
  private URL apiRoot;
  private URL resourceUrl;
  private String requestMethod;


  public SimpleRestClient(URL apiRoot, String requestMethod) {
    this.apiRoot = apiRoot;
    this.requestMethod = requestMethod;
    this.resourceUrl = apiRoot;
  }
  
  
  public void setPath(String path) throws MalformedURLException {
    resourceUrl = new URL(apiRoot.toString() + path);
  }
  
  
  public void open() throws IOException {
    if (conn != null)
      conn.disconnect();
      
    conn = (HttpURLConnection) resourceUrl.openConnection();
    conn.setRequestMethod(requestMethod);
    conn.addRequestProperty("Accept", "application/json");
    conn.addRequestProperty("Content-type", "application/json; charset=UTF-8");
  }

  
  public void close() {
    if (conn != null)
      conn.disconnect();
  }
  
  
  public String read() throws IOException {
    if (conn == null)
      throw new IOException("HTTP connection is not open");

    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String inputLine = null;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null)
      response.append(inputLine);

    in.close();

    return response.toString();    
  }
  
  
  public JSONObject readJson() throws IOException, JSONException {
    if (conn == null)
      throw new IOException("HTTP connection is not open");

    String response = read();
    JSONObject json = new JSONObject(response);
    return json;
  }
  
  
  public void write(String body) throws IOException {
    if (conn == null)
      throw new IOException("HTTP connection is not open");

    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    wr.write(body.toString());
    wr.flush();
  }  


  public void write(JSONObject body) throws IOException {
    if (conn == null)
      throw new IOException("HTTP connection is not open");
    
    write(body.toString());
  }  

}
