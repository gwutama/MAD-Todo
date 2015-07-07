package com.utama.madtodo;

import android.app.Activity;
import android.os.Bundle;


public class DetailsActivity extends Activity {

  @Override
  public void onBackPressed() {
    finish();
  }
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
  }
  
}
