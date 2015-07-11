package com.utama.madtodo.tasks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.utama.madtodo.LoginActivity;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.utils.SimpleRestClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;


/**
 * The Class TestConnectionAsync represents an asynchronous operation for testing connection with
 * the web service.
 */
public class TestConnectionAsync extends AsyncTask<Void, Void, Boolean> {

  /** The context. This can be an activity or fragment. */
  Context context;


  /**
   * Instantiates a new test connection async.
   *
   * @param context The context. This can be an activity or fragment.
   */
  public TestConnectionAsync(Context context) {
    this.context = context;
  }


  /**
   * The actual operation that runs in background. This will try to connect with the web service and
   * returns various message on different results.
   * 
   * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
   */
  @Override
  protected Boolean doInBackground(Void... params) {
    SimpleRestClient rest = null;
    boolean canContactWebService = false;

    try {
      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
      URL apiRoot = new URL(pref.getString("apiRoot", ""));
      rest = new SimpleRestClient(apiRoot, "GET");
      rest.open();
      rest.getResponseCode(); //throws IOException on network error
      canContactWebService = true;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (rest != null)
        rest.close();
    }

    return canContactWebService;
  }


  /**
   * After the execution ends, the login progress bar (from the {@link LoginActivity}) will be
   * shown. If the app has connection with the remote web service, it will try to automatically logs
   * in the user (the email and password are retrieved from the app preferences). Otherwise, it
   * means that the app does not have connection with the remote web service. In this case, enable
   * the offline mode and start the {@link TodoListActivity}.
   * 
   * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
   */
  @Override
  protected void onPostExecute(Boolean canContactWebService) {
    super.onPostExecute(canContactWebService);

    if (context instanceof LoginActivity) {
      LoginActivity activity = (LoginActivity) context;
      activity.showTestConnectionProgress(false);      

      if (canContactWebService && !activity.getLoginFailed()) {
        activity.attemptAutoLogin();
      } else if (!canContactWebService) {
        LocalRemoteTodo.switchToOfflineMode(activity);
        activity.startActivity(new Intent(activity, TodoListActivity.class));
      }
    }
  }

}
