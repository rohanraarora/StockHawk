package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StockDetailActivity extends AppCompatActivity {


    Cursor mCursor;
    LineChartView lineChartView;
    LineSet dataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        Intent intent = getIntent();
        String symbol = intent.getStringExtra("symbol");


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(symbol);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lineChartView = (LineChartView)this.findViewById(R.id.linechart);
        setUpGraph();

        float min = 999999f;
        float max = 0f;
        float totalValue = 0;
        float avg = 0;
         mCursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[] { QuoteColumns.CREATED,QuoteColumns.BIDPRICE }, QuoteColumns.SYMBOL + "= ?",
                new String[] { symbol }, null);
        if(mCursor!=null) {
            while (mCursor.moveToNext()) {
                long  dateTime = Long.parseLong(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CREATED)));
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                String dateTimeString = format.format(new Date(dateTime));
                float value =Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));

                totalValue+= value;
                min = Math.min(min,value);
                max = Math.max(max,value);

                dataset.addPoint(getPoint(dateTimeString,value));
            }
            avg = totalValue/mCursor.getCount();
            min -= 2f;
            max += 2f;
            min = Math.max(0,min);

            Paint thresPaint = new Paint();
            thresPaint.setColor(Color.YELLOW);
            thresPaint.setStyle(Paint.Style.STROKE);
            thresPaint.setAntiAlias(true);
            thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
            thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
            lineChartView.setValueThreshold(avg,avg,thresPaint);
            lineChartView.setAxisBorderValues((int)min,(int)max);
            lineChartView.addData(dataset);
            lineChartView.show();
        }

    }


    public void setUpGraph(){

        Paint gridPaint = new Paint();
        gridPaint.setColor(ContextCompat.getColor(this,R.color.material_blue_700));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        lineChartView.setGrid(ChartView.GridType.HORIZONTAL,gridPaint);

        lineChartView.setBackgroundColor(ContextCompat.getColor(this,R.color.material_blue_500));
        lineChartView.setAxisColor(Color.WHITE);
        lineChartView.setLabelsColor(Color.WHITE);
        lineChartView.setFontSize((int)Tools.fromDpToPx(14));
        lineChartView.setBorderSpacing(Tools.fromDpToPx(32));

        dataset = new LineSet();
        dataset.setColor(Color.WHITE);

    }

    private Point getPoint(String label,float value){
        Point point = new Point(label,value);
        point.setColor(ContextCompat.getColor(this,R.color.material_blue_500));
        point.setStrokeColor(Color.WHITE);
        point.setRadius(Tools.fromDpToPx(6));
        return point;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
