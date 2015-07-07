package com.utama.madtodo;

import com.utama.madtodo.models.DbHelper;
import com.utama.madtodo.models.RemoteUser;
import com.utama.madtodo.tasks.AuthAsync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity {

  // UI references.
  private AutoCompleteTextView emailText;
  private EditText passwordText;
  private Button signInButton;
  private RemoteUser user;
  private ProgressDialog loginProgress;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    
    DbHelper.setupPersistance(this);
    user = new RemoteUser();
    

    // Email edit text
    emailText = (AutoCompleteTextView) findViewById(R.id.authEmailText);    
    emailText.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String email = emailText.getText().toString();
        
        if (TextUtils.isEmpty(email))
          emailText.setError(getString(R.string.auth_no_email));
        else
          emailText.setError(null);
          
        enableDisableSignInButton();
        return false;
      }
    });
    emailText.setError(getString(R.string.auth_no_email));
    
    
    // Password edit text
    passwordText = (EditText) findViewById(R.id.authPasswordText);
    passwordText.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        enableDisableSignInButton();
        return false;
      }
    });

    
    // Sign in button
    signInButton = (Button) findViewById(R.id.authSignInButton);
    signInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });

    
    // Progress dialog
    loginProgress = new ProgressDialog(this);
    loginProgress.setTitle("Logging in");
    loginProgress.setIndeterminate(true);
  }


  private void enableDisableSignInButton() {
    String email = emailText.getText().toString();
    String password = passwordText.getText().toString();

    if (user.isEmailValid(email) && user.isPasswordValid(password)) {
      signInButton.setEnabled(true);
    } else {
      signInButton.setEnabled(false);
    }
  }


  public void attemptLogin() {
    // Reset errors.
    emailText.setError(null);
    passwordText.setError(null);

    // Store values at the time of the login attempt.
    String email = emailText.getText().toString();
    String password = passwordText.getText().toString();
    user.setEmail(email);
    user.setPassword(password);

    showProgress(true);
    new AuthAsync(this, user).execute();
  }


  public void showProgress(boolean show) {
    if (show)
      loginProgress.show();
    else
      loginProgress.hide();
  }

}