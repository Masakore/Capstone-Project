package com.masakorelab.medicreamtracker;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.masakorelab.medicreamtracker.data.Contract;

public class AsyncDataParser extends AsyncTask<String, Void, Void>{
  private final String LOG_TAG = AsyncDataParser.class.getSimpleName();
  public static final String ACTION_DATA_UPDATED = "com.masakorelab.medicreamtracker.ACTION_DATA_UPDATED";
  private final Context mContext;
  private String mClass;
  private String mCRUD = null;

  public AsyncDataParser(Context context, String className, String crudType) {
    mContext = context;

    if (className.equals(Consts.CLASS_REGISTER)) {
      mClass = Consts.CLASS_REGISTER;
    } else if (className.equals(Consts.CLASS_RECORD)) {
      mClass = Consts.CLASS_RECORD;
    }

    if (crudType.equals(Consts.CRUD_CREATE)) {
      mCRUD = Consts.CRUD_CREATE;
    } else if (crudType.equals(Consts.CRUD_UPDATE)) {
      mCRUD = Consts.CRUD_UPDATE;
    } else if (crudType.equals(Consts.CRUD_DELETE)) {
      mCRUD = Consts.CRUD_DELETE;
    }

  }

  @Override
  protected Void doInBackground(String...params) {

    if( params.length == 0 ) {
      return null;
    }

    String name;
    String description;
    String id;
    String bodyPart;
    String date;

    //Insert for Register Activity
    if (mClass.equals(Consts.CLASS_REGISTER) && mCRUD.equals(Consts.CRUD_CREATE)) {
      name = params[0];
      description = params[1];

      if ( name == null || name.isEmpty() ) {
        Log.d(LOG_TAG, "name is null or empty");
        return null;
      }
      ContentValues mediCreamValue = new ContentValues();
      mediCreamValue.put(Contract.MediCreamEntry.COLUMN_NAME, name);
      mediCreamValue.put(Contract.MediCreamEntry.COLUMN_DESCRIPTION, description);

      Uri mediCreamUri = mContext.getContentResolver().insert(Contract.MediCreamEntry.CONTENT_URI, mediCreamValue);
      long mediCreamId = ContentUris.parseId(mediCreamUri);

      if ( mediCreamId == -1 ) {
        Log.d(LOG_TAG, "failed to insert");
        return null;
      }
      return null;
    }

    //Update for Register Activity
    if (mClass.equals(Consts.CLASS_REGISTER) && mCRUD.equals(Consts.CRUD_UPDATE)) {
      name = params[0];
      description = params[1];
      id = params[2];

      if ( name == null || name.isEmpty() ) {
        Log.d(LOG_TAG, "name is null or empty");
        return null;
      }

      if ( id == null || id.isEmpty() ) {
        Log.d(LOG_TAG, "ID is null or empty");
        return null;
      }

      ContentValues mediCreamValue = new ContentValues();
      mediCreamValue.put(Contract.MediCreamEntry.COLUMN_NAME, name);
      mediCreamValue.put(Contract.MediCreamEntry.COLUMN_DESCRIPTION, description);

      int count = mContext.getContentResolver().update(Contract.MediCreamEntry.CONTENT_URI, mediCreamValue,Contract.MediCreamEntry._ID + "= ?", new String[]{id});

      if ( count == 0 ) {
        Log.d(LOG_TAG, "failed to update");
        return null;
      }
      return null;
    }

    //Delete for Register Activity
    if (mClass.equals(Consts.CLASS_REGISTER) && mCRUD.equals(Consts.CRUD_DELETE)) {
      id = params[0];

      if ( id == null || id.isEmpty() ) {
        Log.d(LOG_TAG, "ID is null or empty");
        return null;
      }

      int count = mContext.getContentResolver().delete(Contract.MediCreamEntry.CONTENT_URI, Contract.MediCreamEntry._ID + "= ?", new String[]{id});

      if ( count == 0 ) {
        Log.d(LOG_TAG, "failed to update");
        return null;
      }
      return null;
    }

    //Insert for Register Activity
    if (mClass.equals(Consts.CLASS_RECORD) && mCRUD.equals(Consts.CRUD_CREATE)) {
      date = params[0];
      bodyPart = params[1];
      name = params[2];

      if ( date == null || date.isEmpty() ) {
        Log.d(LOG_TAG, "date is null or empty");
        return null;
      }

      if ( bodyPart == null || bodyPart.isEmpty() ) {
        Log.d(LOG_TAG, "bodyPart is null or empty");
        return null;
      }

      if ( name == null || name.isEmpty() ) {
        Log.d(LOG_TAG, "name is null or empty");
        return null;
      }


      ContentValues recordValue = new ContentValues();

      Cursor c = mContext.getContentResolver().query(Contract.BodyPartEntry.CONTENT_URI, null, Contract.BodyPartEntry.COLUMN_CATEGORYNAME + "= ?", new String[]{ bodyPart }, null, null);
      if (!c.moveToFirst()) {
        Log.d(LOG_TAG, "failed to get bodyPartId");
        c.close();
        return null;
      }
      int bodyPartId = c.getInt(c.getColumnIndex(Contract.MediCreamEntry._ID));

      c = mContext.getContentResolver().query(Contract.MediCreamEntry.CONTENT_URI, null, Contract.MediCreamEntry.COLUMN_NAME + "= ?", new String[]{ name }, null, null);
      if (!c.moveToFirst()) {
        Log.d(LOG_TAG, "failed to get mediCreamId");
        c.close();
        return null;
      }
      int mediCreamId = c.getInt(c.getColumnIndex(Contract.MediCreamEntry._ID));

      recordValue.put(Contract.RecordEntry.COLUMN_APPLY_DATE, Long.parseLong(date));
      recordValue.put(Contract.RecordEntry.COLUMN_PART_OF_BODY, bodyPartId);
      recordValue.put(Contract.RecordEntry.COLUMN_CREAM_NAME, mediCreamId);

      Uri recordUri = mContext.getContentResolver().insert(Contract.RecordEntry.CONTENT_URI, recordValue);
      long recordId = ContentUris.parseId(recordUri);
      c.close();

      if ( recordId == -1 ) {
        Log.d(LOG_TAG, "failed to insert");
        return null;
      }

      updateWidgets();

      return null;
    }

    return null;
  }

  private void updateWidgets() {
    Context context = mContext;
    // Setting the package ensures that only components in our app will receive the broadcast
    Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
        .setPackage(context.getPackageName());
    context.sendBroadcast(dataUpdatedIntent);
  }
}
