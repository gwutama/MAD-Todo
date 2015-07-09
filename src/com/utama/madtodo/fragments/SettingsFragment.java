package com.utama.madtodo.fragments;

import com.utama.madtodo.LoginActivity;
import com.utama.madtodo.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;


/**
 * The Class SettingsFragment represents a fragment for modifying app preferences.
 */
public class SettingsFragment extends PreferenceFragment
    implements OnSharedPreferenceChangeListener {

  /** The shared preferences object */
  private SharedPreferences prefs;

  /**
   * The preference screen object, which is the representation of modifiable preferences displayed
   * in the fragment.
   */
  PreferenceScreen prefsScreen;

  /** The preferences that are modifiable. */
  private Preference email, password, offlineMode, apiRoot, forgetCredentials;


  /**
   * Set up the preference member variables and bind items on click.
   * 
   * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);

    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    prefs.registerOnSharedPreferenceChangeListener(this);

    PreferenceManager prefsMan = getPreferenceManager();
    email = prefsMan.findPreference("email");
    password = prefsMan.findPreference("password");
    offlineMode = prefsMan.findPreference("offlineMode");
    apiRoot = prefsMan.findPreference("apiRoot");

    forgetCredentials = prefsMan.findPreference("forgetCredentials");
    forgetCredentials.setOnPreferenceClickListener(new OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        Editor editor = prefs.edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.commit();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        return false;
      }
    });

    prefsScreen = getPreferenceScreen();

    prefsScreen.removePreference(email);
    prefsScreen.removePreference(password);

    setupOfflineModePreferenceDependencies();
  }


  /**
   * Setup offline mode preference dependencies.
   */
  void setupOfflineModePreferenceDependencies() {
    boolean value = prefs.getBoolean("offlineMode", false);
    if (value) {
      apiRoot.setEnabled(false);
      forgetCredentials.setEnabled(false);
    } else {
      apiRoot.setEnabled(true);
      forgetCredentials.setEnabled(true);
    }
  }


  /**
   * Based on offlineMode value, enable or disable other preferences (API root and
   * "forgot credentials" items).
   * 
   * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(
   *      android.content.SharedPreferences, java.lang.String)
   */
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals("offlineMode")) {
      setupOfflineModePreferenceDependencies();
    }
  }
}
