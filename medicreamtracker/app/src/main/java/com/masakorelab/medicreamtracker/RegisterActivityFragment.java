package com.masakorelab.medicreamtracker;

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
import android.widget.ListView;

import com.masakorelab.medicreamtracker.data.Contract;

public class RegisterActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final int FORECAST_LOADER = 0;
  private RegisterAdapter mRegisterAdapter;

  public RegisterActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_register, container, false);

    mRegisterAdapter = new RegisterAdapter(getActivity(), null, 0);
    ListView listView = (ListView) root.findViewById(R.id.listView_register);
    listView.setAdapter(mRegisterAdapter);

    return root;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Uri mediCreamUri = Contract.MediCreamEntry.CONTENT_URI;
    return new CursorLoader(getActivity(), mediCreamUri, null, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mRegisterAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mRegisterAdapter.swapCursor(null);
  }

}
