package com.masakorelab.medicreamtracker.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.masakorelab.medicreamtracker.AsyncDataParser;
import com.masakorelab.medicreamtracker.R;
import com.masakorelab.medicreamtracker.RecordActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MediWidgetProvider extends AppWidgetProvider{

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_medi_tracker);

      Intent intent = new Intent(context, RecordActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
      remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        setRemoteAdapter(context, remoteViews);
      } else {
        setRemoteAdapterV11(context, remoteViews);
      }

      Intent clickIntentTemplate = new Intent(context, RecordActivity.class);
      PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
          .addNextIntentWithParentStack(clickIntentTemplate)
          .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setPendingIntentTemplate(R.id.widget_listview, clickPendingIntentTemplate);
      remoteViews.setEmptyView(R.id.widget_listview, R.id.widget_empty);

      appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
  }

  @Override
  public void onReceive(@NonNull Context context, @NonNull Intent intent) {
    super.onReceive(context, intent);
    if (AsyncDataParser.ACTION_DATA_UPDATED.equals(intent.getAction())) {
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      int[] appWigetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
      appWidgetManager.notifyAppWidgetViewDataChanged(appWigetIds, R.id.widget_listview);
    }
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
    views.setRemoteAdapter(R.id.widget_listview, new Intent(context, MediWidgetRemoteViewsService.class));
  }

  @SuppressWarnings("deprecation")
  private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
    views.setRemoteAdapter(0, R.id.widget_listview, new Intent(context, MediWidgetRemoteViewsService.class));
  }
}
