package com.masakorelab.medicreamtracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

  public static String timeConverter(long milliSeconds) {
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(milliSeconds);

    return format.format(calendar.getTime());
  }
}
