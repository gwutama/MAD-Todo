package com.utama.madtodo.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;


public class LocalTodo extends TodoEntity {

  private static final String TAG = "LocalTodo";
  private static DbHelper dbHelper = null;


  public LocalTodo() {
    super();
  }


  public LocalTodo(Cursor cursor) {
    super();
    setFromCursor(cursor);
  }


  public LocalTodo(TodoEntity todo) {
    super(todo);
  }


  public static DbHelper getDbHelper() {
    return dbHelper;
  }


  public static void setDbHelper(DbHelper persistance) {
    LocalTodo.dbHelper = persistance;
  }


  public static LocalTodo findOne(long id) {
    Cursor cursor = queryOne(id);

    if (cursor.getCount() == 0)
      return null;

    cursor.moveToFirst();
    LocalTodo todo = new LocalTodo(cursor);
    return todo;
  }


  private static Cursor queryOne(long id) throws SQLiteException {
    Log.d(TAG, "queryOne");

    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    SQLiteQueryBuilder query = new SQLiteQueryBuilder();
    query.setTables(DbConsts.TABLE);
    String whereClause = DbConsts.Column.ID + "=" + id;
    query.appendWhere(whereClause);

    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = query.query(db, null, null, null, null, null, null);
    Log.d(TAG, "Queried records: " + cursor.getCount());

    return cursor;
  }


  public static List<LocalTodo> findAll() {
    return findAll(null);
  }
  
  
  public static List<LocalTodo> findAll(String sortOrder) {
    Cursor cursor = queryAll(sortOrder);
    List<LocalTodo> ret = new ArrayList<LocalTodo>();

    while (cursor.moveToNext()) {
      LocalTodo todo = new LocalTodo(cursor);
      ret.add(todo);
    }

    return ret;
  }


  private static Cursor queryAll(String sortOrder) throws SQLiteException {
    Log.d(TAG, "queryMany");

    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    SQLiteQueryBuilder query = new SQLiteQueryBuilder();
    query.setTables(DbConsts.TABLE);

    if (TextUtils.isEmpty(sortOrder) || sortOrder == null)
      sortOrder = DbConsts.DEFAULT_SORT;

    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = query.query(db, null, null, null, null, null, sortOrder);
    Log.d(TAG, "Queried records: " + cursor.getCount());

    return cursor;
  }


  @Override
  protected long create() {
    ContentValues values = buildValues();
    values.remove(DbConsts.Column.ID); // because of AUTO INCREMENT property of _id field.
    long rowId = create(values);
    id = rowId;
    return rowId;
  }


  private long create(ContentValues values) throws SQLiteException {
    Log.d(TAG, "insert");

    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    SQLiteDatabase db = dbHelper.getWritableDatabase();
    long rowId =
        db.insertWithOnConflict(DbConsts.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    Log.d(TAG, "Inserted record id: " + rowId);

    return rowId;
  }


  @Override
  protected long update() {
    ContentValues values = buildValues();
    long rowId = update(id, values);
    id = rowId;
    return rowId;
  }


  private int update(long id, ContentValues values) throws SQLiteException {
    Log.d(TAG, "update");

    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    String whereClause = DbConsts.Column.ID + "=" + id;
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int ret = db.update(DbConsts.TABLE, values, whereClause, null);
    Log.d(TAG, "Updated records: " + ret);

    return ret;
  }


  @Override
  public long delete() {
    long count = delete(id);
    return count;
  }


  private long delete(long id) throws SQLiteException {
    Log.d(TAG, "delete");

    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    String whereClause = DbConsts.Column.ID + "=" + id;
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    long ret = db.delete(DbConsts.TABLE, whereClause, null);
    Log.d(TAG, "Deleted records: " + ret);

    return ret;
  }


  public static long purge() throws SQLiteException {
    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    List<LocalTodo> todos = findAll();
    long deletedRows = todos.size();

    SQLiteDatabase db = dbHelper.getWritableDatabase();
    dbHelper.onUpgrade(db, 0, DbConsts.DB_VERSION);

    return deletedRows;
  }


  private ContentValues buildValues() {
    ContentValues values = new ContentValues();
    values.put(DbConsts.Column.ID, id);
    values.put(DbConsts.Column.REMOTE_ID, remoteId);
    values.put(DbConsts.Column.NAME, name);
    values.put(DbConsts.Column.DESCRIPTION, description);
    values.put(DbConsts.Column.EXPIRY, expiry.getTime());
    values.put(DbConsts.Column.IS_IMPORTANT, isImportant);
    values.put(DbConsts.Column.IS_MARKED_DONE, isMarkedDone);
    return values;
  }


  public void setFromCursor(Cursor cur) {
    id = cur.getLong(cur.getColumnIndex(DbConsts.Column.ID));
    remoteId = cur.getLong(cur.getColumnIndex(DbConsts.Column.REMOTE_ID));
    name = cur.getString(cur.getColumnIndex(DbConsts.Column.NAME));
    description = cur.getString(cur.getColumnIndex(DbConsts.Column.DESCRIPTION));
    expiry = new Date(cur.getLong(cur.getColumnIndex(DbConsts.Column.EXPIRY)));
    isImportant = cur.getInt(cur.getColumnIndex(DbConsts.Column.IS_IMPORTANT)) == 1;
    isMarkedDone = cur.getInt(cur.getColumnIndex(DbConsts.Column.IS_MARKED_DONE)) == 1;
  }


}
