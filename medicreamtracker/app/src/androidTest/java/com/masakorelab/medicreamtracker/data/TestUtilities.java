package com.masakorelab.medicreamtracker.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase{

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

  static ContentValues createBodyPartValues() {
    ContentValues bodyPartValue = new ContentValues();
    bodyPartValue.put(Contract.BodyPartEntry.COLUMN_CATEGORYNAME, "Face");
    bodyPartValue.put(Contract.BodyPartEntry.COLUMN_PARTNAME, "Nose");

    return bodyPartValue;
  }

  static ContentValues createRecordValues(int mediCreamId, int bodyPartId) {
    ContentValues recordValue = new ContentValues();
    recordValue.put(Contract.RecordEntry.COLUMN_CREAM_NAME, mediCreamId);
    recordValue.put(Contract.RecordEntry.COLUMN_PART_OF_BODY, bodyPartId);
    // How to handle date: http://stackoverflow.com/questions/7363112/best-way-to-work-with-dates-in-android-sqlite
    recordValue.put(Contract.RecordEntry.COLUMN_APPLY_DATE, System.currentTimeMillis());

    return recordValue;
  }

}
