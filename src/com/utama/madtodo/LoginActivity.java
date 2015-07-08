package com.utama.madtodo;

import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.models.RemoteUser;
import com.utama.madtodo.tasks.AuthAsync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends Activity {

  // UI references.
  private AutoCompleteTextView emailText;
  private EditText passwordText;
  private Button signInButton;
  private RemoteUser user;
  private ProgressDialog loginProgress;
  private TextView loginErrorTextView;
  private Boolean loginFailed;


  @Override
  public void onBackPressed() {
    finish();
  }

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    
    // Track user data
    user = new RemoteUser();
    
    
    // Login error warning notification
    loginErrorTextView = (TextView) findViewById(R.id.loginErrorTextView);
    loginErrorTextView.setBackgroundColor(0xFFD43B0D);
    loginErrorTextView.setTextColor(0xFFFFFFFF);
    loginErrorTextView.setVisibility(TextView.GONE);
    
    loginFailed = getIntent().getBooleanExtra("isLoginFailure", false);
    if (loginFailed)
      loginErrorTextView.setVisibility(TextView.VISIBLE);
        

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
        loginErrorTextView.setVisibility(TextView.GONE);
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
        loginErrorTextView.setVisibility(TextView.GONE);        
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

    
    // Attempt auto login if offline mode is set to off, otherwise work locally (go directly
    // to the todo list activity.
    LocalRemoteTodo.setupPersistence(this);
    
    if(!LocalRemoteTodo.isOfflineMode() && !loginFailed)
      attemptAutoLogin();
    else if(LocalRemoteTodo.isOfflineMode())
      startActivity(new Intent(this, TodoListActivity.class));
  }


  @Override
  protected void onResume() {
    super.onResume();
    fillInEmailPasswordFieldsFromPreferences();
  }


  private void attemptAutoLogin() {
    fillInEmailPasswordFieldsFromPreferences();

    if (signInButton.isEnabled())
      attemptLogin();
  }


  private void fillInEmailPasswordFieldsFromPreferences() {
    // Fill in default email and password from preference manager
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    String email = pref.getString("email", "");

    if (!TextUtils.isEmpty(email))
      emailText.setError(null); // reset error message
    emailText.setText(email);

    String password = pref.getString("password", "");
    passwordText.setText(password);

    enableDisableSignInButton();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    return false;
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
    if (LocalRemoteTodo.isOfflineMode())
      return;
      
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
    
    saveUserCredentialsInPreferences(email, password);
  }
  

  private void saveUserCredentialsInPreferences(String email, String password) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    Editor editor = prefs.edit();
    editor.putString("email", email);
    editor.putString("password", password);
    editor.commit();
  }


  public void showProgress(boolean show) {
    if (show)
      loginProgress.show();
    else
      loginProgress.hide();
  }

}
