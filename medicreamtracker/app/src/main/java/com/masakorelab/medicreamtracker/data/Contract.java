package com.masakorelab.medicreamtracker.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names.
 */
public class Contract {

  public static final String CONTENT_AUTHORITY = "com.masakorelab.medicreamtracker";

  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  public static final String PATH_MEDI_CREAM = "medicream";
  public static final String PATH_BODY_PART = "bodypart";
  public static final String PATH_RECORD = "record";

  // To make it easy to query for the exact date, we normalize all dates that go into
  // the database to the start of the the Julian day at UTC.
  public static long normalizeDate(long applyDate) {
    // normalize the date to the beginning of the (UTC) day
    Time time = new Time();
    time.set(applyDate);
    int julianDay = Time.getJulianDay(applyDate, time.gmtoff);
    return time.setJulianDay(julianDay);
  }

  /* Inner class that defines the table contents of the cream name table */
  public static final class MediCreamEntry implements BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEDI_CREAM).build();

    //This is the Android platform's base MIME type for a content: URI containing a Cursor of zero or more items.
    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/" + PATH_MEDI_CREAM;

    //This is the Android platform's base MIME type for a content: URI containing a Cursor of a single item.
    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/" + PATH_MEDI_CREAM;

    // Table name
    public static final String TABLE_NAME = "medicream";

    // Medical cream name: String
    public static final String COLUMN_NAME = "name";

    // Medical cream name's description: String
    public static final String COLUMN_DESCRIPTION = "description";

    //https://developer.android.com/reference/android/content/ContentUris.html
    public static Uri buildMediCreamUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

  }

  public static final class RecordEntry implements BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECORD).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECORD;

    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECORD;

    // Table name
    public static final String TABLE_NAME = "record";

    // Medical cream name: String and foreign key
    public static final String COLUMN_CREAM_NAME = "medicream";

    // When did you apply the cream you selected: Integer
    // Ref: Dates and times in SQLite: https://www.sqlite.org/datatype3.html#section_2_2
    public static final String COLUMN_APPLY_DATE = "date";

    // Which part of your body you applied the cream: String and foreign key
    public static final String COLUMN_PART_OF_BODY = "partofbody";

    public static Uri buildRecordUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildRecordCream(String mediCreamName) {
      return CONTENT_URI.buildUpon().appendPath(mediCreamName).build();
    }

    public static String getMediCreamName(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  public static final class BodyPartEntry implements BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_BODY_PART).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BODY_PART;

    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BODY_PART;

    // Table name
    public static final String TABLE_NAME = "bodypart";

    // Main Body parts name: String
    public static final String COLUMN_CATEGORYNAME = "categoryname";

    // Sub Body parts name: String
    public static final String COLUMN_PARTNAME = "partname";

    public static Uri buildBodyPartUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }
  }
}
