package com.utama.madtodo.model;

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
  private static LocalPersistance persistance = null;

  
  public LocalTodo() {
    super();
  }
  
  
  public LocalTodo(Cursor cursor) {
    super();
    setFromCursor(cursor);
  }

  
  public static LocalPersistance getPersistance() {
    return persistance;
  }


  public static void setPersistance(LocalPersistance persistance) {
    LocalTodo.persistance = persistance;
  }


  public static LocalTodo findOne(long id) {
    Cursor cursor = queryOne(id);

    if (cursor.getCount() == 0)
      return null;

    LocalTodo todo = new LocalTodo(cursor);
    return todo;
  }
  
  
  private static Cursor queryOne(long id) {
    Log.d(TAG, "queryOne");

    if (persistance == null)
      throw new SQLiteException("LocalPersistance instance is not set");
      
    SQLiteQueryBuilder query = new SQLiteQueryBuilder();
    query.setTables(LocalTodoConsts.TABLE);
    String whereClause = LocalTodoConsts.Column.ID + "=" + id;
    query.appendWhere(whereClause);

    SQLiteDatabase db = persistance.getReadableDatabase();
    Cursor cursor = query.query(db, null, null, null, null, null, null);
    Log.d(TAG, "Queried records: " + cursor.getCount());

    return cursor;
  }  


  public static List<LocalTodo> findAll() {
    Cursor cursor = queryAll(null);
    List<LocalTodo> ret = new ArrayList<LocalTodo>();
    
    while (cursor.moveToNext()) {
      LocalTodo todo = new LocalTodo(cursor);
      ret.add(todo);
    }
    
    return ret;
  }

  
  private static Cursor queryAll(String sortOrder) {
    Log.d(TAG, "queryMany");

    if (persistance == null)
      throw new SQLiteException("LocalPersistance instance is not set");
    
    SQLiteQueryBuilder query = new SQLiteQueryBuilder();
    query.setTables(LocalTodoConsts.TABLE);

    if (TextUtils.isEmpty(sortOrder))
      sortOrder = LocalTodoConsts.DEFAULT_SORT;

    SQLiteDatabase db = persistance.getReadableDatabase();
    Cursor cursor = query.query(db, null, null, null, null, null, sortOrder);
    Log.d(TAG, "Queried records: " + cursor.getCount());

    return cursor;
  }
  

  @Override
  protected long create() {
    ContentValues values = buildValues();
    values.remove(LocalTodoConsts.Column.ID); // because of AUTO INCREMENT property of _id field.
    long rowId = create(values);
    id = rowId; 
    return rowId;
  }

  
  private long create(ContentValues values) {
    Log.d(TAG, "insert");
    
    if (persistance == null)
      throw new SQLiteException("LocalPersistance instance is not set");    

    SQLiteDatabase db = persistance.getWritableDatabase();
    long rowId = db.insertWithOnConflict(LocalTodoConsts.TABLE, null, values,
        SQLiteDatabase.CONFLICT_IGNORE);
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
  
  
  private int update(long id, ContentValues values) {
    Log.d(TAG, "update");

    if (persistance == null)
      throw new SQLiteException("LocalPersistance instance is not set");    
    
    String whereClause = LocalTodoConsts.Column.ID + "=" + id;
    SQLiteDatabase db = persistance.getWritableDatabase();
    int ret = db.update(LocalTodoConsts.TABLE, values, whereClause, null);
    Log.d(TAG, "Updated records: " + ret);

    return ret;
  }  


  @Override
  public long delete() {
    long deletedCount = delete(id);
    return deletedCount;
    
  }
  
  
  private long delete(long id) {
    Log.d(TAG, "delete");
    
    if (persistance == null)
      throw new SQLiteException("LocalPersistance instance is not set");    

    String whereClause = LocalTodoConsts.Column.ID + "=" + id;
    SQLiteDatabase db = persistance.getWritableDatabase();
    long ret = db.delete(LocalTodoConsts.TABLE, whereClause, null);
    Log.d(TAG, "Deleted records: " + ret);

    return ret;
  }  


  private ContentValues buildValues() {
    ContentValues values = new ContentValues();
    values.put(LocalTodoConsts.Column.ID, id);
    values.put(LocalTodoConsts.Column.NAME, name);    
    values.put(LocalTodoConsts.Column.DESCRIPTION, description);
    values.put(LocalTodoConsts.Column.EXPIRY, expiry.getTime());
    values.put(LocalTodoConsts.Column.IS_IMPORTANT, isImportant);
    values.put(LocalTodoConsts.Column.IS_MARKED_DONE, isMarkedDone);
    return values;
  }
  
  
  public void setFromCursor(Cursor cur) {
    id = cur.getLong(cur.getColumnIndex(LocalTodoConsts.Column.ID));
    name = cur.getString(cur.getColumnIndex(LocalTodoConsts.Column.NAME));
    description = cur.getString(cur.getColumnIndex(LocalTodoConsts.Column.DESCRIPTION));
    expiry = new Date(cur.getLong(cur.getColumnIndex(LocalTodoConsts.Column.EXPIRY)));
    isImportant = cur.getInt(cur.getColumnIndex(LocalTodoConsts.Column.IS_IMPORTANT)) != 0;
    isMarkedDone = cur.getInt(cur.getColumnIndex(LocalTodoConsts.Column.IS_MARKED_DONE)) != 0;
  }


}
