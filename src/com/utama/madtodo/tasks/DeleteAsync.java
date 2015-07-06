package com.utama.madtodo.tasks;

import java.io.IOException;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public final class DeleteAsync extends AsyncTask<Long, Void, Integer> {

  private static final String TAG = "DeleteTask";
  private Fragment fragment;


  public DeleteAsync(Fragment fragment) {
    super();
    this.fragment = fragment;
  }


  @Override
  protected Integer doInBackground(Long... params) {
    Log.d(TAG, "doInBackground");
    long id = params[0];
    LocalRemoteTodo todo = LocalRemoteTodo.findOne(id);

    try {
      long count = todo.delete();
      if (todo != null && count > 0)
        return R.string.delete_success;
      else
        return R.string.delete_general_error;
    } catch (IOException e) {
      e.printStackTrace();
      return R.string.network_error;
    }
  }


  @Override
  protected void onPostExecute(Integer result) {
    super.onPostExecute(result);
    Toast.makeText(fragment.getActivity(), fragment.getString(result), Toast.LENGTH_SHORT).show();

    if (result == R.string.delete_success)
      fragment.startActivity(new Intent(fragment.getActivity(), TodoListActivity.class));
  }

}
