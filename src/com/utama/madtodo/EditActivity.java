package com.utama.madtodo;

import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.tasks.SaveAsync;

import android.os.Bundle;
import android.view.MenuItem;


public class EditActivity extends CreateActivity implements ActionSaveable, ActionDeleteable {

  EditFragment editFragment;


  @Override
  public void onBackPressed() {
    finish();
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_edit);
    editFragment = (EditFragment) getFragmentManager().findFragmentById(R.id.editFragment);
  }


  @Override
  protected void onResume() {
    super.onResume();
    editFragment.updateView(activeTodoId);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        LocalRemoteTodo todo = editFragment.buildTodo();
        if (todo != null) {
          new SaveAsync(this).execute(todo);
          return true;
        }
        return false;
    }

    return super.onOptionsItemSelected(item);
  }

}
