package com.utama.madtodo.tasks;

import java.io.IOException;

import org.json.JSONException;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.fragments.TodoListFragment;
import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


/**
 * The Class SyncAsync represents an asynchronous task for synchronizinng local and remote data.
 */
public class SyncAsync extends AsyncTask<Void, Void, Integer> {

  /** For debugging purposes. */
  private static final String TAG = "SyncAsync";
  
  /** The context. This can be an activity or fragment. */
  private final Context context;


  /**
   * Instantiates a new sync async.
   *
   * @param context The context. This can be an activity or fragment.
   */
  public SyncAsync(Context context) {
    super();
    this.context = context;
  }


  /**
   * The actual application that runs in background. This will synchronize local and remote data.
   * 
   * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
   */
  @Override
  protected Integer doInBackground(Void... params) {
    Log.d(TAG, "SyncAsync.doInBackground");

    try {
      LocalRemoteTodo.sync();
      return R.string.synchronization_success;
    } catch (IOException e) {
      e.printStackTrace();
      return R.string.network_error;
    } catch (JSONException e) {
      e.printStackTrace();
      return R.string.response_error;
    }
  }


  /**
   * Before executing the lengthy operation, make sure that a toast notification is displayed 
   * ("syncing with server" or some sort).
   * 
   * @see android.os.AsyncTask#onPreExecute()
   */
  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    Toast.makeText(context, R.string.synchronizing, Toast.LENGTH_SHORT).show();
  }


  /**
   * After the execution ends, display a toast notification message. On a successful or failure
   * sync, make sure that the todo list gets refreshed. On a failure sync, the local data will
   * not be rolled back. Thus it still make sense to refresh the list since the local data
   * has been deleted anyway.
   * 
   * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
   */
  @Override
  protected void onPostExecute(Integer result) {
    super.onPostExecute(result);
    Toast.makeText(context, context.getString(result), Toast.LENGTH_SHORT).show();

    if (result == R.string.network_error)
      LocalRemoteTodo.switchToOfflineMode((Activity) context);        
    
    if ((result == R.string.synchronization_success || result == R.string.network_error)
        && context instanceof TodoListActivity) {
      TodoListActivity activity = (TodoListActivity) context;
      FragmentManager fragmentManager = activity.getFragmentManager();
      TodoListFragment fragment =
          (TodoListFragment) fragmentManager.findFragmentById(R.id.todoListFragment);
      fragment.forceRefreshList();
    }
  }

}
