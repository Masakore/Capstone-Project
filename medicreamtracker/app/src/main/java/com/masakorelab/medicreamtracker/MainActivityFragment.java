package com.masakorelab.medicreamtracker;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.masakorelab.medicreamtracker.data.Contract;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final int MAIN_LOADER = 3;
  SimpleCursorAdapter mMainAdapter;

  private MaterialSpinner mNameSpinner;

  public MainActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_main, container, false);

    mNameSpinner = (MaterialSpinner) root.findViewById(R.id.item_spinner);
    setSpinner();

    return root;
  }

  private void setSpinner() {
    String[] columns;
    int[] to;

    Cursor mediCreamCursor = getActivity().getContentResolver().query(Contract.MediCreamEntry.CONTENT_URI, null, null, null, null);
    columns = new String[] { Contract.MediCreamEntry.COLUMN_NAME };
    to = new int[] { android.R.id.text1 };
    mMainAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mediCreamCursor, columns, to);
    mMainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mNameSpinner.setAdapter(mMainAdapter);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(MAIN_LOADER, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

  }
}
