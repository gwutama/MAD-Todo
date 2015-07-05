package com.utama.madtodo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

import com.utama.madtodo.model.DbHelper;
import com.utama.madtodo.model.LocalRemoteTodo;
import com.utama.madtodo.model.LocalTodo;
import com.utama.madtodo.model.RemoteTodo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class ListActivity extends Activity {

  private static final String TAG = "ListActivity";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    
    try {
      SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
      URL apiRoot = new URL(pref.getString("apiRoot", ""));
      RemoteTodo.setApiRoot(apiRoot);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      Toast.makeText(this, R.string.api_root_error, Toast.LENGTH_SHORT).show();
    }
    
    LocalTodo.setDbHelper(new DbHelper(this));    
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
      Toast.makeText(ListActivity.this, R.string.synchronizing, Toast.LENGTH_LONG).show();      
    }
    
    
    @Override
    protected void onPostExecute(Integer result) {
      super.onPostExecute(result);
      Toast.makeText(ListActivity.this, getString(result), Toast.LENGTH_SHORT).show();      
    }

  }

}
