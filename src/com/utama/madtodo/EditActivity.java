package com.utama.madtodo;

import com.utama.madtodo.fragments.EditFragment;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.tasks.SaveAsync;

import android.os.Bundle;
import android.view.MenuItem;


// TODO: Auto-generated Javadoc
/**
 * The Class EditActivity.
 */
public class EditActivity extends CreateActivity implements ActionSaveable, ActionDeleteable {

  /** The edit fragment. */
  EditFragment editFragment;


  /* (non-Javadoc)
   * @see com.utama.madtodo.CreateActivity#onBackPressed()
   */
  @Override
  public void onBackPressed() {
    finish();
  }


  /* (non-Javadoc)
   * @see com.utama.madtodo.CreateActivity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_edit);
    editFragment = (EditFragment) getFragmentManager().findFragmentById(R.id.editFragment);
  }


  /**
   * Update edit fragment with a data. The activity retrieves an id (activeTodoId) that was
   * set somewhere else using putExtras(DbConsts.Columns.id, ...).
   * 
   * @see com.utama.madtodo.SingleTodoActivity#onResume()
   */
  @Override
  protected void onResume() {
    super.onResume();
    editFragment.updateView(activeTodoId);
  }


  /* (non-Javadoc)
   * @see com.utama.madtodo.CreateActivity#onOptionsItemSelected(android.view.MenuItem)
   */
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
