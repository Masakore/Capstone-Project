package com.masakorelab.medicreamtracker;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.masakorelab.medicreamtracker.data.Contract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RecordActivity extends AppCompatActivity {
  private static final String STATE_PARTS_SPINNER = "statepartsspinner";
  private static final String STATE_NAME_SPINNER = "statenamespinner";
  private static final String STATE_DATE = "statedatepicker";
  private static final String STATE_DIALOG = "statedialog";
  private boolean IS_DIALOG_OPEN = false;

  SimpleCursorAdapter mBodyPartSpinnerCursorAdapter;
  SimpleCursorAdapter mMediCreamSpinnerCursorAdapter;

  private TextInputLayout inputLayoutDate;
  private EditText inputDate;
  private MaterialSpinner inputName, inputParts;
  private Button btnCreate;

  private AlertDialog mDialog;
  private DatePickerDialog mDatePicker;

  private String mName;
  private String mParts;
  private String mCalendarTime;

  private SimpleDateFormat dateFormat;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    //Alert Dialog for inserting data
    final LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
    final View dialogLayout = inflater.inflate(R.layout.dialog_record_create, (ViewGroup) findViewById(R.id.dialog_layout_root));
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.record_dialog_title_create));
    builder.setView(dialogLayout);
    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        inputParts.setSelection(0);
        inputName.setSelection(0);
        inputDate.getText().clear();
        inputLayoutDate.setErrorEnabled(false);
        inputLayoutDate.setError(null);
        IS_DIALOG_OPEN = false;
        dialog.dismiss();
      }
    });
    mDialog = builder.create();
    dateFormat = new SimpleDateFormat(getString(R.string.date_format));

    //ref: text validation: http://www.androidhive.info/2015/09/android-material-design-floating-labels-for-edittext/
    inputLayoutDate = (TextInputLayout) dialogLayout.findViewById(R.id.input_layout_date);
    inputName = (MaterialSpinner) dialogLayout.findViewById(R.id.record_name);
    inputParts = (MaterialSpinner) dialogLayout.findViewById(R.id.record_parts);
    inputDate = (EditText) dialogLayout.findViewById(R.id.record_date);
    inputDate.setInputType(InputType.TYPE_NULL);

    btnCreate = (Button) dialogLayout.findViewById(R.id.btn_create);
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
        IS_DIALOG_OPEN = true;
        mDialog.show();
      }
    });

    setDateTimeField();
    setSpinner();
  }

  //ref:https://developer.android.com/reference/android/widget/SimpleCursorAdapter.html#SimpleCursorAdapter(android.content.Context, int, android.database.Cursor, java.lang.String[], int[])
  private void setSpinner() {
    String[] columns;
    int[] to;

    Cursor bodyPartCursor = getContentResolver().query(Contract.BodyPartEntry.CONTENT_URI, null, null, null, null);
    columns = new String[] { Contract.BodyPartEntry.COLUMN_CATEGORYNAME };
    to = new int[] { android.R.id.text1 };
    mBodyPartSpinnerCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, bodyPartCursor, columns, to);
    mBodyPartSpinnerCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    inputParts.setAdapter(mBodyPartSpinnerCursorAdapter);

    Cursor mediCreamCursor = getContentResolver().query(Contract.MediCreamEntry.CONTENT_URI, null, null, null, null);
    columns = new String[] { Contract.MediCreamEntry.COLUMN_NAME };
    to = new int[] { android.R.id.text1 };
    mMediCreamSpinnerCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, mediCreamCursor, columns, to);
    mMediCreamSpinnerCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    inputName.setAdapter(mMediCreamSpinnerCursorAdapter);
  }

  private void submitForm() {
    if (!validate()) {
      return;
    }

    Cursor c = (Cursor) inputName.getSelectedItem();
    int ind_name = c.getColumnIndex(Contract.MediCreamEntry.COLUMN_NAME);
    mName = c.getString(ind_name);

    c = (Cursor) inputParts.getSelectedItem();
    int ind_parts = c.getColumnIndex(Contract.BodyPartEntry.COLUMN_CATEGORYNAME);
    mParts = c.getString(ind_parts);

    insertRecords(mParts, mName);
    Toast.makeText(this, getString(R.string.toast_created), Toast.LENGTH_SHORT).show();

    inputParts.setSelection(0);
    inputName.setSelection(0);
    inputDate.getText().clear();
    inputLayoutDate.setErrorEnabled(false);
    inputLayoutDate.setError(null);
    IS_DIALOG_OPEN = false;
    mDialog.dismiss();
  }

  private boolean validate() {
    if (inputDate.getText().toString().trim().isEmpty()) {
      inputLayoutDate.setErrorEnabled(true);
      inputLayoutDate.setError(getString(R.string.validation_empty));
    }

    if (inputParts.getSelectedItem() == null || inputParts.getSelectedItem().equals(getString(R.string.record_placeholder_parts))) {
      inputParts.setError(getString(R.string.validation_empty));
    }

    if (inputName.getSelectedItem() == null || inputName.getSelectedItem().equals(getString(R.string.record_placeholder_name))) {
      inputName.setError(getString(R.string.validation_empty));
    }

    return !(inputLayoutDate.isErrorEnabled() || inputParts.getError() != null || inputName.getError() != null);
  }

  //ref:http://androidopentutorials.com/android-datepickerdialog-on-edittext-click-event/
  private void setDateTimeField() {
    Calendar newCalendar = Calendar.getInstance();
    mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        inputDate.setText(dateFormat.format(newDate.getTime()));
        mCalendarTime = Long.toString(newDate.getTimeInMillis());
      }

    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    inputDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mDatePicker.show();
      }
    });
  }

  private void insertRecords(String parts, String name) {
    AsyncDataParser adp = new AsyncDataParser(this, Consts.CLASS_RECORD, Consts.CRUD_CREATE);
    adp.execute(mCalendarTime, parts, name);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_DIALOG, IS_DIALOG_OPEN);
    outState.putString(STATE_DATE, inputDate.getText().toString());
    outState.putInt(STATE_NAME_SPINNER, inputName.getSelectedItemPosition());
    outState.putInt(STATE_PARTS_SPINNER, inputParts.getSelectedItemPosition());
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_DIALOG) == true) {
      inputDate.setText(savedInstanceState.getString(STATE_DATE));
      inputParts.setSelection(savedInstanceState.getInt(STATE_PARTS_SPINNER));
      inputName.setSelection(savedInstanceState.getInt(STATE_NAME_SPINNER));
      IS_DIALOG_OPEN = true;
      mDialog.show();
    }
  }
}
