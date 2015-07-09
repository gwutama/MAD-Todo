package com.utama.madtodo.tasks;

import java.io.IOException;

import org.json.JSONException;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


/**
 * The Class SaveAsync represents an asynchronous operation for saving (creating new and updating
 * existing) both local and remote todo tasks.
 * 
 * If the passed id {@link LocalRemoteTodo} instance is not set or if it equals to -1, then
 * the data will be created. Otherwise the data will be saved.
 */
public class SaveAsync extends AsyncTask<LocalRemoteTodo, Void, Integer> {

  /** For debugging purposes. */
  private static final String TAG = "CreateTask";
  
  /** The context. This can be an activity or fragment. */
  protected final Context context;


  /**
   * Instantiates a new save async.
   *
   * @param context The context. This can be an activity or fragment.
   */
  public SaveAsync(Context context) {
    super();
    this.context = context;
  }


  /**
   * The actual operation that runs in background. This will either create or update existing
   * local and remote todo tasks.
   * 
   * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
   */
  @Override
  protected Integer doInBackground(LocalRemoteTodo... params) {
    Log.d(TAG, "doInBackground");

    try {
      LocalRemoteTodo todo = params[0];

      if (todo.save() > -1)
        return R.string.edit_success;
      else
        return R.string.edit_general_error;
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return R.string.edit_name_empty_error;
    } catch (IOException e) {
      e.printStackTrace();
      return R.string.network_error;
    } catch (JSONException e) {
      e.printStackTrace();
      return R.string.response_error;
    }
  }


  /**
   * After the execution ends, a toast notification message will be shown. Finally the user will
   * be redirected to the {@link TodoListActivity} activity on success.
   * 
   * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
   */
  @Override
  protected void onPostExecute(Integer result) {
    super.onPostExecute(result);
    Toast.makeText(context, context.getString(result), Toast.LENGTH_SHORT).show();

    if (result == R.string.network_error)
      LocalRemoteTodo.switchToOfflineMode((Activity) context);    
    
    if (result == R.string.edit_success || result == R.string.network_error)
      context.startActivity(new Intent(context, TodoListActivity.class));
  }

}
