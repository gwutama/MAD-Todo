package com.utama.madtodo.tasks;

import java.io.IOException;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.models.LocalRemoteTodo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


/**
 * The Class DeleteAsync represents an asynchronous operation for deleting both local and
 * remote todo tasks.
 */
public final class DeleteAsync extends AsyncTask<Long, Void, Integer> {

  /** For debugging purposes. */
  private static final String TAG = "DeleteTask";
  
  /** The context. This can be an activity or fragment. */
  private Context context;


  /**
   * Instantiates a new delete async.
   *
   * @param context The context. This can be an activity or fragment.
   */
  public DeleteAsync(Context context) {
    super();
    this.context = context;
  }


  /**
   * The actual operation that runs in background. This will delete both local and 
   * remote todo tasks.
   * 
   * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
   */
  @Override
  protected Integer doInBackground(Long... params) {
    Log.d(TAG, "doInBackground");
    long id = params[0];
    LocalRemoteTodo todo = LocalRemoteTodo.findOne(id);

    try {
      long deletedId = todo.delete();
      if (todo != null && deletedId > -1)
        return R.string.delete_success;
      else
        return R.string.delete_general_error;
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return R.string.delete_invalid_id_error;      
    } catch (IOException e) {
      e.printStackTrace();
      return R.string.network_error;
    }
  }


  /**
   * After the execution ends, the app will show a toast notification message and return back 
   * to {@link TodoListActivity} regardless of where the user is.
   * 
   * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
   */
  @Override
  protected void onPostExecute(Integer result) {
    super.onPostExecute(result);
    Toast.makeText(context, context.getString(result), Toast.LENGTH_SHORT).show();

    if (result == R.string.delete_success || result == R.string.network_error)
      context.startActivity(new Intent(context, TodoListActivity.class));
  }

}
