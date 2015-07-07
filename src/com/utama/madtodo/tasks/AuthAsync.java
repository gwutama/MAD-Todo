package com.utama.madtodo.tasks;

import java.io.IOException;

import com.utama.madtodo.LoginActivity;
import com.utama.madtodo.R;
import com.utama.madtodo.SettingsActivity;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.models.RemoteUser;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;


public class AuthAsync extends AsyncTask<Void, Void, Integer> {

  private final Context context;
  private final RemoteUser user;


  public AuthAsync(Context context, RemoteUser user) {
    super();
    this.context = context;
    this.user = user;
  }


  @Override
  protected Integer doInBackground(Void... params) {
    try {
      if (user.auth())
        return R.string.auth_success;
      else
        return R.string.auth_failure;
    } catch (IOException e) {
      e.printStackTrace();
      return R.string.auth_network_error;
    }
  }


  @Override
  protected void onPostExecute(Integer result) {
    Toast.makeText(context, context.getString(result), Toast.LENGTH_SHORT).show();

    if (context instanceof LoginActivity) {
      LoginActivity activity = (LoginActivity) context;

      if (result == R.string.auth_success)
        activity.finish();

      activity.showProgress(false);
    }

    switch (result) {
      case R.string.auth_success:
        context.startActivity(new Intent(context, TodoListActivity.class));
        break;
      case R.string.auth_network_error:
        context.startActivity(new Intent(context, SettingsActivity.class));
        break;
    }
  }


  @Override
  protected void onCancelled() {
    if (context instanceof LoginActivity)
      ((LoginActivity) context).showProgress(false);
  }
}
