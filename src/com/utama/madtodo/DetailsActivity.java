package com.utama.madtodo;

import com.utama.madtodo.fragments.DetailsFragment;

import android.os.Bundle;


public class DetailsActivity extends SingleTodoActivity
    implements ActionEditable, ActionDeleteable {

  DetailsFragment detailsFragment;


  @Override
  public void onBackPressed() {
    finish();
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
    detailsFragment = (DetailsFragment) getFragmentManager().findFragmentById(R.id.detailsFragment);
  }
  
  
  @Override
  protected void onResume() {
    super.onResume();
    detailsFragment.updateView(activeTodoId);
  }

}
