package com.utama.madtodo.models;

import java.net.MalformedURLException;
import java.net.URL;

import com.utama.madtodo.R;
import com.utama.madtodo.SettingsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class DbHelper extends SQLiteOpenHelper {

  private static final String TAG = "DbHelper";


  public DbHelper(Context context) {
    super(context, DbConsts.DB_NAME, null, DbConsts.DB_VERSION);
  }


  @Override
  public void onCreate(SQLiteDatabase db) {
    String sql = String.format(
        "CREATE TABLE %s(" + "%s INTEGER PRIMARY KEY, " + "%s INTEGER DEFAULT 0, "
            + "%s TEXT NOT NULL, " + "%s TEXT, " + "%s INTEGER DEFAULT 0, "
            + "%s INTEGER DEFAULT 0, " + "%s INTEGER DEFAULT 0)",
        DbConsts.TABLE, DbConsts.Column.ID, DbConsts.Column.REMOTE_ID, DbConsts.Column.NAME,
        DbConsts.Column.DESCRIPTION, DbConsts.Column.EXPIRY, DbConsts.Column.IS_IMPORTANT,
        DbConsts.Column.IS_MARKED_DONE);

    Log.d(TAG, "onCreate with SQL: " + sql);
    db.execSQL(sql);
  }


  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + DbConsts.TABLE);
    onCreate(db);
  }


  public static final boolean setupPersistence(Activity activity) {
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);

    boolean offlineMode = pref.getBoolean("offlineMode", false);
    LocalRemoteTodo.offlineMode = offlineMode;

    if (!offlineMode) {
      try {
        URL apiRoot = new URL(pref.getString("apiRoot", ""));
        RemoteTodo.setApiRoot(apiRoot);
        RemoteUser.setApiRoot(apiRoot);
      } catch (MalformedURLException e) {
        e.printStackTrace();
        Toast.makeText(activity, R.string.api_root_error, Toast.LENGTH_LONG).show();
        
        if (activity instanceof SettingsActivity == false)
          activity.startActivity(new Intent(activity, SettingsActivity.class));
        
        return false;
      }
    }

    LocalTodo.setDbHelper(new DbHelper(activity));

    return true;
  }

}
