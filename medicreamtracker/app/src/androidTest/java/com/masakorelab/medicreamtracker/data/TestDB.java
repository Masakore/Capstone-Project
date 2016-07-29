package com.masakorelab.medicreamtracker.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDB extends AndroidTestCase{

  public static final String LOG_TAG = TestDB.class.getSimpleName();

  void deleteTheDatabase() {
    mContext.deleteDatabase(DBHelper.DATABASE_NAME);
  }

  public void setUp() {
    deleteTheDatabase();
  }

  public void testCreateDb() throws Throwable {
    // build a HashSet of all of the table names we wish to look for
    // Note that there will be another table in the DB that stores the
    // Android metadata (db version information)
    final HashSet<String> tableNameHashSet = new HashSet<String>();
    tableNameHashSet.add(Contract.MediCreamEntry.TABLE_NAME);
    tableNameHashSet.add(Contract.BodyPartEntry.TABLE_NAME);
    tableNameHashSet.add(Contract.RecordEntry.TABLE_NAME);

    mContext.deleteDatabase(DBHelper.DATABASE_NAME);
    SQLiteDatabase db = new DBHelper(
        this.mContext).getWritableDatabase();
    assertEquals(true, db.isOpen());

    // have we created the tables we want?
    Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

    assertTrue("Error: This means that the database has not been created correctly",
        c.moveToFirst());

    // verify that the tables have been created
    do {
      tableNameHashSet.remove(c.getString(0));
    } while( c.moveToNext() );

    // if this fails, it means that your database doesn't contain without any entry tables
    assertTrue("Error: Your database was created without any entry tables",
        tableNameHashSet.isEmpty());

    // TEST for MediCreamTABEL start------------------------
    // now, do our tables contain the correct columns?
    c = db.rawQuery("PRAGMA table_info(" + Contract.MediCreamEntry.TABLE_NAME + ")",
        null);

    assertTrue("Error: This means that we were unable to query the database for Medi Cream table information.",
        c.moveToFirst());

    // Build a HashSet of all of the column names we want to look for
    final HashSet<String> MediCreamColumnHashSet = new HashSet<String>();
    MediCreamColumnHashSet.add(Contract.MediCreamEntry._ID);
    MediCreamColumnHashSet.add(Contract.MediCreamEntry.COLUMN_NAME);
    MediCreamColumnHashSet.add(Contract.MediCreamEntry.COLUMN_DESCRIPTION);

    int MediCreamColumnNameIndex = c.getColumnIndex("name");
    do {
      String columnName = c.getString(MediCreamColumnNameIndex);
      MediCreamColumnHashSet.remove(columnName);
    } while(c.moveToNext());

    // if this fails, it means that your database doesn't contain all of the required Medi Cream
    // entry columns
    assertTrue("Error: The database doesn't contain all of the required Medi Cream entry columns",
      MediCreamColumnHashSet.isEmpty());
    // TEST for MediCreamTABEL end------------------------

    // TEST for bodypart table start------------------------
    // now, do our tables contain the correct columns?
    c = db.rawQuery("PRAGMA table_info(" + Contract.BodyPartEntry.TABLE_NAME + ")",
        null);

    assertTrue("Error: This means that we were unable to query the database for Body part information.",
        c.moveToFirst());

    // Build a HashSet of all of the column names we want to look for
    final HashSet<String> bodyPartColumnHashSet = new HashSet<String>();
    bodyPartColumnHashSet.add(Contract.BodyPartEntry._ID);
    bodyPartColumnHashSet.add(Contract.BodyPartEntry.COLUMN_CATEGORYNAME);
    bodyPartColumnHashSet.add(Contract.BodyPartEntry.COLUMN_PARTNAME);

    int bodyPartColumnNameIndex = c.getColumnIndex("name");
    do {
      String columnName = c.getString(bodyPartColumnNameIndex);
      bodyPartColumnHashSet.remove(columnName);
    } while(c.moveToNext());

    // if this fails, it means that your database doesn't contain all of the required Medi Cream
    // entry columns
    assertTrue("Error: The database doesn't contain all of the required Body Part entry columns",
        bodyPartColumnHashSet.isEmpty());
    // TEST for bodypart table end------------------------

    // TEST for record table start------------------------
    c = db.rawQuery("PRAGMA table_info(" + Contract.RecordEntry.TABLE_NAME + ")",
        null);

    assertTrue("Error: This means that we were unable to query Record for Record table information.",
        c.moveToFirst());

    // Build a HashSet of all of the column names we want to look for
    final HashSet<String> recordColumnHashSet = new HashSet<String>();
    recordColumnHashSet.add(Contract.RecordEntry._ID);
    recordColumnHashSet.add(Contract.RecordEntry.COLUMN_CREAM_NAME);
    recordColumnHashSet.add(Contract.RecordEntry.COLUMN_PART_OF_BODY);
    recordColumnHashSet.add(Contract.RecordEntry.COLUMN_APPLY_DATE);

    int recordColumnNameIndex = c.getColumnIndex("name");
    do {
      String columnName = c.getString(recordColumnNameIndex);
      recordColumnHashSet.remove(columnName);
    } while(c.moveToNext());

    // if this fails, it means that your database doesn't contain all of the required Medi Cream
    // entry columns
    assertTrue("Error: The database doesn't contain all of the required Record entry columns",
        bodyPartColumnHashSet.isEmpty());
    // TEST for record table start------------------------

    db.close();
  }

  public void testMediCreamTable() {

    // First step: Get reference to writable database
    // If there's an error in those massive SQL table creation Strings,
    // errors will be thrown here when you try to get a writable database.
    DBHelper dbHelper = new DBHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    // Second Step : Create values
    ContentValues mediCreamValues = TestUtilities.createMediCreamValues();

    // Third Step : Insert ContentValues into database and get a row ID back
    long mediCreamRowId = db.insert(Contract.MediCreamEntry.TABLE_NAME, null, mediCreamValues);
    assertTrue(mediCreamRowId != -1);

    // Fourth Step: Query the database and receive a Cursor back
    // A cursor is your primary interface to the query results.
    Cursor mediCreamCursor = db.query(
        Contract.MediCreamEntry.TABLE_NAME,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertTrue("Error: No Records returned from medicream query", mediCreamCursor.moveToFirst());

    // Fifth Step: Validate the medicream Query
    TestUtilities.validateCursor(" testInsertReadDb mediCreamEntry failed to validate", mediCreamCursor, mediCreamValues);

    // Sixth Step: Close cursor and database
    mediCreamCursor.close();
    dbHelper.close();
  }

  public void testBodyPartTable() {
    DBHelper dbHelper = new DBHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues bodyPartValues = TestUtilities.createBodyPartValues();

    long bodyPartRowId = db.insert(Contract.BodyPartEntry.TABLE_NAME, null, bodyPartValues);
    assertTrue(bodyPartRowId != -1);

    Cursor bodyPartCursor = db.query(
        Contract.BodyPartEntry.TABLE_NAME,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertTrue("Error: No Records returned from body part query", bodyPartCursor.moveToFirst());

    TestUtilities.validateCursor(" testInsertReadDb BodyPartEntry failed to validate", bodyPartCursor, bodyPartValues);

    bodyPartCursor.close();
    dbHelper.close();
  }

  public void testRecordTable() {
    DBHelper dbHelper = new DBHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues recordValues = TestUtilities.createRecordValues(12, 3);

    long recordRowId = db.insert(Contract.RecordEntry.TABLE_NAME, null, recordValues);
    assertTrue(recordRowId != -1);

    Cursor recordCursor = db.query(
        Contract.RecordEntry.TABLE_NAME,
        null,
        null,
        null,
        null,
        null,
        null
    );

    assertTrue("Error: No Records returned from query", recordCursor.moveToFirst());

    TestUtilities.validateCursor(" testInsertReadDb RecordEntry failed to validate", recordCursor, recordValues);

    recordCursor.close();
    dbHelper.close();
  }
}
