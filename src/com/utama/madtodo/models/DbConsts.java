package com.utama.madtodo.models;


/**
 * The Class DbConsts contains various constants for local persistence using sqlite database.
 */
public class DbConsts {

  /** The Constant DB_NAME represents the database name. */
  public static final String DB_NAME = "madtodo.db";

  /** The Constant DB_VERSION represents the database version. */
  public static final int DB_VERSION = 2;


  public class Table {

    public class Tasks {

      /** The Constant TABLE represent the table name used in the sqlite database. */
      public static final String TABLE = "tasks";

      /**
       * The Constant SORT_IMPORTANCE_DATE represents the todo list sorting by importance+date. This
       * is the "WHERE" clause of the SQL query.
       */
      public static final String SORT_IMPORTANCE_DATE =
          Column.IS_MARKED_DONE + ", " + Column.IS_IMPORTANT + " DESC , " + Column.EXPIRY;

      /**
       * The Constant SORT_DATE_IMPORTANCE represents the todo list sorting by date+importance. This
       * is the "WHERE" clause of the SQL query.
       */
      public static final String SORT_DATE_IMPORTANCE =
          Column.IS_MARKED_DONE + ", " + Column.EXPIRY + ", " + Column.IS_IMPORTANT + " DESC";

      /** The Constant DEFAULT_SORT represents the default sorting on application start. */
      public static final String DEFAULT_SORT = SORT_DATE_IMPORTANCE;

      /**
       * The Class Column represents column names of the table where tasks are saved.
       */
      public class Column {

        /** The unique id. */
        public static final String ID = "id";

        /** The id of tasks on the remote (web service) side. */
        public static final String REMOTE_ID = "remote_id";

        /** The task's name. */
        public static final String NAME = "name";

        /** The task's description. */
        public static final String DESCRIPTION = "description";

        /** The taks's due date. */
        public static final String EXPIRY = "expiry";

        /** Whether the task is important or has higher priority. */
        public static final String IS_IMPORTANT = "is_important";

        /** Whether the task was marked as done. */
        public static final String IS_MARKED_DONE = "is_marked_done";
      }

    }

    public class Contacts {

      /** The Constant TABLE represent the table name used in the sqlite database. */
      public static final String TABLE = "contacts";

      /**
       * The Class Column represents column names of the table where tasks are saved.
       */
      public class Column {
        public static final String ID = "id";
        public static final String TODOS_ID = "todos_id";
        public static final String CONTACTS_ID = "contacts_id";
      }
    }

  }

}
