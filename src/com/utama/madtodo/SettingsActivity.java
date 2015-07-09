package com.utama.madtodo;

import com.utama.madtodo.models.LocalRemoteTodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * The Class SettingsActivity represents an activity for modifying app preferences.
 */
public class SettingsActivity extends Activity {

  /**
   * Try to re-login on back button click if offline mode is off.
   *  Always assume that API root has been changed. Otherwise just start todo list activity.
   *  
   * @see android.app.Activity#onBackPressed()
   */
  @Override
  public void onBackPressed() {
    LocalRemoteTodo.setupPersistence(this);
    
    if (LocalRemoteTodo.isOfflineMode())
      startActivity(new Intent(this, TodoListActivity.class));
    else
      startActivity(new Intent(this, LoginActivity.class));
  }


  /**
   * Inflates activity_settings.xml.
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
  }

}
