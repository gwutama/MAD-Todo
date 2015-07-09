package com.utama.madtodo;

import com.utama.madtodo.fragments.CreateFragment;
import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.tasks.SaveAsync;

import android.os.Bundle;
import android.view.MenuItem;


/**
 * The Class CreateActivity represents an activity for creating a new task.
 */
public class CreateActivity extends SingleTodoActivity implements ActionSaveable {

  /** The create fragment is the fragment that is actually contains form fields for
   * creating a new task. */
  CreateFragment createFragment;


  /**
   * The implementation is to close the activity on back button press.
   * 
   * @see android.app.Activity#onBackPressed()
   */
  @Override
  public void onBackPressed() {
    finish();
  }


  /**
   * Inflates activity_create.xml and sets up {@link com.utama.madtodo.fragments.CreateFragment}
   * 
   * @see com.utama.madtodo.SingleTodoActivity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create);
    createFragment = (CreateFragment) getFragmentManager().findFragmentById(R.id.createFragment);
  }


  /**
   * This method does nothing other than executing {@link com.utama.madtodo.tasks.SaveAsync}
   * on save button click.
   * 
   * @see com.utama.madtodo.SingleTodoActivity#onOptionsItemSelected(android.view.MenuItem)
   */
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
