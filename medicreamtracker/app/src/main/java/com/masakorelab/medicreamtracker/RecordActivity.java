package com.masakorelab.medicreamtracker;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.masakorelab.medicreamtracker.data.Contract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final int SPINNER_LOADER = 2;
  private RecordSpinnerAdapter mRecordSpinnerAdapter;

  private TextInputLayout inputLayoutName, inputLayoutParts, inputLayoutDate;
  private EditText inputName, inputDate;
  private Spinner inputParts;
  private Button btnCreate;

  private AlertDialog mDialog;
  private DatePickerDialog mDatePicker;

  private SimpleDateFormat dateFormat;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mRecordSpinnerAdapter = new RecordSpinnerAdapter(this, null, 0);
    getSupportLoaderManager().initLoader(SPINNER_LOADER, null, this);

    //Alert Dialog for inserting data
    final LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
    final View dialogLayout = inflater.inflate(R.layout.dialog_record_create, (ViewGroup) findViewById(R.id.dialog_layout_root));
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Create Your Records");
    builder.setView(dialogLayout);
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        inputDate.getText().clear();
        inputName.getText().clear();
        inputLayoutDate.setErrorEnabled(false);
        inputLayoutDate.setError(null);
        inputLayoutParts.setErrorEnabled(false);
        inputLayoutParts.setError(null);
        inputLayoutName.setErrorEnabled(false);
        inputLayoutName.setError(null);
        dialog.dismiss();
      }
    });
    mDialog = builder.create();
    dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    //ref: text validation: http://www.androidhive.info/2015/09/android-material-design-floating-labels-for-edittext/
    inputLayoutName = (TextInputLayout) dialogLayout.findViewById(R.id.input_layout_name);
    inputLayoutParts = (TextInputLayout) dialogLayout.findViewById(R.id.input_layout_parts);
    inputLayoutDate = (TextInputLayout) dialogLayout.findViewById(R.id.input_layout_date);
    inputName = (EditText) dialogLayout.findViewById(R.id.record_name);
    inputParts = (Spinner) dialogLayout.findViewById(R.id.record_parts);
    inputDate = (EditText) dialogLayout.findViewById(R.id.record_date);
    btnCreate = (Button) dialogLayout.findViewById(R.id.btn_create);
    inputName.addTextChangedListener(new MyTextWatcher(inputName));
    inputParts.setAdapter(mRecordSpinnerAdapter);

    btnCreate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        submitForm();
      }
    });

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mDialog.show();
      }
    });

    setDateTimeField();
  }

  private void submitForm() {
    if (!validate()) {
      return;
    }

    insertRecords(inputDate.getText().toString().trim(), inputParts.toString().trim(), inputName.getText().toString().trim());
    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();

    inputDate.getText().clear();
    inputName.getText().clear();
    inputLayoutDate.setErrorEnabled(false);
    inputLayoutDate.setError(null);
    inputLayoutParts.setErrorEnabled(false);
    inputLayoutParts.setError(null);
    inputLayoutName.setErrorEnabled(false);
    inputLayoutName.setError(null);
    mDialog.dismiss();
  }

  private boolean validate() {
    if (inputDate.getText().toString().trim().isEmpty()) {
      inputLayoutParts.setErrorEnabled(true);
      inputLayoutParts.setError(getString(R.string.register_validation_empty));
      return false;
    }
    if (inputParts.getSelectedItem().toString().trim().isEmpty()) {
      inputLayoutParts.setErrorEnabled(true);
      inputLayoutParts.setError(getString(R.string.register_validation_empty));
      return false;
    }
    if (inputName.getText().toString().trim().isEmpty()) {
      inputLayoutName.setErrorEnabled(true);
      inputLayoutName.setError(getString(R.string.register_validation_empty));
      return false;
    }
    return true;
  }


  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Uri bodyPartUri = Contract.BodyPartEntry.CONTENT_URI;
    return new CursorLoader(this, bodyPartUri, null, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mRecordSpinnerAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mRecordSpinnerAdapter.swapCursor(null);
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
        case R.id.record_name:
          validate();
          break;
        case R.id.record_parts:
          validate();
          break;
        case R.id.record_date:
          validate();
          break;
      }

    }
  }

  //ref:http://androidopentutorials.com/android-datepickerdialog-on-edittext-click-event/
  private void setDateTimeField() {
    Calendar newCalendar = Calendar.getInstance();
    mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        inputDate.setText(dateFormat.format(newDate.getTime()));
      }

    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    inputDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mDatePicker.show();
      }
    });
  }

  private void insertRecords(String date, String parts, String name) {
    AsyncDataParser adp = new AsyncDataParser(this, Consts.CLASS_REGISTER, Consts.CRUD_CREATE);
    adp.execute(date, parts, name);
  }
}
