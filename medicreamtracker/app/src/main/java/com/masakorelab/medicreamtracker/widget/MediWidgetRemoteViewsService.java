package com.masakorelab.medicreamtracker.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.masakorelab.medicreamtracker.R;
import com.masakorelab.medicreamtracker.Utils;
import com.masakorelab.medicreamtracker.data.Contract;

public class MediWidgetRemoteViewsService extends RemoteViewsService {
  public final String LOG_TAG = MediWidgetRemoteViewsService.class.getSimpleName();

  private static final String[] JOINED_RECORD_COLUMN = {
      Contract.RecordEntry.TABLE_NAME + "." + Contract.RecordEntry._ID,
      Contract.RecordEntry.COLUMN_APPLY_DATE,
      Contract.BodyPartEntry.COLUMN_CATEGORYNAME,
      Contract.MediCreamEntry.COLUMN_NAME
  };

  private static final int INDEX_RECORD_ID = 0;
  private static final int INDEX_APPLY_DATE = 1;
  private static final int INDEX_BODY_CATEGORYNAME = 2;
  private static final int INDEX_APPLY_MEDI_NAME = 3;

  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new RemoteViewsFactory() {
      private Cursor data = null;

      @Override
      public void onCreate() {

      }

      @Override
      public void onDataSetChanged() {
        if(data != null) {
          data.close();
        }

        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        Uri recordUri = Contract.RecordEntry.CONTENT_URI_JOINED_TABLE;
        data = getContentResolver().query(recordUri, JOINED_RECORD_COLUMN,  null, null, Contract.RecordEntry.COLUMN_APPLY_DATE + " DESC");
        Binder.restoreCallingIdentity(identityToken);

      }

      @Override
      public void onDestroy() {
        if (data != null) {
          data.close();
          data = null;
        }
      }

      @Override
      public int getCount() {
        return data == null ? 0 : data.getCount();
      }

      @Override
      public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
          return null;
        }
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_medi_list_item);
        long toLongDate = Long.parseLong(data.getString(INDEX_APPLY_DATE));
        String applyDate = Utils.timeConverter(getApplicationContext(), toLongDate);
        String bodyParts = data.getString(INDEX_BODY_CATEGORYNAME);
        String creamName = data.getString(INDEX_APPLY_MEDI_NAME);

        views.setTextViewText(R.id.widget_date, applyDate);
        views.setTextViewText(R.id.widget_parts, bodyParts);
        views.setTextViewText(R.id.widget_name, creamName);

        final Intent fillInIntent = new Intent();
        Uri recordUri = Contract.RecordEntry.CONTENT_URI_JOINED_TABLE;
        fillInIntent.setData(recordUri);
        views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
        return views;
      }

      @Override
      public RemoteViews getLoadingView() {
        return new RemoteViews(getPackageName(), R.layout.widget_medi_list_item);
      }

      @Override
      public int getViewTypeCount() {
        return 1;
      }

      @Override
      public long getItemId(int position) {
        if (data.moveToPosition(position)) {
          return data.getLong(INDEX_RECORD_ID);
        }
        return position;
      }

      @Override
      public boolean hasStableIds() {
        return true;
      }
    };
  }
}
