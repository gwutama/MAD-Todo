package com.utama.madtodo.tasks;

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


public class TestConnectionAsync extends AsyncTask<Void, Void, Boolean> {

  Context context;


  public TestConnectionAsync(Context context) {
    this.context = context;
  }


  @Override
  protected Boolean doInBackground(Void... params) {
    SimpleRestClient rest = null;
    boolean canContactWebService = false;

    try {
      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
      URL apiRoot = new URL(pref.getString("apiRoot", ""));
      rest = new SimpleRestClient(apiRoot, "GET");
      rest.open();
      canContactWebService = true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      rest.close();
    }

    return canContactWebService;
  }


  @Override
  protected void onPostExecute(Boolean canContactWebService) {
    super.onPostExecute(canContactWebService);    

    if (context instanceof LoginActivity) { 
      LoginActivity activity = (LoginActivity) context;
      activity.showLoginProgress(false);

      if (canContactWebService && !activity.getLoginFailed()) {
        activity.attemptAutoLogin();
      } else if (!canContactWebService){
        LocalRemoteTodo.switchToOfflineMode(activity);
        activity.startActivity(new Intent(activity, TodoListActivity.class));
      }
    }
  }

}
