package com.masakorelab.medicreamtracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.masakorelab.medicreamtracker.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase{
  static final String TEST_CREAM_NAME = "Vaserin";

  static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValue) {
    assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
    validateCurrentRecord(error, valueCursor, expectedValue);
    valueCursor.close();
  }

  static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
    Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
    for (Map.Entry<String, Object> entry: valueSet) {
      String columnName = entry.getKey();
      int index = valueCursor.getColumnIndex(columnName);
      assertFalse("'Column '" + columnName + "' not found.'" + error, index == -1);
      String expectedValue = entry.getValue().toString();
      assertEquals("'Value '" + entry.getValue().toString() +
        "' did not match the expected value '" +
        expectedValue + "." + error, expectedValue, valueCursor.getString(index));
    }

  }

  static ContentValues createMediCreamValues() {
    ContentValues mediCreamValue = new ContentValues();
    mediCreamValue.put(Contract.MediCreamEntry.COLUMN_NAME, "Vaserin");
    mediCreamValue.put(Contract.MediCreamEntry.COLUMN_DESCRIPTION, "One day two times in the Morning and night");

    return mediCreamValue;
  }

  static long insertMediCreamValues(Context context) {
    DBHelper dbHelper = new DBHelper(context);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues testValues = TestUtilities.createMediCreamValues();

    long mediCreamRowId;
    mediCreamRowId = db.insert(Contract.MediCreamEntry.TABLE_NAME, null, testValues);

    assertTrue("Error: Failure to insert Medi Cream entry", mediCreamRowId != -1);

    return mediCreamRowId;
  }

  static ContentValues createBodyPartValues() {
    ContentValues bodyPartValue = new ContentValues();
    bodyPartValue.put(Contract.BodyPartEntry.COLUMN_CATEGORYNAME, "Face");
    bodyPartValue.put(Contract.BodyPartEntry.COLUMN_PARTNAME, "Nose");

    return bodyPartValue;
  }

  static ContentValues createRecordValues(long mediCreamId, long bodyPartId) {
    ContentValues recordValue = new ContentValues();
    recordValue.put(Contract.RecordEntry.COLUMN_CREAM_NAME, mediCreamId);
    recordValue.put(Contract.RecordEntry.COLUMN_PART_OF_BODY, bodyPartId);
    // How to handle date: http://stackoverflow.com/questions/7363112/best-way-to-work-with-dates-in-android-sqlite
    recordValue.put(Contract.RecordEntry.COLUMN_APPLY_DATE, System.currentTimeMillis());

    return recordValue;
  }

  /*
     The functions we provide inside of TestProvider use this utility class to test
     the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
     CTS tests.
     Note that this only tests that the onChange function is called; it does not test that the
     correct Uri is returned.
  */
  static class TestContentObserver extends ContentObserver {
    final HandlerThread mHT;
    boolean mContentChanged;

    static TestContentObserver getTestContentObserver() {
      HandlerThread ht = new HandlerThread("ContentObserverThread");
      ht.start();
      return new TestContentObserver(ht);
    }

    private TestContentObserver(HandlerThread ht) {
      super(new Handler(ht.getLooper()));
      mHT = ht;
    }

    // On earlier versions of Android, this onChange method is called
    @Override
    public void onChange(boolean selfChange) {
      onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
      mContentChanged = true;
    }

    public void waitForNotificationOrFail() {
      // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
      // It's useful to look at the Android CTS source for ideas on how to test your Android
      // applications.  The reason that PollingCheck works is that, by default, the JUnit
      // testing framework is not running on the main Android application thread.
      new PollingCheck(5000) {
        @Override
        protected boolean check() {
          return mContentChanged;
        }
      }.run();
      mHT.quit();
    }
  }

  static TestContentObserver getTestContentObserver() {
    return TestContentObserver.getTestContentObserver();
  }
}
