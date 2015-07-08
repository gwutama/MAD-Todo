package com.utama.madtodo;

import com.utama.madtodo.models.LocalRemoteTodo;

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


public class SettingsFragment extends PreferenceFragment
    implements OnSharedPreferenceChangeListener {

  private SharedPreferences prefs;
  PreferenceScreen prefsScreen;
  private Preference email, password, offlineMode, apiRoot, forgetCredentials;


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
  

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    LocalRemoteTodo.setupPersistence(getActivity());

    if (key.equals("offlineMode")) {
      setupOfflineModePreferenceDependencies();
    }
  }
}
