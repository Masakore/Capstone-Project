package com.masakorelab.medicreamtracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainAdapter extends CursorAdapter {

  final Context mContext;
  public MainAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    mContext = context;
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = LayoutInflater.from(context).inflate(R.layout.fragment_main, parent, false);
    return null;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    ImageView img_face = (ImageView) view.findViewById(R.id.img_head);
    ImageView img_body = (ImageView) view.findViewById(R.id.img_body);
    ImageView img_left_arm = (ImageView) view.findViewById(R.id.img_left_arm);
    ImageView img_right_arm = (ImageView) view.findViewById(R.id.img_right_arm);
    ImageView img_left_leg = (ImageView) view.findViewById(R.id.img_left_leg);
    ImageView img_right_leg = (ImageView) view.findViewById(R.id.img_right_leg);

    String dateInMillis = cursor.getString(RecordActivityFragment.COL_RECOR_APPLY_DATE);

    img_face.setBackgroundColor(mContext.getResources().getColor(R.color.grey_700));
    img_body.setBackgroundColor(mContext.getResources().getColor(R.color.grey_700));
    img_left_arm.setBackgroundColor(mContext.getResources().getColor(R.color.grey_700));
    img_right_arm.setBackgroundColor(mContext.getResources().getColor(R.color.grey_700));
    img_left_leg.setBackgroundColor(mContext.getResources().getColor(R.color.grey_700));
    img_right_leg.setBackgroundColor(mContext.getResources().getColor(R.color.grey_700));

  }
}
