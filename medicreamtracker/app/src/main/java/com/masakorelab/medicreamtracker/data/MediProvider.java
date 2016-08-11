package com.masakorelab.medicreamtracker.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MediProvider extends ContentProvider{

  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private DBHelper mOpenHelper;

  static final int MEDI_CREAM = 100;

  static final int BODY_PART = 200;

  static final int RECORD = 300;
  static final int RECORD_CREAME_NAME = 301;
  static final int JOINED_RECORD = 302;

  private static final SQLiteQueryBuilder sRecordByMediCreamQueryBuilder;
  private static final SQLiteQueryBuilder sRecordByMediCreamAndBodyPartQueryBuilder;

  static {
    sRecordByMediCreamQueryBuilder = new SQLiteQueryBuilder();

    sRecordByMediCreamQueryBuilder.setTables(
        Contract.RecordEntry.TABLE_NAME + " INNER JOIN " +
            Contract.MediCreamEntry.TABLE_NAME +
            " ON " + Contract.RecordEntry.TABLE_NAME +
            "." + Contract.RecordEntry.COLUMN_CREAM_NAME +
            " = " + Contract.MediCreamEntry.TABLE_NAME +
            "." + Contract.MediCreamEntry._ID);

    sRecordByMediCreamAndBodyPartQueryBuilder = new SQLiteQueryBuilder();

    sRecordByMediCreamAndBodyPartQueryBuilder.setTables(
        Contract.RecordEntry.TABLE_NAME +
            " INNER JOIN " +
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
            "." + Contract.BodyPartEntry._ID
    );
  }

  private static final String sMediCreamName = Contract.MediCreamEntry.TABLE_NAME + "." + Contract.MediCreamEntry.COLUMN_NAME + " = ? ";

  private Cursor getRecordByMediCreamName(Uri uri, String[] projection, String sortOrder) {
    String mediCreamName = Contract.RecordEntry.getMediCreamName(uri);

    String[] selectionArgs = new String[]{mediCreamName};
    String selection = sMediCreamName;

    //https://developer.android.com/reference/android/database/sqlite/SQLiteQueryBuilder.html#query(android.database.sqlite.SQLiteDatabase, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    return  sRecordByMediCreamQueryBuilder.query(mOpenHelper.getReadableDatabase(),
      projection, //projection = select in sql
      selection, //select = where in sql
      selectionArgs, //selectArgs = condition of where in sql
      null, //groupBy
      null, //having
      sortOrder);
  }

  private Cursor getJoinedRecord(String[] projection, String selection, String[] selectionArgs, String sortOrder) {

    return  sRecordByMediCreamAndBodyPartQueryBuilder.query(mOpenHelper.getReadableDatabase(),
        projection, //projection = select in sql
        selection, //select = where in sql
        selectionArgs, //selectArgs = condition of where in sql
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
    matcher.addURI(authority, Contract.PATH_JOINED_RECORD_TABLE, JOINED_RECORD);

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

    Cursor retCursor;
    switch (sUriMatcher.match(uri)) {

      case RECORD_CREAME_NAME: {
        retCursor = getRecordByMediCreamName(uri, projection, sortOrder);
        break;
      }

      case RECORD: {
        retCursor = mOpenHelper.getReadableDatabase().query(
            Contract.RecordEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
        break;
      }

      case MEDI_CREAM: {
        retCursor = mOpenHelper.getReadableDatabase().query(
            Contract.MediCreamEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
        break;
      }

      case BODY_PART: {
        retCursor = mOpenHelper.getReadableDatabase().query(
            Contract.BodyPartEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
        break;
      }
      case JOINED_RECORD: {
        retCursor = getJoinedRecord(projection, selection, selectionArgs, sortOrder);
        break;
      }
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
    return retCursor;
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
      case JOINED_RECORD:
        return Contract.RecordEntry.CONTENT_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri" + uri);
    }
  }

  @Nullable
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Uri returnUri;

    switch(match) {
      case RECORD: {
        normalizeDate(values);
        long _id = db.insert(Contract.RecordEntry.TABLE_NAME, null, values);
        if ( _id > 0 )
          returnUri = Contract.RecordEntry.buildRecordUri(_id);
        else
          //Seems no need to add try & catch with this...
          throw new android.database.SQLException("Failed to insert row into " + uri);
        break;
      }
      case MEDI_CREAM: {
        long _id = db.insert(Contract.MediCreamEntry.TABLE_NAME, null, values);
        if ( _id > 0 )
          returnUri = Contract.MediCreamEntry.buildMediCreamUri(_id);
        else
          throw new android.database.SQLException("Failed to insert row into " + uri);
        break;
      }
      case BODY_PART: {
        long _id = db.insert(Contract.BodyPartEntry.TABLE_NAME, null, values);
        if ( _id > 0 )
          returnUri = Contract.BodyPartEntry.buildBodyPartUri(_id);
        else
          throw new android.database.SQLException("Failed to insert row into " + uri);
        break;
      }
      default:
        throw new UnsupportedOperationException("Unknown uri:" + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnUri;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowDeleted;

    if ( null == selection ) selection = "1";
    switch (match) {
      case MEDI_CREAM:
        rowDeleted = db.delete(Contract.MediCreamEntry.TABLE_NAME, selection, selectionArgs);
        break;
      case BODY_PART:
        rowDeleted = db.delete(Contract.BodyPartEntry.TABLE_NAME, selection, selectionArgs);
        break;
      case RECORD:
        rowDeleted = db.delete(Contract.RecordEntry.TABLE_NAME, selection, selectionArgs);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri:" + uri + " Match:" + match);
    }
    if ( rowDeleted != 0 ) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowDeleted;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsUpdated;

    switch (match) {
      case RECORD:
        normalizeDate(values);
        rowsUpdated = db.update(Contract.RecordEntry.TABLE_NAME, values, selection,
            selectionArgs);
        break;
      case MEDI_CREAM:
        rowsUpdated = db.update(Contract.MediCreamEntry.TABLE_NAME, values, selection,
            selectionArgs);
        break;
      case BODY_PART:
        rowsUpdated = db.update(Contract.BodyPartEntry.TABLE_NAME, values, selection,
            selectionArgs);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (rowsUpdated != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsUpdated;
  }

  private void normalizeDate(ContentValues values) {
    // normalize the date value
    if (values.containsKey(Contract.RecordEntry.COLUMN_APPLY_DATE)) {
      long dateValue = values.getAsLong(Contract.RecordEntry.COLUMN_APPLY_DATE);
      values.put(Contract.RecordEntry.COLUMN_APPLY_DATE, Contract.normalizeDate(dateValue));
    }
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
