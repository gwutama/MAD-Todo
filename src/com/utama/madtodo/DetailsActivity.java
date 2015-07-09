package com.utama.madtodo;

import com.utama.madtodo.fragments.DetailsFragment;

import android.os.Bundle;


/**
 * The Class DetailsActivity represents an activity to display details of a task.
 */
public class DetailsActivity extends SingleTodoActivity
    implements ActionEditable, ActionDeleteable {

  /** The details fragment if the fragment that actually contains text fields for
   * displaying details. */
  DetailsFragment detailsFragment;


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
   * Inflates activity_details.xml and sets up {@link com.utama.madtodo.fragments.DetailsFragment}
   * 
   * @see com.utama.madtodo.SingleTodoActivity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
    detailsFragment = (DetailsFragment) getFragmentManager().findFragmentById(R.id.detailsFragment);
  }
  
  
  /**
   * Update details fragment with a data. The activity retrieves an id (activeTodoId) that was
   * set somewhere else using putExtras(DbConsts.Columns.id, ...).
   * 
   * @see com.utama.madtodo.SingleTodoActivity#onResume()
   */
  @Override
  protected void onResume() {
    super.onResume();
    detailsFragment.updateView(activeTodoId);
  }

}
