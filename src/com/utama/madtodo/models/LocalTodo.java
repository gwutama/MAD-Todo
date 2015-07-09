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


/**
 * The Class LocalTodo represents a class for working with local todo tasks. This method will NOT
 * take care of a synchronized operations with the web service.
 * 
 * This is a lower level class for executing CRUD operations and managing records that are saved on
 * the local device.
 */
public class LocalTodo extends TodoEntity {

  /** For debugging purposes. */
  private static final String TAG = "LocalTodo";

  /** The instance of DbHelper which manages the database connection and database creation. */
  private static DbHelper dbHelper = null;


  /**
   * Instantiates a new local todo.
   */
  public LocalTodo() {
    super();
  }


  /**
   * Instantiates a new local todo using a valid database cursor.
   *
   * @param cursor The database cursor.
   */
  public LocalTodo(Cursor cursor) {
    super();
    setFromCursor(cursor);
  }


  /**
   * Instantiates a new local todo using a valid todo entity instance.
   *
   * @param todo The TodoEntitiy instance.
   */
  public LocalTodo(TodoEntity todo) {
    super(todo);
  }


  /**
   * Gets the db helper.
   *
   * @return the db helper
   */
  public static DbHelper getDbHelper() {
    return dbHelper;
  }


  /**
   * Sets the db helper.
   *
   * @param persistance the new db helper
   */
  public static void setDbHelper(DbHelper persistance) {
    LocalTodo.dbHelper = persistance;
  }


  /**
   * Find one record by (local) task id.
   *
   * @param id The id of the local task. Please not that this is NOT the remote task id, since they
   *        serve for different purposes.
   * @return A {@link LocalTodo} todo. If record cannot be found, this will return an instance which
   *         id equals to -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   */
  public static LocalTodo findOne(long id) throws IllegalArgumentException {
    if (id < 0)
      throw new IllegalArgumentException("Invalid id");

    Cursor cursor = queryOne(id);

    if (cursor.getCount() == 0)
      return null;

    cursor.moveToFirst();
    LocalTodo todo = new LocalTodo(cursor);
    return todo;
  }


  /**
   * Helper method to retrieve a local task record by id by querying the sql database directly.
   *
   * @param id The id of the local task. Please not that this is NOT the remote task id, since they
   *        serve for different purposes.
   * @return A database cursor.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   */
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


  /**
   * Find all task records. The results are sorted by {@link DbConsts.DEFAULT_SORT}.
   *
   * @return A list of {@link LocalTodo} instance.
   */
  public static List<LocalTodo> findAll() {
    return findAll(null);
  }


  /**
   * Find all task records with a specific sorting order.
   *
   * @param sortOrder The sort order. You can supply other "WHERE" clauses other than the ones
   *        specified by {@link DbConsts.SORT_IMPORTANCE_DATE} or
   *        {@link DbConsts.SORT_DATE_IMPORTANCE}.
   * @return A list of {@link queryAll} instance.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   */
  public static List<LocalTodo> findAll(String sortOrder) throws SQLiteException {
    Cursor cursor = queryAll(sortOrder);
    List<LocalTodo> ret = new ArrayList<LocalTodo>();

    while (cursor.moveToNext()) {
      LocalTodo todo = new LocalTodo(cursor);
      ret.add(todo);
    }

    return ret;
  }


  /**
   * Helper method to retrieve all local tasks records by querying the sql database directly.
   *
   * @param sortOrder The sort order. You can supply other "WHERE" clauses other than the ones
   *        specified by {@link DbConsts.SORT_IMPORTANCE_DATE} or
   *        {@link DbConsts.SORT_DATE_IMPORTANCE}.
   * @return A database cursor.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   */
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


  /**
   * Create a record.
   *
   * @return The newly inserted record id on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   * @see com.utama.madtodo.models.TodoEntity#create()
   */
  @Override
  protected long create() throws IllegalArgumentException, SQLiteException {
    if (TextUtils.isEmpty(name))
      throw new IllegalArgumentException("Task name cannot be empty");

    ContentValues values = buildValues();
    values.remove(DbConsts.Column.ID); // because of AUTO INCREMENT property of _id field.
    long rowId = create(values);
    id = rowId;
    return rowId;
  }


  /**
   * Helper method to insert a record into database using SQL query directly.
   *
   * @param values The values to be updated into the database.
   * @return The newly inserted record id.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   */
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


  /**
   * Update a record.
   *
   * @return The id of updated record on success. Otherwise -1.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   * @see com.utama.madtodo.models.TodoEntity#update()
   */
  @Override
  protected long update() throws IllegalArgumentException, SQLiteException {
    if (TextUtils.isEmpty(name))
      throw new IllegalArgumentException("Task name cannot be empty");

    ContentValues values = buildValues();
    if (update(id, values) == 1)
      return id;
    else
      return -1;
  }


  /**
   * Helper method to update a record from the database using SQL query directly.
   *
   * @param id The id of the local task. Please not that this is NOT the remote task id, since they
   *        serve for different purposes.
   * @param values The values to be updated into the database.
   * @return The number of updated records. In this case, the number will be 1 if the record has
   *         been updated. Otherwise 0.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   */
  private int update(long id, ContentValues values) throws SQLiteException {
    Log.d(TAG, "update");

    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    String whereClause = DbConsts.Column.ID + "=" + id;
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count = db.update(DbConsts.TABLE, values, whereClause, null);
    Log.d(TAG, "Updated records: " + count);

    return count;
  }


  /**
   * Delete a record.
   *
   * @return The id of deleted record on success. Otherwise -1.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   * @throws IllegalArgumentException Thrown when id is invalid.
   * @see com.utama.madtodo.models.TodoEntity#delete()
   */
  @Override
  public long delete() throws SQLiteException, IllegalArgumentException {
    if (id < 0)
      throw new IllegalArgumentException("Invalid id");

    if (delete(id) == 1)
      return id;
    else
      return -1;
  }


  /**
   * Helper method to delete a record from the database using SQL query directly.
   *
   * @param id The id of the local task. Please not that this is NOT the remote task id, since they
   *        serve for different purposes.
   * @return The number of deleted rows from the database. In this case, the number will be 1 if the
   *         record has been deleted. Otherwise 0.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   */
  private int delete(long id) throws SQLiteException {
    Log.d(TAG, "delete");

    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    String whereClause = DbConsts.Column.ID + "=" + id;
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count = db.delete(DbConsts.TABLE, whereClause, null);
    Log.d(TAG, "Deleted records: " + count);

    return count;
  }


  /**
   * Purge all records in the database.
   *
   * @return The number of deleted rows from the database.
   * @throws SQLiteException Thrown when the {@link DbHelper} instance is not set. Use
   *         {@link #setDbHelper(DbHelper)} to do it before executing this method.
   */
  public static long purge() throws SQLiteException {
    if (dbHelper == null)
      throw new SQLiteException("DbHelper instance is not set");

    List<LocalTodo> todos = findAll();
    long deletedRows = todos.size();

    SQLiteDatabase db = dbHelper.getWritableDatabase();
    dbHelper.onUpgrade(db, 0, DbConsts.DB_VERSION);

    return deletedRows;
  }


  /**
   * Builds values for CRU operations.
   *
   * @return The content values for CRU operations.
   */
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


  /**
   * Sets the member variables from a database cursor.
   *
   * @param cur A database cursor.
   */
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
