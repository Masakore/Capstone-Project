package com.masakorelab.medicreamtracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.masakorelab.medicreamtracker.data.Contract;

public class RecordSpinnerAdapter extends CursorAdapter{

  final Context mContext;
  public RecordSpinnerAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    mContext = context;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    return null;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    int idx_name = cursor.getColumnIndex(Contract.BodyPartEntry.COLUMN_CATEGORYNAME);

    TextView name = (TextView)view.findViewById(R.id.parts_record);

    name.setText(cursor.getString(idx_name));
  }
}
