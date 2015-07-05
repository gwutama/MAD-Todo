package com.utama.madtodo.model;

public class DbConsts {

  // DB specific constants
  public static final String DB_NAME = "madtodo.db";
  public static final int DB_VERSION = 1;
  public static final String TABLE = "todos";
  public static final String DEFAULT_SORT = Column.ID + " DESC";

  
  // DB columns constants
  public class Column {
    public static final String ID = "id";
    public static final String REMOTE_ID = "remote_id";    
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String EXPIRY = "expiry";
    public static final String IS_IMPORTANT = "is_important";
    public static final String IS_MARKED_DONE = "is_marked_done";
  }
    
}
