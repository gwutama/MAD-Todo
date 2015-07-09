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


/**
 * The Class SimpleRestClient represents a simple HTTP client that talks with a JSON-based
 * REST web service.
 */
public class SimpleRestClient {

  /** Track the connection. */
  private HttpURLConnection conn;
  
  /** The base URL of the web service. */
  private URL apiRoot;
  
  /** The complete path to the web service resource. */
  private URL resourceUrl;
  
  /** The request method. */
  private String requestMethod;
  
  /** The connection timeout in ms. */
  private static int CONNECT_TIMEOUT_MS = 10000;


  /**
   * Instantiates a new simple rest client.
   *
   * @param apiRoot The base URL of the web service
   * @param requestMethod The request method (GET, POST, PUT, DELETE, etc).
   */
  public SimpleRestClient(URL apiRoot, String requestMethod) {
    this.apiRoot = apiRoot;
    this.requestMethod = requestMethod;
    this.resourceUrl = apiRoot;
  }
  
  
  /**
   * Sets the path of a web service resource.
   *
   * @param path The path to the web service resource. Not that only the path must be passed here
   *             and not the complete URL.
   * @throws MalformedURLException Thrown if the combination of API root (passed in the constructor)
   *                               and the passed resource path do not build a valid URL.
   */
  public void setPath(String path) throws MalformedURLException {
    resourceUrl = new URL(apiRoot.toString() + path);
  }
  
  
  /**
   * Open HTTP connection to the web service API root.
   *
   * @throws IOException Thrown when the HTTP connection cannot be opened (network error).
   */
  public void open() throws IOException {
    if (conn != null)
      conn.disconnect();
      
    conn = (HttpURLConnection) resourceUrl.openConnection();
    conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
    conn.setRequestMethod(requestMethod);
    conn.addRequestProperty("Accept", "application/json");
    conn.addRequestProperty("Content-type", "application/json; charset=UTF-8");
  }

  
  /**
   * Close the connection.
   */
  public void close() {
    if (conn != null)
      conn.disconnect();
  }
  
  
  /**
   * Read the response from the server.
   *
   * @return Response as string.
   * @throws IOException Thrown when the HTTP response cannot be read (connection was not open).
   */
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
  
  
  /**
   * Read the response from the server as a JSON data.
   *
   * @return Ahe JSON object
   * @throws IOException Thrown when the HTTP response cannot be read (connection was not open).
   * @throws JSONException Thrown when the response is not a valid JSON data.
   */
  public JSONObject readJson() throws IOException, JSONException {
    if (conn == null)
      throw new IOException("HTTP connection is not open");

    String response = read();
    JSONObject json = new JSONObject(response);
    return json;
  }
  
  
  /**
   * Write request body to the server.
   *
   * @param body The request body as string.
   * @throws IOException Thrown when the request body cannot be written (connection was not open).
   */
  public void write(String body) throws IOException {
    if (conn == null)
      throw new IOException("HTTP connection is not open");

    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    wr.write(body.toString());
    wr.flush();
  }  


  /**
   * Write JSON request body to the server.
   *
   * @param body The request body as JSON data.
   * @throws IOException Thrown when the request body cannot be written (connection was not open).
   */
  public void write(JSONObject body) throws IOException {
    if (conn == null)
      throw new IOException("HTTP connection is not open");
    
    write(body.toString());
  }  

}
