package com.utama.madtodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class ListActivity extends Activity {

  private static final String TAG = "ListActivity";
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
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
    }
    else if (id == R.id.action_create) {
      Log.d(TAG, "onOptionsItemSelected: action_create");
      startActivity(new Intent(this, CreateActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
