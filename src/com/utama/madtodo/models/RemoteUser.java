package com.utama.madtodo.models;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.utama.madtodo.utils.SimpleRestClient;


public class RemoteUser {

  private String email;
  private String password;

  private static final String RESOURCE_PATH = "/users";
  private static URL apiRoot;

  private static final int PASSWORD_MIN_LENGTH = 6;
  private static final int PASSWORD_MAX_LENGTH = 6;
  private static final Pattern EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


  public static URL getApiRoot() {
    return apiRoot;
  }


  public static void setApiRoot(URL apiRoot) {
    RemoteUser.apiRoot = apiRoot;
  }


  public String getEmail() {
    return email;
  }


  public void setEmail(String user) {
    this.email = user;
  }


  public String getPassword() {
    return password;
  }


  public void setPassword(String password) {
    this.password = password;
  }


  public RemoteUser() {}


  public RemoteUser(RemoteUser remoteUser) {
    email = remoteUser.getEmail();
    password = remoteUser.getPassword();
  }


  public boolean isEmailValid(String email) {
    Matcher matcher = EMAIL_ADDRESS_REGEX.matcher(email);
    return matcher.find();
  }


  public boolean isPasswordValid(String password) {
    int length = password.length();
    return (length >= PASSWORD_MIN_LENGTH && length <= PASSWORD_MAX_LENGTH);
  }


  public boolean auth() throws IOException {
    SimpleRestClient rest = new SimpleRestClient(apiRoot, "PUT");
    String response = null;

    try {
      rest.setPath(RESOURCE_PATH + "/auth");
      rest.open();
      rest.write(buildRequestPayload());
      response = rest.read();
    } finally {
      rest.close();
    }

    return response.equals("true");
  }


  private JSONObject buildRequestPayload() {
    JSONObject payload = new JSONObject();

    try {
      payload.put("email", email);
      payload.put("pwd", password);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return payload;
  }
}
