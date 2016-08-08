package com.masakorelab.medicreamtracker;

import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.masakorelab.medicreamtracker.data.Contract;

public class RegisterActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final int FORECAST_LOADER = 0;
  private RegisterAdapter mRegisterAdapter;

  private EditText inputName, inputDescription;
  private TextInputLayout inputLayoutName, inputLayoutDescription;
  private Button btnRegister;

  public RegisterActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    mRegisterAdapter = new RegisterAdapter(getActivity(), null, 0);

    View root = inflater.inflate(R.layout.fragment_register, container, false);

    ListView listView = (ListView) root.findViewById(R.id.listView_register);
    listView.setAdapter(mRegisterAdapter);

    //ref: text validation: http://www.androidhive.info/2015/09/android-material-design-floating-labels-for-edittext/
    inputLayoutName = (TextInputLayout) root.findViewById(R.id.input_layout_name);
    inputLayoutDescription = (TextInputLayout) root.findViewById(R.id.input_layout_description);
    inputName = (EditText) root.findViewById(R.id.register_cream_name);
    inputDescription = (EditText) root.findViewById(R.id.register_desctiption);
    btnRegister = (Button) root.findViewById(R.id.btn_register);
    inputName.addTextChangedListener(new MyTextWatcher(inputName));

    btnRegister.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        submitForm();
      }
    });

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

  private void submitForm() {
    if(!validateName()) {
      return;
    }

    Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
  }

  private boolean validateName() {
    if (inputName.getText().toString().trim().isEmpty()) {
      inputLayoutName.setError(getString(R.string.register_validation_empty));
      requestFocus(inputName);
      return false;
    }
    return true;
  }

  private void requestFocus(View view) {
    if (view.requestFocus()) {
      getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
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
}
