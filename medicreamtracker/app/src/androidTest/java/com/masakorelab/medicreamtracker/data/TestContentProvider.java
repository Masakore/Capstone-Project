package com.masakorelab.medicreamtracker.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

public class TestContentProvider extends AndroidTestCase{

  public static final String LOG_TAG = TestContentProvider.class.getSimpleName();

  public void deleteAllRecordsFromProvider() {
    mContext.getContentResolver().delete(
        Contract.RecordEntry.CONTENT_URI,
        null,
        null
    );

    mContext.getContentResolver().delete(
        Contract.MediCreamEntry.CONTENT_URI,
        null,
        null
    );

    mContext.getContentResolver().delete(
        Contract.BodyPartEntry.CONTENT_URI,
        null,
        null
        );

    Cursor cursor = mContext.getContentResolver().query(
        Contract.RecordEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    assertEquals("Error: Records not deleted from Record table during test initialization", 0, cursor.getCount());
    cursor.close();

    cursor = mContext.getContentResolver().query(
        Contract.MediCreamEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    assertEquals("Error: Records not deleted from Medi Cream table during test initialization", 0, cursor.getCount());
    cursor.close();

    cursor = mContext.getContentResolver().query(
        Contract.BodyPartEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    assertEquals("Error: Records not deleted Body part table during test initialization", 0, cursor.getCount());
    cursor.close();

  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
      deleteAllRecordsFromProvider();
  }

  public void testProviderRegistry() {
    PackageManager pm = mContext.getPackageManager();

    ComponentName componentName = new ComponentName(mContext.getPackageName(),MediProvider.class.getName());

    try {
      ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

      assertEquals("Error: MediProvider registered with authority: " + providerInfo.authority +
        " instead of authority: " + Contract.CONTENT_AUTHORITY,
          providerInfo.authority, Contract.CONTENT_AUTHORITY);

    } catch (PackageManager.NameNotFoundException e) {
      assertTrue("Error: MediProvider not registered at " + mContext.getPackageName(),false);
    }
  }

  public void testGetType() {

    String type = mContext.getContentResolver().getType(Contract.RecordEntry.CONTENT_URI);
    assertEquals("Error: the RecordEntry CONTENT_URI should return RecordEntry.CONTENT_URI",
        Contract.RecordEntry.CONTENT_TYPE, type);

    String creamName = "testcream";
    type = mContext.getContentResolver().getType(Contract.RecordEntry.buildRecordCream(creamName));
    assertEquals("Error: the RecordEntry CONTENT_URI with cream name should return RecordEntry.CONTENT_URI",
        Contract.RecordEntry.CONTENT_TYPE, type);

    type = mContext.getContentResolver().getType(Contract.MediCreamEntry.CONTENT_URI);
    assertEquals("Error: the RecordEntry CONTENT_URI should return MediCreamEntry.CONTENT_URI",
        Contract.MediCreamEntry.CONTENT_TYPE, type);

    type = mContext.getContentResolver().getType(Contract.BodyPartEntry.CONTENT_URI);
    assertEquals("Error: the RecordEntry CONTENT_URI should return BodyPartEntry.CONTENT_URI",
        Contract.BodyPartEntry.CONTENT_TYPE, type);
  }

  public void testBasicMediCreamQuery() {
    DBHelper dbHelper = new DBHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues testValues = TestUtilities.createMediCreamValues();

    long retRowId = db.insert(Contract.MediCreamEntry.TABLE_NAME, null, testValues);
    assertTrue("Unable to Insert Medi Cream entry into the Database", retRowId != -1);

    db.close();

    Cursor cursor = mContext.getContentResolver().query(
        Contract.MediCreamEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    TestUtilities.validateCursor("testBasicMediCreamQuery", cursor, testValues);
    cursor.close();
  }

  public void testBasicBodyPartQuery() {
    DBHelper dbHelper = new DBHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    ContentValues testValues = TestUtilities.createBodyPartValues();

    long retRowId = db.insert(Contract.BodyPartEntry.TABLE_NAME, null, testValues);
    assertTrue("Unable to Insert Body Part entry into the Database", retRowId != -1);

    db.close();

    Cursor cursor = mContext.getContentResolver().query(
        Contract.BodyPartEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    TestUtilities.validateCursor("testBasicBodyPartQuery", cursor, testValues);
    cursor.close();
  }

  public void testBasicRecordQuery() {
    DBHelper dbHelper = new DBHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();


    /* Prepare Medi Cream Id and Body part Id are used for foreign key.
    Note: currently providing query to Record table only with Medi Cream Name. */
    long mediCreamRowId = TestUtilities.insertMediCreamValues(mContext);
    ContentValues testValues = TestUtilities.createRecordValues(mediCreamRowId, 3);

    long retRowId = db.insert(Contract.RecordEntry.TABLE_NAME, null, testValues);
    assertTrue("Unable to Insert Record entry into the Database", retRowId != -1);

    db.close();

    Cursor cursor = mContext.getContentResolver().query(
        Contract.RecordEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    TestUtilities.validateCursor("testBasicRecordQuery", cursor, testValues);
    cursor.close();
  }

  //Just picked up one table for testing
  public void testUpdateMediCream() {
    ContentValues values = TestUtilities.createMediCreamValues();
    Uri mediCreamUri = mContext.getContentResolver().insert(Contract.MediCreamEntry.CONTENT_URI, values);
    long mediCreamId = ContentUris.parseId(mediCreamUri);

    assertTrue(mediCreamId != -1);
    Log.d(LOG_TAG, "New row id " + mediCreamId);

    ContentValues updateValues = new ContentValues(values);
    updateValues.put(Contract.MediCreamEntry._ID, mediCreamId);
    updateValues.put(Contract.MediCreamEntry.COLUMN_NAME, "Bug Killer");
    updateValues.put(Contract.MediCreamEntry.COLUMN_DESCRIPTION, "To expel bugs");

    Cursor mediCreamCursor = mContext.getContentResolver().query(Contract.MediCreamEntry.CONTENT_URI, null, null, null, null);

    TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
    mediCreamCursor.registerContentObserver(tco);

    int count = mContext.getContentResolver().update(
        Contract.MediCreamEntry.CONTENT_URI, updateValues, Contract.MediCreamEntry._ID + "= ?", new String[]{Long.toString(mediCreamId)});
    assertEquals(count, 1);

    tco.waitForNotificationOrFail();

    mediCreamCursor.unregisterContentObserver(tco);
    mediCreamCursor.close();

    Cursor cursor = mContext.getContentResolver().query(
        Contract.MediCreamEntry.CONTENT_URI,
        null,
        Contract.MediCreamEntry._ID + " + " + mediCreamId,
        null,
        null
    );

    TestUtilities.validateCursor("Error: testUpdateMediCream", cursor, updateValues);
    cursor.close();
  }

  //https://github.com/udacity/Advanced_Android_Development/blob/8.07_Adding_Attributions/app/src/androidTest/java/com/example/android/sunshine/app/data/TestProvider.java
  public void testInsertReadProvider() {
    ContentValues testMediCreamValues = TestUtilities.createMediCreamValues();

    TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
    mContext.getContentResolver().registerContentObserver(Contract.MediCreamEntry.CONTENT_URI, true, tco);
    Uri mediCreamUri = mContext.getContentResolver().insert(Contract.MediCreamEntry.CONTENT_URI, testMediCreamValues);

    tco.waitForNotificationOrFail();
    mContext.getContentResolver().unregisterContentObserver(tco);

    long mediCreamRowId = ContentUris.parseId(mediCreamUri);

    assertTrue(mediCreamRowId != -1);

    Cursor mediCreamCursor = mContext.getContentResolver().query(
        Contract.MediCreamEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    TestUtilities.validateCursor("testInsertReadProvider. Error validating MediCreamEntry.", mediCreamCursor, testMediCreamValues);

    ContentValues testBodyPartValues = TestUtilities.createBodyPartValues();

    tco = TestUtilities.getTestContentObserver();
    mContext.getContentResolver().registerContentObserver(Contract.BodyPartEntry.CONTENT_URI, true, tco);
    Uri bodyPartUri = mContext.getContentResolver().insert(Contract.BodyPartEntry.CONTENT_URI, testBodyPartValues);

    tco.waitForNotificationOrFail();
    mContext.getContentResolver().unregisterContentObserver(tco);

    long bodyPartRowId = ContentUris.parseId(bodyPartUri);

    assertTrue(bodyPartRowId != -1);

    Cursor bodyPartCursor = mContext.getContentResolver().query(
        Contract.BodyPartEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    TestUtilities.validateCursor("testInsertReadProvider. Error validating bodyPartEntry.", bodyPartCursor, testBodyPartValues);

    long testMediCreamRowId = ContentUris.parseId(mediCreamUri);
    assertTrue(testMediCreamRowId != -1);

    long testBodyPartRowId = ContentUris.parseId(bodyPartUri);
    assertTrue(testBodyPartRowId != -1);

    ContentValues testRecordValues = TestUtilities.createRecordValues(testMediCreamRowId, testBodyPartRowId);

    tco = TestUtilities.getTestContentObserver();
    mContext.getContentResolver().registerContentObserver(Contract.RecordEntry.CONTENT_URI, true, tco);
    Uri recortUri = mContext.getContentResolver().insert(Contract.RecordEntry.CONTENT_URI, testRecordValues);

    tco.waitForNotificationOrFail();
    mContext.getContentResolver().unregisterContentObserver(tco);

    long recordRowId = ContentUris.parseId(recortUri);

    assertTrue(recordRowId != -1);

    Cursor recordCursor = mContext.getContentResolver().query(
        Contract.RecordEntry.CONTENT_URI,
        null,
        null,
        null,
        null
    );

    TestUtilities.validateCursor("testInsertReadProvider. Error validating RecordEntry.", recordCursor, testRecordValues);

    // Add the location values in with the weather data so that we can make
    // sure that the join worked and we actually get all the values back
    testRecordValues.putAll(testMediCreamValues);

    recordCursor = mContext.getContentResolver().query(
        Contract.RecordEntry.buildRecordCream(TestUtilities.TEST_CREAM_NAME),
        null,
        null,
        null,
        null
    );
    TestUtilities.validateCursor("testInsertReadProvider. Error validating RecordEntry.", recordCursor, testRecordValues);
  }

  public void testDeleteRecords() {
    testInsertReadProvider();

    TestUtilities.TestContentObserver mediCreamObserver = TestUtilities.getTestContentObserver();
    mContext.getContentResolver().registerContentObserver(Contract.MediCreamEntry.CONTENT_URI, true, mediCreamObserver);

    TestUtilities.TestContentObserver bodyPartObserver = TestUtilities.getTestContentObserver();
    mContext.getContentResolver().registerContentObserver(Contract.BodyPartEntry.CONTENT_URI, true, bodyPartObserver);

    TestUtilities.TestContentObserver recordObserver = TestUtilities.getTestContentObserver();
    mContext.getContentResolver().registerContentObserver(Contract.RecordEntry.CONTENT_URI, true, recordObserver);

    deleteAllRecordsFromProvider();

    mediCreamObserver.waitForNotificationOrFail();
    bodyPartObserver.waitForNotificationOrFail();
    recordObserver.waitForNotificationOrFail();

    mContext.getContentResolver().unregisterContentObserver(mediCreamObserver);
    mContext.getContentResolver().unregisterContentObserver(bodyPartObserver);
    mContext.getContentResolver().unregisterContentObserver(recordObserver);
  }
}
