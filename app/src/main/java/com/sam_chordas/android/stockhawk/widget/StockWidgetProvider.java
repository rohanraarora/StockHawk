package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by rohanarora on 27/09/16.
 */
public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            int layoutId = R.layout.stock_widget;
            RemoteViews widget=new RemoteViews(context.getPackageName(), layoutId);

            Intent serviceIntent = new Intent(context,StockWidgetService.class);
            widget.setRemoteAdapter(R.id.widget_list_view,serviceIntent);
            widget.setEmptyView(R.id.widget_list_view, R.id.empty_view);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
            appWidgetManager.updateAppWidget(appWidgetId, widget);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
