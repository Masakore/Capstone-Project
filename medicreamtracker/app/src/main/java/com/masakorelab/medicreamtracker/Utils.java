package com.masakorelab.medicreamtracker;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

  public static String timeConverter(Context context, long milliSeconds) {
    SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_format));

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(milliSeconds);

    return format.format(calendar.getTime());
  }
}
