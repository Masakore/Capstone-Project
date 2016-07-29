package com.masakorelab.medicreamtracker.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names.
 */
public class Contract {

  /* Inner class that defines the table contents of the cream name table */
  public static final class MediCreamEntry implements BaseColumns {

    // Table name
    public static final String TABLE_NAME = "medicream";

    // Medical cream name: String
    public static final String COLUMN_NAME = "name";

    // Medical cream name's description: String
    public static final String COLUMN_DESCRIPTION = "description";

  }

  public static final class RecordEntry implements BaseColumns {

    // Table name
    public static final String TABLE_NAME = "record";

    // Medical cream name: String and foreign key
    public static final String COLUMN_CREAM_NAME = "medicream";

    // When did you apply the cream you selected: Integer
    // Ref: Dates and times in SQLite: https://www.sqlite.org/datatype3.html#section_2_2
    public static final String COLUMN_APPLY_DATE = "date";

    // Which part of your body you applied the cream: String and foreign key
    public static final String COLUMN_PART_OF_BODY = "partofbody";

  }

  public static final class BodyPartEntry implements BaseColumns {

    // Table name
    public static final String TABLE_NAME = "bodypart";

    // Main Body parts name: String
    public static final String COLUMN_CATEGORYNAME = "categoryname";

    // Sub Body parts name: String
    public static final String COLUMN_PARTNAME = "partname";

  }
}
