package com.masakorelab.medicreamtracker;

import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.masakorelab.medicreamtracker.data.Contract;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecordActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final int RECORD_LOADER = 1;
  private RecordAdapter mRecordAdapter;

  public RecordActivityFragment() {
  }

  public static final String[] JOINE_RECORD_COLUMNS = {
      Contract.RecordEntry.TABLE_NAME + "." + Contract.RecordEntry._ID,
      Contract.RecordEntry.COLUMN_APPLY_DATE,
      Contract.RecordEntry.COLUMN_CREAM_NAME,
      Contract.RecordEntry.COLUMN_PART_OF_BODY,
      Contract.BodyPartEntry.COLUMN_CATEGORYNAME,
      Contract.MediCreamEntry.COLUMN_NAME
  };

  static final int COL_RECOR_ID = 0;
  static final int COL_RECOR_APPLY_DATE = 1;
  static final int COL_RECOR_CREAM_NAME = 2;
  static final int COL_RECOR_PART_OF_BODY = 3;
  static final int COL_BODY_CATEGORYNAME = 4;
  static final int COL_MEDI_NAME = 5;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root =  inflater.inflate(R.layout.fragment_record, container, false);

    mRecordAdapter = new RecordAdapter(getActivity(), null, 0);

    final ListView listView = (ListView) root.findViewById(R.id.listView_record);
    listView.setAdapter(mRecordAdapter);

    return root;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(RECORD_LOADER, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String sortOrder = Contract.RecordEntry.COLUMN_APPLY_DATE + " ASC";

    Uri recordUri = Contract.RecordEntry.CONTENT_URI_JOINED_TABLE;
    return new CursorLoader(getActivity(), recordUri, JOINE_RECORD_COLUMNS, null, null, sortOrder);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mRecordAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mRecordAdapter.swapCursor(null);
  }
}
