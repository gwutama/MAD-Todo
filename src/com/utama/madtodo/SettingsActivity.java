package com.utama.madtodo;

import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SettingsActivity extends Activity {

  @Override
  public void onBackPressed() {
    // Try to re-login on back button click if offline mode is off.
    // Always assume that API root has been changed.
    // Otherwise just start todo list activity.
    if (LocalRemoteTodo.isOfflineMode())
      startActivity(new Intent(this, TodoListActivity.class));
    else
      startActivity(new Intent(this, LoginActivity.class));
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
  }

}
