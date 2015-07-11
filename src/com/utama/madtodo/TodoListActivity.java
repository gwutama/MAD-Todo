package com.utama.madtodo;

import com.utama.madtodo.fragments.TodoListFragment;
import com.utama.madtodo.models.DbConsts;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.tasks.SyncAsync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


/**
 * The Class TodoListActivity represents an activity for viewing list of tasks.
 */
public class TodoListActivity extends Activity {
  
  /** The todo list fragment. */
  private TodoListFragment todoListFragment;
  
  /** The login progress dialog. Will be shown during authentication. */
  private ProgressDialog saveProgress;  
  
  
  /**
   * The implementation is to close the application on back button press and make sure that
   * offline mode is disabled.
   * 
   * @see android.app.Activity#onBackPressed()
   */
  @Override
  public void onBackPressed() {
    LocalRemoteTodo.switchToOnlineMode(this);
    finishAffinity();
  }
  
  
  /**
   * Inflates activity_todo_list.xml and sets up {@link TodoListFragment} and the save progress
   * dialog.
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_todo_list);
    LocalRemoteTodo.setupPersistence(this);

    todoListFragment =
        (TodoListFragment) getFragmentManager().findFragmentById(R.id.todoListFragment);    
    
    // Progress dialogs
    saveProgress = new ProgressDialog(this);
    saveProgress.setTitle(R.string.app_name);
    saveProgress.setMessage("Saving");
    saveProgress.setIndeterminate(true);    
  }
  
  
  /**
   * Convenient method to show and hide save progress dialog.
   * 
   * @param show True to show the progress dialog. False otherwise.
   */
  public void showSaveProgressDialog(boolean show) {
    if (show)
      saveProgress.show();
    else
      saveProgress.hide();
  }
  
  
  /**
   * Inflates todolist.xml into menu.
   * 
   * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.todolist, menu);
    return true;
  }


  /**
   * Based on user menu selection, this method will decide whether:
   * - {@link SettingsActivity} needs to be shown (if settings menu was chosen)
   * - whether user needs to be redirected to {@link CreateActivity} (if "new task" menu was chosen)
   * - async task {@link SyncAsync} is to be executed (if user chooses to manually sync his data)
   * - Sort the todo list by importance+date or date+importance.
   * 
   * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
      case R.id.action_create:
        startActivity(new Intent(this, CreateActivity.class));
        return true;
      case R.id.action_sync:
        new SyncAsync(this).execute();
        return true;
      case R.id.action_sort_importance_date:
        item.setChecked(true);
        todoListFragment.setSortOrder(DbConsts.SORT_IMPORTANCE_DATE);
        todoListFragment.forceRefreshList();
        return true;
      case R.id.action_sort_date_importance:
        item.setChecked(true);
        todoListFragment.setSortOrder(DbConsts.SORT_DATE_IMPORTANCE);
        todoListFragment.forceRefreshList();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
