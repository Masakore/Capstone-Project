package com.masakorelab.medicreamtracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
  TextInputLayout inputLayoutName, inputLayoutDescription;
  EditText inputName, inputDescription;
  Button btnRegister;
  AlertDialog mDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    //Alert Dialog for inserting data
    final LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
    final View dialogLayout = inflater.inflate(R.layout.dialog_register_create, (ViewGroup) findViewById(R.id.dialog_layout_root));
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.register_dialog_add_title));
    builder.setView(dialogLayout);
    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        inputName.getText().clear();
        inputDescription.getText().clear();
        inputLayoutName.setErrorEnabled(false);
        inputLayoutName.setError(null);
        dialog.dismiss();
      }
    });
    mDialog = builder.create();

    //ref: text validation: http://www.androidhive.info/2015/09/android-material-design-floating-labels-for-edittext/
    inputLayoutName = (TextInputLayout) dialogLayout.findViewById(R.id.input_layout_name);
    inputLayoutDescription = (TextInputLayout) dialogLayout.findViewById(R.id.input_layout_description);
    inputName = (EditText) dialogLayout.findViewById(R.id.register_cream_name);
    inputDescription = (EditText) dialogLayout.findViewById(R.id.register_desctiption);
    btnRegister = (Button) dialogLayout.findViewById(R.id.btn_register);
    inputName.addTextChangedListener(new MyTextWatcher(inputName));

    btnRegister.setOnClickListener(new View.OnClickListener() {
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
  }

  private void submitForm() {
    if (!validateName()) {
      return;
    }

    insertRegisteredData(inputName.getText().toString().trim(), inputDescription.getText().toString().trim());
    Toast.makeText(this, getString(R.string.toast_added), Toast.LENGTH_SHORT).show();

    inputName.getText().clear();
    inputDescription.getText().clear();
    inputLayoutName.setErrorEnabled(false);
    inputLayoutName.setError(null);
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

  private void insertRegisteredData(String name, String description) {
    AsyncDataParser adp = new AsyncDataParser(this, Consts.CLASS_REGISTER, Consts.CRUD_CREATE);
    adp.execute(name, description);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

}
