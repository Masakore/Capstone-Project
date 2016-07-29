package com.masakorelab.medicreamtracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.masakorelab.medicreamtracker.data.Contract.*;

public class DBHelper extends SQLiteOpenHelper {

  private static final  int DATABASE_VERSION = 2;

  static final String DATABASE_NAME = "medicream.db";

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {

    final String SQL_CREATE_MEDI_CREAME_TABLE = "CREATE TABLE " + MediCreamEntry.TABLE_NAME + "(" +
       MediCreamEntry._ID + " INTEGER PRIMARY KEY, " +
       MediCreamEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
       MediCreamEntry.COLUMN_DESCRIPTION + " TEXT " +
       " );";

    final String SQL_CREATE_BODY_PARTS_TABLE = "CREATE TABLE " + BodyPartEntry.TABLE_NAME + "(" +
        BodyPartEntry._ID + " INTEGER PRIMARY KEY, " +
        BodyPartEntry.COLUMN_CATEGORYNAME + " TEXT UNIQUE NOT NULL, " +
        BodyPartEntry.COLUMN_PARTNAME + " TEXT " +
        " );";

    final String SQL_CREATE_RECORD_TABLE = "CREATE TABLE " + RecordEntry.TABLE_NAME + "(" +
        RecordEntry._ID + "  INTEGER PRIMARY KEY, " +
        RecordEntry.COLUMN_CREAM_NAME + " INTEGER, " +
        RecordEntry.COLUMN_PART_OF_BODY + " INTEGER, " +
        RecordEntry.COLUMN_APPLY_DATE + " INTEGER, " +

        //Set up foreign key here
        " FOREIGN KEY (" +  RecordEntry.COLUMN_CREAM_NAME + ") REFERENCES " +
        MediCreamEntry.TABLE_NAME + " (" + MediCreamEntry._ID + "), " +

        " FOREIGN KEY (" +  RecordEntry.COLUMN_CREAM_NAME + ") REFERENCES " +
        BodyPartEntry.TABLE_NAME + " (" + BodyPartEntry._ID + "));";

        //ref: https://www.sqlite.org/lang_conflict.html
        //On second thought, we do not need this constraint
        //attempt to prevent from exactly same date date to be created
        //" UNIQUE (" + RecordEntry.COLUMN_APPLY_DATE + ") ON CONFLICT REPLACE" +

    sqLiteDatabase.execSQL(SQL_CREATE_MEDI_CREAME_TABLE);
    sqLiteDatabase.execSQL(SQL_CREATE_BODY_PARTS_TABLE);
    sqLiteDatabase.execSQL(SQL_CREATE_RECORD_TABLE);

  }

  /* Intentionally blank because existing data must be kept even when you change your db schema as much as possible */
  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    // Note that this only fires if you change the version number for your database.
    // It does NOT depend on the version number for your application.

  }
}
