package com.utama.madtodo.tasks;

import java.io.IOException;

import com.utama.madtodo.LoginActivity;
import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.models.RemoteUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;


/**
 * The Class AuthAsync represents an asynchronous operation for authenticating user with the remote
 * web service.
 */
public class AuthAsync extends AsyncTask<Void, Void, Integer> {

  /** The context. This can be an activity or fragment. */
  private final Context context;

  /** The user instance to authenticate. */
  private final RemoteUser user;


  /**
   * Instantiates a new auth async.
   *
   * @param context The context. This can be an activity or fragment.
   * @param user The user instance to authenticate.
   */
  public AuthAsync(Context context, RemoteUser user) {
    super();
    this.context = context;
    this.user = user;
  }


  /**
   * The actual operation that runs in background. This will authenticate user with the remote web
   * service.
   * 
   * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
   */
  @Override
  protected Integer doInBackground(Void... params) {
    try {
      return user.auth() ? R.string.auth_success : R.string.auth_failure;
    } catch (IOException e) {
      e.printStackTrace();
      return R.string.auth_network_error;
    } catch (Exception e) {
      e.printStackTrace();
      return R.string.auth_network_error;
    }
  }


  /**
   * After the execution ends, all toast messages (login success or network error) will be shown
   * except when the authentication fails. In that case, the user will be redirected to the
   * {@link LoginActivity} back, where a much clearer error message will be shown.
   * 
   * On a successful authentication, the user will be redirected to the {@link TodoListActivity}
   * and {@link SyncAsync} will be executed.
   * 
   * If a network error occurs, the app will try to enable the offline mode. The user will then be
   * redirected to {@link TodoListActivity} and from then on, the application will work locally.
   * 
   * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
   */
  @Override
  protected void onPostExecute(Integer result) {
    if (result != R.string.auth_failure)
      Toast.makeText(context, context.getString(result), Toast.LENGTH_LONG).show();

    if (context instanceof LoginActivity) {
      LoginActivity activity = (LoginActivity) context;

      if (result == R.string.auth_success)
        activity.finish();

      activity.showLoginProgress(false);
    }

    switch (result) {
      case R.string.auth_success:
        new SyncAsync(context).execute();
        context.startActivity(new Intent(context, TodoListActivity.class));
        break;
      case R.string.auth_failure:
        context.startActivity(
            new Intent(context, LoginActivity.class).putExtra("isLoginFailure", true));
        break;
      case R.string.auth_network_error:
        // Cannot contact the web service. Enable offline mode and open todo list activity.
        LocalRemoteTodo.switchToOfflineMode((Activity) context);
        context.startActivity(new Intent(context, TodoListActivity.class));
        break;
    }
  }


  /**
   * Mak sure that the login progress in LoginActivity is closed. This should not happen since the
   * user cannot cancel the operation anyway.
   * 
   * @see android.os.AsyncTask#onCancelled()
   */
  @Override
  protected void onCancelled() {
    if (context instanceof LoginActivity)
      ((LoginActivity) context).showLoginProgress(false);
  }
}
