package com.utama.madtodo.models;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.utama.madtodo.utils.SimpleRestClient;


/**
 * The Class RemoteUser represents a class for working with users related operations of the web
 * service.
 */
public class RemoteUser {

  /** The user's email address. */
  private String email;

  /** The user's password. */
  private String password;

  /** The Constant RESOURCE_PATH represents the web service API path for managing users. */
  private static final String RESOURCE_PATH = "/users";

  /** The API root to the web service. */
  private static URL apiRoot;

  /** The Constant PASSWORD_MIN_LENGTH represents the minimum length of user's password. */
  private static final int PASSWORD_MIN_LENGTH = 6;

  /** The Constant PASSWORD_MIN_LENGTH represents the maximum length of user's password. */
  private static final int PASSWORD_MAX_LENGTH = 6;

  /**
   * The Constant EMAIL_ADDRESS_REGEX represents the regular expression pattern for validating
   * user's email address.
   */
  private static final Pattern EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


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
   * @param apiRoot The new API root.
   */
  public static void setApiRoot(URL apiRoot) {
    RemoteUser.apiRoot = apiRoot;
  }


  /**
   * Gets the email.
   *
   * @return User's email.
   */
  public String getEmail() {
    return email;
  }


  /**
   * Sets the email.
   *
   * @param user The new email.
   */
  public void setEmail(String user) {
    this.email = user;
  }


  /**
   * Gets the password.
   *
   * @return User's password.
   */
  public String getPassword() {
    return password;
  }


  /**
   * Sets the password.
   *
   * @param password The new password.
   */
  public void setPassword(String password) {
    this.password = password;
  }


  /**
   * Instantiates a new remote user.
   */
  public RemoteUser() {}


  /**
   * Instantiates a new remote user.
   *
   * @param remoteUser the remote user
   */
  public RemoteUser(RemoteUser remoteUser) {
    email = remoteUser.getEmail();
    password = remoteUser.getPassword();
  }


  /**
   * Checks if email is valid.
   *
   * @param email The email address.
   * @return True if email is valid. False otherwise.
   */
  public boolean isEmailValid(String email) {
    Matcher matcher = EMAIL_ADDRESS_REGEX.matcher(email);
    return matcher.find();
  }


  /**
   * Checks if is password valid.
   * 
   * Currently this only checks whether the password length is between the minimum and maximum
   * lengths.
   *
   * @param password The password.
   * @return True if is password valid. False otherwise.
   */
  public boolean isPasswordValid(String password) {
    int length = password.length();
    return (length >= PASSWORD_MIN_LENGTH && length <= PASSWORD_MAX_LENGTH);
  }


  /**
   * Authenticate user using the web service.
   *
   * @return True if authentication was successful. False otherwise.
   * @throws IOException Thrown when there is a network issue, as well as when the offline mode is
   *         active.
   */
  public boolean auth() throws IOException {
    SimpleRestClient rest = new SimpleRestClient(apiRoot, "PUT");
    String response = null;

    try {
      rest.setPath(RESOURCE_PATH + "/auth");
      rest.open();
      rest.write(buildRequestPayload());
      response = rest.read();
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IOException("Network error. Malformed API root or resource path?");
    } finally {
      rest.close();
    }

    return response.equals("true");
  }


  /**
   * Builds the JSON request payload to send to the web service.
   *
   * @return The JSON object to send to the web service.
   */
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
