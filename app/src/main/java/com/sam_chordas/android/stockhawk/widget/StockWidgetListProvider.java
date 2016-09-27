package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by rohanarora on 27/09/16.
 */
public class StockWidgetListProvider implements RemoteViewsService.RemoteViewsFactory {


    Context mContext;
    Cursor mCursor;
    public StockWidgetListProvider(Context context, Intent intent){
        mContext = context;

    }

    public void updateCursor(){
        if(mCursor!=null){
            mCursor.close();
            mCursor = null;
        }
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onCreate() {
        updateCursor();
    }

    @Override
    public void onDataSetChanged() {
        mCursor.close();
        updateCursor();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        mCursor.moveToPosition(i);
        remoteViews.setTextViewText(R.id.stock_symbol,mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
        remoteViews.setTextViewText(R.id.bid_price,mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
        remoteViews.setTextViewText(R.id.change,mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE)));
        if (mCursor.getInt(mCursor.getColumnIndex(QuoteColumns.ISUP)) == 1) {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
