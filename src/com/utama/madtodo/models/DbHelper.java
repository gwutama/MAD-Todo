package com.utama.madtodo.models;

import com.utama.madtodo.models.DbConsts.Table.Tasks;
import com.utama.madtodo.models.DbConsts.Table.Contacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * The Class DbHelper represents a helper class to manage database creation and version management.
 */
public class DbHelper extends SQLiteOpenHelper {

  /** For debugging purposes. */
  private static final String TAG = "DbHelper";


  /**
   * Instantiates a new db helper.
   *
   * @param context This is usually an activity instance.
   */
  public DbHelper(Context context) {
    super(context, DbConsts.DB_NAME, null, DbConsts.DB_VERSION);
  }


  /**
   * The SQL query that is to be executed when there is still no sqlite database structure in the
   * app's installation directory.
   * 
   * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
   */
  @Override
  public void onCreate(SQLiteDatabase db) {
    String sql = String.format(
        "CREATE TABLE %s(" 
        + "%s INTEGER PRIMARY KEY, " 
        + "%s INTEGER DEFAULT 0, "
        + "%s TEXT NOT NULL, " 
        + "%s TEXT, " 
        + "%s INTEGER DEFAULT 0, "
        + "%s INTEGER DEFAULT 0, " 
        + "%s INTEGER DEFAULT 0)",
        Tasks.TABLE, Tasks.Column.ID, Tasks.Column.REMOTE_ID, Tasks.Column.NAME,
        Tasks.Column.DESCRIPTION, Tasks.Column.EXPIRY, Tasks.Column.IS_IMPORTANT,
        Tasks.Column.IS_MARKED_DONE);

    Log.d(TAG, "onCreate with SQL: " + sql);
    db.execSQL(sql);
    
    sql = String.format(
        "CREATE TABLE %s("
        + "%s INTEGER PRIMARY KEY, "
        + "%s INTEGER NOT NULL, "
        + "%s INTEGER NOT NULL, "
        + "FOREIGN KEY %s REFERENCES %s(%s) ON DELETE CASCADE)",
        Contacts.TABLE, Contacts.Column.ID, Contacts.Column.TODOS_ID, Contacts.
        Column.CONTACTS_ID, Contacts.Column.TODOS_ID, Tasks.TABLE, Tasks.Column.ID);
    
    Log.d(TAG, "onCreate with SQL: " + sql);
    db.execSQL(sql);    
  }


  /**
   * The SQL query that is to be executed when the user installs a newer version of this app that
   * may require a database upgrade. This method only drops the table structure and recreates it for
   * now.
   * 
   * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
   *      int, int)
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + Tasks.TABLE);    
    db.execSQL("DROP TABLE IF EXISTS " + Contacts.TABLE);    
    onCreate(db);
  }

}
