package com.utama.madtodo;

import java.io.IOException;

import org.json.JSONException;

import com.utama.madtodo.models.DbHelper;
import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class TodoListActivity extends Activity {

  private static final String TAG = "TodoListActivity";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_todo_list);    
    DbHelper.setupPersistance(this);
  }

  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.list, menu);
    return true;
  }
  
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      Log.d(TAG, "onOptionsItemSelected: action_settings");
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    } else if (id == R.id.action_create) {
      Log.d(TAG, "onOptionsItemSelected: action_create");
      startActivity(new Intent(this, CreateActivity.class));
      return true;
    } else if (id == R.id.action_sync) {
      new SyncAsync().execute();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  private final class SyncAsync extends AsyncTask<Void, Void, Integer> {

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
      Toast.makeText(TodoListActivity.this, R.string.synchronizing, Toast.LENGTH_SHORT).show();      
    }
    
    
    @Override
    protected void onPostExecute(Integer result) {
      super.onPostExecute(result);      
      Toast.makeText(TodoListActivity.this, getString(result), Toast.LENGTH_SHORT).show();      
      
      if (result == R.string.synchronization_success) {
        TodoListFragment fragment =
            (TodoListFragment) getFragmentManager().findFragmentById(R.id.todoListFragment);
        fragment.forceRefreshList();
      }
    }

  }

}
