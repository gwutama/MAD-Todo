package com.utama.madtodo.model;

import android.provider.BaseColumns;


public class LocalTodoConsts {

  // DB specific constants
  public static final String DB_NAME = "madtodo.db";
  public static final int DB_VERSION = 1;
  public static final String TABLE = "todos";
  public static final String DEFAULT_SORT = Column.ID + " DESC";

  
  // DB columns constants
  public class Column {
    public static final String ID = BaseColumns._ID;
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String EXPIRY = "expiry";
    public static final String IS_IMPORTANT = "is_important";
    public static final String IS_MARKED_DONE = "is_marked_done";
  }
    
}
