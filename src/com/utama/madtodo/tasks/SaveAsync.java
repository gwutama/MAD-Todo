package com.utama.madtodo.tasks;

import java.io.IOException;

import org.json.JSONException;

import com.utama.madtodo.R;
import com.utama.madtodo.TodoListActivity;
import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class SaveAsync extends AsyncTask<LocalRemoteTodo, Void, Integer> {

  private static final String TAG = "CreateTask";
  private final Fragment fragment;


  public SaveAsync(Fragment fragment) {
    super();
    this.fragment = fragment;
  }


  @Override
  protected Integer doInBackground(LocalRemoteTodo... params) {
    Log.d(TAG, "doInBackground");

    try {
      LocalRemoteTodo todo = params[0];

      if (todo.save() > 0)
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


  @Override
  protected void onPostExecute(Integer result) {
    super.onPostExecute(result);
    Toast.makeText(fragment.getActivity(), fragment.getString(result), Toast.LENGTH_SHORT).show();

    if (result == R.string.edit_success)
      fragment.startActivity(new Intent(fragment.getActivity(), TodoListActivity.class));
  }

}
