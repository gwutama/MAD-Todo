package com.utama.madtodo;

public class EditActivity extends CreateActivity {

  @Override
  public void onBackPressed() {
    finish();
  }
  
  
  public void setContentView() {
    super.setContentView(R.layout.activity_edit);
  }

}
