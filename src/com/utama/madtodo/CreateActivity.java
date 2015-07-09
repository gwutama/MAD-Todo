package com.utama.madtodo;

import com.utama.madtodo.fragments.CreateFragment;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.tasks.SaveAsync;

import android.os.Bundle;
import android.view.MenuItem;


public class CreateActivity extends SingleTodoActivity implements ActionSaveable {

  CreateFragment createFragment;


  @Override
  public void onBackPressed() {
    finish();
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create);
    createFragment = (CreateFragment) getFragmentManager().findFragmentById(R.id.createFragment);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        LocalRemoteTodo todo = createFragment.buildTodo();
        if (todo != null) {
          new SaveAsync(this).execute(todo);
          return true;
        }
        return false;
    }

    return super.onOptionsItemSelected(item);
  }

}
