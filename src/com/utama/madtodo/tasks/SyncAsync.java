package com.utama.madtodo.tasks;

import java.io.IOException;

import org.json.JSONException;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.TodoListFragment;
import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class SyncAsync extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = "SyncAsync";
  private final Context context;


  public SyncAsync(Context context) {
    this.context = context;
  }


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


  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    Toast.makeText(context, R.string.synchronizing, Toast.LENGTH_SHORT).show();
  }


  @Override
  protected void onPostExecute(Integer result) {
    super.onPostExecute(result);
    Toast.makeText(context, context.getString(result), Toast.LENGTH_SHORT).show();

    if (result == R.string.synchronization_success || result == R.string.network_error) {
      TodoListActivity activity = (TodoListActivity) context;
      FragmentManager fragmentManager = activity.getFragmentManager();
      TodoListFragment fragment =
          (TodoListFragment) fragmentManager.findFragmentById(R.id.todoListFragment);
      fragment.forceRefreshList();
    }
  }

}
