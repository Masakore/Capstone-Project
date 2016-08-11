package com.masakorelab.medicreamtracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.masakorelab.medicreamtracker.data.Contract;

public class RecordAdapter extends CursorAdapter {

  final Context mContext;
  public RecordAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    mContext = context;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = LayoutInflater.from(context).inflate(R.layout.listview_record_row, parent, false);
    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    TextView date = (TextView)view.findViewById(R.id.date_record);
    TextView parts = (TextView)view.findViewById(R.id.parts_record);
    TextView name = (TextView)view.findViewById(R.id.name_record);

    String tmp = cursor.getString(RecordActivityFragment.COL_RECOR_APPLY_DATE);
    tmp = Utils.timeConverter(Long.parseLong(tmp));

    date.setText(tmp);
    parts.setText(cursor.getString(RecordActivityFragment.COL_BODY_CATEGORYNAME));
    name.setText(cursor.getString(RecordActivityFragment.COL_MEDI_NAME));
  }
}
