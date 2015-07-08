package com.utama.madtodo.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


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

}
