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
  private Dialog mDialog;

  private String UPDATE_ID;
  public RecordActivityFragment() {
  }

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
    Uri recordUri = Contract.RecordEntry.CONTENT_URI_JOINED_TABLE;
    return new CursorLoader(getActivity(), recordUri, null, null, null, null);
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
