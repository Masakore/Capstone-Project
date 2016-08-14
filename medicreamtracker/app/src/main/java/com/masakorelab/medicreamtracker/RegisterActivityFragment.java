package com.masakorelab.medicreamtracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.masakorelab.medicreamtracker.data.Contract;

public class RegisterActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final String STATE_NAME = "statename";
  private static final String STATE_DESCRIPTION = "statedescription";
  private static final String STATE_DIALOG = "statedialog";
  private static boolean IS_DIALOG_OPEN = false;

  //Google services
  private AdView mAdView;

  private static final int REGISTER_LOADER = 0;
  private RegisterAdapter mRegisterAdapter;
  private Dialog mDialog;

  private String UPDATE_ID;
  private TextInputLayout inputLayoutName, inputLayoutDescription;
  private EditText inputName, inputDescription;
  private Button btnUpdate, btnDelete;

  public RegisterActivityFragment() {
  }

  //TODO onSaveInstanceState

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_register, container, false);

    mRegisterAdapter = new RegisterAdapter(getActivity(), null, 0);

    final View dialogLayout = inflater.inflate(R.layout.dialog_register_update_delete, (ViewGroup) getActivity().findViewById(R.id.dialog_layout_root_update_delete));
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(getString(R.string.register_dialog_edit_title));
    builder.setView(dialogLayout);
    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        UPDATE_ID = null;
        inputName.getText().clear();
        inputDescription.getText().clear();
        inputLayoutName.setErrorEnabled(false);
        inputLayoutName.setError(null);
        dialog.dismiss();
      }
    });
    mDialog = builder.create();

    //ref: text validation: http://www.androidhive.info/2015/09/android-material-design-floating-labels-for-edittext/
    inputLayoutName = (TextInputLayout) dialogLayout.findViewById(R.id.update_delete_layout_name);
    inputLayoutDescription = (TextInputLayout) dialogLayout.findViewById(R.id.update_delete_layout_description);
    inputName = (EditText) dialogLayout.findViewById(R.id.register_cream_name_update_delete);
    inputDescription = (EditText) dialogLayout.findViewById(R.id.register_desctiption_update_delete);
    btnUpdate = (Button) dialogLayout.findViewById(R.id.btn_register_update);
    btnDelete = (Button) dialogLayout.findViewById(R.id.btn_register_delete);
    inputName.addTextChangedListener(new MyTextWatcher(inputName));

    btnUpdate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        submitForm();
      }
    });

    btnDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        deleteRegisteredData(UPDATE_ID);

        UPDATE_ID = null;
        inputName.getText().clear();
        inputDescription.getText().clear();
        inputLayoutName.setErrorEnabled(false);
        inputLayoutName.setError(null);
        IS_DIALOG_OPEN = false;
        mDialog.dismiss();
      }
    });

    final ListView listView = (ListView) root.findViewById(R.id.listView_register);
    listView.setAdapter(mRegisterAdapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView selectedList = (ListView) parent;
        Cursor cursor = (Cursor) selectedList.getItemAtPosition(position);

        int COL_ID = cursor.getColumnIndex(Contract.MediCreamEntry._ID);
        int COL_NAME = cursor.getColumnIndex(Contract.MediCreamEntry.COLUMN_NAME);
        int COL_DESCTIPTION = cursor.getColumnIndex(Contract.MediCreamEntry.COLUMN_DESCRIPTION);

        UPDATE_ID = cursor.getString(COL_ID);
        inputName.setText(cursor.getString(COL_NAME));
        inputDescription.setText(cursor.getString(COL_DESCTIPTION));
        IS_DIALOG_OPEN = true;
        mDialog.show();
      }
    });

    //Ads
    mAdView = (AdView) root.findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
    mAdView.loadAd(adRequest);

    return root;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(REGISTER_LOADER, null, this);
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

  private void submitForm() {
    if (!validateName()) {
      return;
    }
    updateRegisteredData(inputName.getText().toString().trim(), inputDescription.getText().toString().trim(), UPDATE_ID.trim());
    Toast.makeText(getActivity(), getString(R.string.toast_updated), Toast.LENGTH_SHORT).show();

    UPDATE_ID = null;
    inputName.getText().clear();
    inputDescription.getText().clear();
    inputLayoutName.setErrorEnabled(false);
    inputLayoutName.setError(null);
    IS_DIALOG_OPEN = false;
    mDialog.dismiss();
  }

  private boolean validateName() {
    if (inputName.getText().toString().trim().isEmpty()) {
      inputLayoutName.setErrorEnabled(true);
      inputLayoutName.setError(getString(R.string.register_validation_empty));
      return false;
    }
    return true;
  }

  private class MyTextWatcher implements TextWatcher {
    private View view;

    private MyTextWatcher(View view) {
      this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
      switch (view.getId()) {
        case R.id.register_cream_name:
          validateName();
          break;
      }

    }
  }

  private void updateRegisteredData(String name, String description, String updateId) {
    AsyncDataParser adp = new AsyncDataParser(getActivity(), Consts.CLASS_REGISTER, Consts.CRUD_UPDATE);
    adp.execute(name, description, updateId);
  }

  private void deleteRegisteredData(String updateId) {
    AsyncDataParser adp = new AsyncDataParser(getActivity(), Consts.CLASS_REGISTER, Consts.CRUD_DELETE);
    adp.execute(updateId);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(STATE_DIALOG, IS_DIALOG_OPEN);
    outState.putString(STATE_NAME, inputName.getText().toString());
    outState.putString(STATE_DESCRIPTION, inputDescription.getText().toString());
  }

  @Override
  public void onViewStateRestored(Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_DIALOG) == true) {
      inputName.setText(savedInstanceState.getString(STATE_NAME));
      inputDescription.setText(savedInstanceState.getString(STATE_DESCRIPTION));
      IS_DIALOG_OPEN = true;
      mDialog.show();
    }
  }
}
