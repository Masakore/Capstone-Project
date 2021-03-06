package com.masakorelab.medicreamtracker;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.masakorelab.medicreamtracker.data.Contract;

import java.util.Calendar;

public class MainActivityFragment extends Fragment {
  static final String STATE_SPINNER = "selecteditem";

  SimpleCursorAdapter mSpinnerAdapter;

  private Spinner mNameSpinner;

  ImageView img_face;
  ImageView img_body;
  ImageView img_left_arm;
  ImageView img_right_arm;
  ImageView img_left_leg;
  ImageView img_right_leg;

  private final int THREEDAYS = 3;
  private final int ONEWEEK = 7;
  private final int THREEWEEKS = 21;

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

  public MainActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_main, container, false);

    img_face = (ImageView) root.findViewById(R.id.img_head);
    img_body = (ImageView) root.findViewById(R.id.img_body);
    img_left_arm = (ImageView) root.findViewById(R.id.img_left_arm);
    img_right_arm = (ImageView) root.findViewById(R.id.img_right_arm);
    img_left_leg = (ImageView) root.findViewById(R.id.img_left_leg);
    img_right_leg = (ImageView) root.findViewById(R.id.img_right_leg);
    mNameSpinner = (Spinner) root.findViewById(R.id.item_spinner);
    setSpinner();

    return root;
  }

  private void setSpinner() {
    String[] columns;
    int[] to;

    Cursor mediCreamCursor = getActivity().getContentResolver().query(Contract.MediCreamEntry.CONTENT_URI, null, null, null, null);
    columns = new String[] { Contract.MediCreamEntry.COLUMN_NAME };
    to = new int[] { android.R.id.text1 };
    mSpinnerAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mediCreamCursor, columns, to);
    mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mNameSpinner.setAdapter(mSpinnerAdapter);
    mNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setColor();
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  private void setColor() {
    Cursor c = (Cursor) mNameSpinner.getSelectedItem();
    int ind_name = c.getColumnIndex(Contract.MediCreamEntry.COLUMN_NAME);
    String creamName = c.getString(ind_name);

    String bodyParts[] = {"Face", "Body", "LeftArm", "RightArm", "LeftLeg", "RightLeg"};

    for (String bodyPart : bodyParts) {
      setColorHelper(creamName, bodyPart);
    }
  }

  private void setColorHelper(String creamName, String bodyPart) {
    Uri uri = Contract.RecordEntry.CONTENT_URI_JOINED_TABLE;
    String sortOrder = Contract.RecordEntry.COLUMN_APPLY_DATE + " DESC";
    String selection = Contract.MediCreamEntry.COLUMN_NAME + " =?" + " AND " + Contract.BodyPartEntry.COLUMN_CATEGORYNAME + " =?";
    String[] selectionArgs = {creamName, bodyPart};
    Cursor cursor =  getActivity().getContentResolver().query(uri, JOINE_RECORD_COLUMNS, selection, selectionArgs, sortOrder);

    ImageView img_view_bodypart = null;

    //Todo refactor here later
    if (bodyPart.equals(getString(R.string.main_face))) {
      img_view_bodypart = img_face;
    }
    if (bodyPart.equals(getString(R.string.main_body))) {
      img_view_bodypart = img_body;
    }
    if (bodyPart.equals(getString(R.string.main_leftarm))) {
      img_view_bodypart = img_left_arm;
    }
    if (bodyPart.equals(getString(R.string.main_rightarm))) {
      img_view_bodypart = img_right_arm;
    }
    if (bodyPart.equals(getString(R.string.main_leftleg))) {
      img_view_bodypart = img_left_leg;
    }
    if (bodyPart.equals(getString(R.string.main_leftleg))) {
      img_view_bodypart = img_right_leg;
    }

    if (img_view_bodypart == null) {
      return;
    }

    if (cursor.moveToFirst()) {
      long currentTime = Calendar.getInstance().getTimeInMillis();
      long appliedDate = Long.parseLong(cursor.getString(COL_RECOR_APPLY_DATE));
      long diff = currentTime - appliedDate;

      if (diff > 0) {
        int howManyDaysPassed = (int) (diff / (1000*60*60*24));

        if (howManyDaysPassed >= THREEDAYS && howManyDaysPassed < ONEWEEK) {
          GradientDrawable drawable = (GradientDrawable) img_view_bodypart.getBackground().getCurrent();
          drawable.setColor(getActivity().getResources().getColor(R.color.threedays));
        } else if (howManyDaysPassed >= ONEWEEK && howManyDaysPassed < THREEWEEKS) {
          GradientDrawable drawable = (GradientDrawable) img_view_bodypart.getBackground().getCurrent();
          drawable.setColor(getActivity().getResources().getColor(R.color.oneweek));
        } else if (howManyDaysPassed >= THREEWEEKS) {
          GradientDrawable drawable = (GradientDrawable) img_view_bodypart.getBackground().getCurrent();
          drawable.setColor(getActivity().getResources().getColor(R.color.threeweeks));
        }
      }
    } else {
      GradientDrawable drawable = (GradientDrawable) img_view_bodypart.getBackground().getCurrent();
      drawable.setColor(Color.WHITE);
    }
    cursor.close();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(STATE_SPINNER, mNameSpinner.getSelectedItemPosition());
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onViewStateRestored(Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);

    if (savedInstanceState != null) {
      mNameSpinner.setSelection(savedInstanceState.getInt(STATE_SPINNER));
    }
  }
}
