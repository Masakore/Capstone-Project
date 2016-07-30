package com.masakorelab.medicreamtracker.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase{
  private static final String creamName = "Anti-inflammatory";
  private static final String bodyPartName = "face";


  // Set Uri for test here
  private static final Uri TEST_MEDI_CREAM_DIR = Contract.MediCreamEntry.CONTENT_URI;

  private static final Uri TEST_BODY_PART_DIR = Contract.BodyPartEntry.CONTENT_URI;

  private static final Uri TEST_RECORD_DIR = Contract.RecordEntry.CONTENT_URI;
  private static final Uri TEST_RECORD_WITH_MEDI_CREAM_DIR = Contract.RecordEntry.buildRecordCream(creamName);

  public void testUriMatcher() {
    UriMatcher testMatcher = MediProvider.buildUriMatcher();

    assertEquals("Error: The Medi Cream URI was matched incorrectly.", testMatcher.match(TEST_MEDI_CREAM_DIR), MediProvider.MEDI_CREAM);
    assertEquals("Error: The Body Part URI was matched incorrectly.", testMatcher.match(TEST_BODY_PART_DIR), MediProvider.BODY_PART);
    assertEquals("Error: The Record URI was matched incorrectly.", testMatcher.match(TEST_RECORD_DIR), MediProvider.RECORD);
    assertEquals("Error: The Record with Medi Cream Name URI was matched incorrectly.", testMatcher.match(TEST_RECORD_WITH_MEDI_CREAM_DIR), MediProvider.RECORD_CREAME_NAME);

  }
}
