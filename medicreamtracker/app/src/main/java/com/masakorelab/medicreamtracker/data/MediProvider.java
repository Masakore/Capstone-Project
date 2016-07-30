package com.masakorelab.medicreamtracker.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MediProvider extends ContentProvider{

  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private DBHelper mOpenHelper;

  static final int MEDI_CREAM = 100;
  static final int MEDI_CREAM_ID = 101;
  static final int MEDI_CREAM_NAME = 102;
  static final int MEDI_CREAM_DESCRIPTION = 103;

  static final int BODY_PART = 200;
  static final int BODY_PART_ID = 201;
  static final int BODY_PART_CATEGORYNAME = 202;
  static final int BODY_PART_PARTNAME = 203;

  static final int RECORD = 300;
  static final int RECORD_ID = 301;
  static final int RECORD_CREAME_NAME = 302;
  static final int RECORD_APPLY_DATE = 302;
  static final int RECORD_PART_OF_BODY = 302;

  private static final SQLiteQueryBuilder sRecordByMediCreamAndBodyPartQueryBuilder;

  static {
    sRecordByMediCreamAndBodyPartQueryBuilder = new SQLiteQueryBuilder();

    sRecordByMediCreamAndBodyPartQueryBuilder.setTables(
        Contract.RecordEntry.TABLE_NAME + " INNER JOIN " +
            Contract.MediCreamEntry.TABLE_NAME +
            " ON " + Contract.RecordEntry.TABLE_NAME +
            "." + Contract.RecordEntry.COLUMN_CREAM_NAME +
            " = " + Contract.MediCreamEntry.TABLE_NAME +
            "." + Contract.MediCreamEntry._ID +
            " INNER JOIN " +
            Contract.BodyPartEntry.TABLE_NAME +
            " ON " + Contract.RecordEntry.TABLE_NAME +
            "." + Contract.RecordEntry.COLUMN_PART_OF_BODY +
            " = " + Contract.BodyPartEntry.TABLE_NAME +
            "." + Contract.BodyPartEntry._ID);
  }

  private Cursor getRecordByMediCreamName(String sortOrder) {
    //https://developer.android.com/reference/android/database/sqlite/SQLiteQueryBuilder.html#query(android.database.sqlite.SQLiteDatabase, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    return  sRecordByMediCreamAndBodyPartQueryBuilder.query(mOpenHelper.getReadableDatabase(),
      null, //projection = select in sql
      null, //select = where in sql
      null, //selectArgs = condition of where in sql
      null, //groupBy
      null, //having
      sortOrder);
  }

  //https://developer.android.com/reference/android/content/UriMatcher.html
  static UriMatcher buildUriMatcher() {
    // All paths added to the UriMatcher have a corresponding code to return when a match is
    // found.  The code passed into the constructor represents the code to return for the root
    // URI.  It's common to use NO_MATCH as the code for this case.
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = Contract.CONTENT_AUTHORITY;

    // For each type of URI you want to add, create a corresponding code.
    matcher.addURI(authority, Contract.PATH_MEDI_CREAM, MEDI_CREAM);

    matcher.addURI(authority, Contract.PATH_BODY_PART, BODY_PART);

    matcher.addURI(authority, Contract.PATH_RECORD, RECORD);
    matcher.addURI(authority, Contract.PATH_RECORD + "/*", RECORD_CREAME_NAME);

    return matcher;
  }


  @Override
  public boolean onCreate() {
    mOpenHelper = new DBHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    return null;
  }

  @Nullable
  @Override
  public String getType(Uri uri) {

    final int match = sUriMatcher.match(uri);

    switch (match) {
      case MEDI_CREAM:
        return Contract.MediCreamEntry.CONTENT_TYPE;
      case BODY_PART:
        return Contract.BodyPartEntry.CONTENT_TYPE;
      case RECORD:
        return Contract.RecordEntry.CONTENT_TYPE;
      case RECORD_CREAME_NAME:
        return Contract.RecordEntry.CONTENT_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri" + uri);
    }
  }

  @Nullable
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    return null;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    return 0;
  }

  // You do not need to call this method. This is a method specifically to assist the testing
  // framework in running smoothly. You can read more at:
  // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
  @Override
  @TargetApi(11)
  public void shutdown() {
    mOpenHelper.close();
    super.shutdown();
  }
}
