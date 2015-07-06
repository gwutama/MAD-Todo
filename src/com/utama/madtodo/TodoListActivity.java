package com.utama.madtodo;

import com.utama.madtodo.models.DbConsts;
import com.utama.madtodo.models.DbHelper;
import com.utama.madtodo.tasks.SyncAsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class TodoListActivity extends Activity {

  static final String TAG = "TodoListActivity";
  private TodoListFragment todoListFragment;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_todo_list);
    DbHelper.setupPersistance(this);

    todoListFragment =
        (TodoListFragment) getFragmentManager().findFragmentById(R.id.todoListFragment);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.todolist, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        Log.d(TAG, "onOptionsItemSelected: action_settings");
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
      case R.id.action_create:
        Log.d(TAG, "onOptionsItemSelected: action_create");
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
