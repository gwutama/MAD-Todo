package com.utama.madtodo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class LocalPersistance extends SQLiteOpenHelper {

  private static final String TAG = "LocalPersistance";
  
  
  public LocalPersistance(Context context) {
    super(context, LocalTodoConsts.DB_NAME, null, LocalTodoConsts.DB_VERSION);
  }

  
  @Override
  public void onCreate(SQLiteDatabase db) {
    String sql = String.format(
        "CREATE TABLE %s("
        + "%s INT PRIMARY KEY, "
        + "%s TEXT, "
        + "%s TEXT, "
        + "%s INT, "
        + "%s INT, "
        + "%s INT)", 
        LocalTodoConsts.TABLE, LocalTodoConsts.Column.ID, LocalTodoConsts.Column.NAME, 
        LocalTodoConsts.Column.DESCRIPTION, LocalTodoConsts.Column.EXPIRY, 
        LocalTodoConsts.Column.IS_IMPORTANT, LocalTodoConsts.Column.IS_MARKED_DONE);

    Log.d(TAG, "onCreate with SQL: " + sql);
    db.execSQL(sql);
  }

  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + LocalTodoConsts.TABLE);
    onCreate(db);
  }

}
