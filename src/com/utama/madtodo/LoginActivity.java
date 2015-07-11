package com.utama.madtodo;

import com.utama.madtodo.models.LocalRemoteTodo;
import com.utama.madtodo.models.RemoteUser;
import com.utama.madtodo.tasks.AuthAsync;
import com.utama.madtodo.tasks.TestConnectionAsync;

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


/**
 * The Class LoginActivity represents an activity to log a user in. A user must supply
 * his email and password.
 * 
 * If the device fails to connect to the web service, it will bypass the login screen and
 * the user will be redirected to the todo list activity. From then on, the application
 * will work offline and no synchronization will happen.
 */
public class LoginActivity extends Activity {

  /** The email field. */
  // UI references.
  private AutoCompleteTextView emailText;
  
  /** The password field. */
  private EditText passwordText;
  
  /** The sign in button. */
  private Button signInButton;
  
  /** Track actions on the web service side. */
  private RemoteUser user;
  
  /** The login progress dialog. Will be shown during authentication. */
  private ProgressDialog loginProgress;
  
  /** The test connection progress dialog. Will be shown during connection test prior
   * starting the app. */
  private ProgressDialog testConnectionProgress;
  
  /** The login error notification bar. Will be shown when the authentication fails. */
  private TextView loginErrorTextView;
  
  /** Tracks whether the last login was failed. This member variable is used together
   * with loginErrorTextView. */
  private Boolean loginFailed;


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
   * Inflates activity_login.xml and sets up UI components callback bindings. 
   * Finally checks whether device has connection to the web service asynchronously using 
   * {@link com.utama.madtodo.tasks.TestConnectionAsync}. Otherwise offline mode will be 
   * automatically enabled in {@link com.utama.madtodo.tasks.TestConnectionAsync} and 
   * user will be redirected to the todo list activity.
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Track user data
    user = new RemoteUser();


    // Login error warning notification
    loginErrorTextView = (TextView) findViewById(R.id.loginErrorTextView);
    loginErrorTextView.setBackgroundColor(0xFFDD792E);
    loginErrorTextView.setTextColor(0xFFFFFFFF);
    loginErrorTextView.setVisibility(TextView.GONE);

    setLoginFailed(getIntent().getBooleanExtra("isLoginFailure", false));
    if (getLoginFailed())
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


    // Progress dialogs
    loginProgress = new ProgressDialog(this);
    loginProgress.setTitle(R.string.app_name);
    loginProgress.setMessage(getString(R.string.auth_logging_in));
    loginProgress.setIndeterminate(true);
    
    testConnectionProgress = new ProgressDialog(this);
    testConnectionProgress.setTitle(R.string.app_name);
    testConnectionProgress.setMessage(getString(R.string.auth_testing_connection));
    testConnectionProgress.setIndeterminate(true);
  }


  /**
   * Make sure to always hide login progress bar and test connection progress bar on activity
   * resume.
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume() {
    super.onResume();
    showLoginProgress(false);    
    showTestConnectionProgress(false);
    fillInEmailPasswordFieldsFromPreferences();
    runTestConnectionAsync();
  }
  

  /**
   * Check whether device has connection to the web service. Otherwise offline mode will be 
   * enabled in TestConnectionAsync and user will be redirected to the todo list activity.
   */
  private void runTestConnectionAsync() {
    showTestConnectionProgress(true);    
    LocalRemoteTodo.setupPersistence(this);
    new TestConnectionAsync(this).execute();    
  }


  /**
   * Attempt auto login if offline mode is disabled, assuming that email and password were
   * already automatically filled 
   * (e.g. by {@link com.utama.madtodo.LoginActivity#fillInEmailPasswordFieldsFromPreferences()}). 
   * Otherwise, this method will start {@link com.utama.madtodo.activity.TodoListActivity} instead.
   */
  public void attemptAutoLogin() {
    if (LocalRemoteTodo.isOfflineMode()) {
      startActivity(new Intent(this, TodoListActivity.class));      
      return;
    }
    
    showTestConnectionProgress(false);
    
    fillInEmailPasswordFieldsFromPreferences();

    if (signInButton.isEnabled())
      attemptLogin();
  }


  /**
   * Fill in email password fields from preferences.
   */
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


  /**
   * Inflates the login.xml into menu.
   * 
   * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }


  /**
   * Run {@link com.utama.madtodo.SettingsActivity} on clicked.
   * 
   * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    return false;
  }


  /**
   * Enable or disable sign in button. This method will check the input values of the email 
   * and password fields. The sign in button is disabled by default and only enabled if
   * user supplied a valid email address and password.
   */
  private void enableDisableSignInButton() {
    String email = emailText.getText().toString();
    String password = passwordText.getText().toString();

    if (user.isEmailValid(email) && user.isPasswordValid(password)) {
      signInButton.setEnabled(true);
    } else {
      signInButton.setEnabled(false);
    }
  }


  /**
   * Attempt to log in user. This method will execute {@link com.utama.madtodo.tasks.AuthAsync}.
   * Finally, the email and password will be saved in the app preferences, does not matter
   * whether the email/password combination is correct or not.
   * 
   */
  private void attemptLogin() {
    if (LocalRemoteTodo.isOfflineMode()) {
      startActivity(new Intent(this, TodoListActivity.class));      
      return;
    }

    // Reset errors.
    emailText.setError(null);
    passwordText.setError(null);

    // Store values at the time of the login attempt.
    String email = emailText.getText().toString();
    String password = passwordText.getText().toString();
    user.setEmail(email);
    user.setPassword(password);

    showLoginProgress(true);
    new AuthAsync(this, user).execute();

    saveUserCredentialsInPreferences(email, password);
  }


  /**
   * Save user credentials in preferences.
   *
   * @param email The user's email address.
   * @param password The user's password.
   */
  private void saveUserCredentialsInPreferences(String email, String password) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    Editor editor = prefs.edit();
    editor.putString("email", email);
    editor.putString("password", password);
    editor.commit();
  }


  /**
   * Show or hide the login progress dialog.
   *
   * @param show True if the dialog to be shown. False otherwise.
   */
  public void showLoginProgress(boolean show) {
    if (show)
      loginProgress.show();
    else
      loginProgress.hide();
  }

  
  /**
   * Show or hide the test connection progress dialog.
   *
   * @param show True if the dialog to be shown. False otherwise.
   */
  public void showTestConnectionProgress(boolean show) {
    if (show)
      testConnectionProgress.show();
    else
      testConnectionProgress.hide();    
  }
  

  /**
   * Gets the login failed.
   *
   * @return the login failed
   */
  public Boolean getLoginFailed() {
    return loginFailed;
  }


  /**
   * Sets the login failed.
   *
   * @param loginFailed the new login failed
   */
  public void setLoginFailed(Boolean loginFailed) {
    this.loginFailed = loginFailed;
  }

}
