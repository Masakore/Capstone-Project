package com.masakorelab.medicreamtracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.masakorelab.medicreamtracker.data.Contract;

public class RegisterAdapter extends CursorAdapter {

  final Context mContext;
  public RegisterAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    mContext = context;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = LayoutInflater.from(context).inflate(R.layout.listview_register_row, parent, false);
    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    int idx_id = cursor.getColumnIndex(Contract.MediCreamEntry._ID);
    int idx_name = cursor.getColumnIndex(Contract.MediCreamEntry.COLUMN_NAME);
    int idx_des = cursor.getColumnIndex(Contract.MediCreamEntry.COLUMN_DESCRIPTION);

    TextView id = (TextView)view.findViewById(R.id.id_register);
    TextView name = (TextView)view.findViewById(R.id.name_register);
    TextView description = (TextView)view.findViewById(R.id.des_register);

    id.setText(cursor.getString(idx_id));
    name.setText(cursor.getString(idx_name));
    description.setText(cursor.getString(idx_des));
  }
}
