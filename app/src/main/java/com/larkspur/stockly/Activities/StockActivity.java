package com.larkspur.stockly.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.R;

import java.util.ArrayList;

public class StockActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private LineChart chart;
    //    private SeekBar seekBarX, seekBarY;
//    private TextView tvX, tvY;
    private Typeface tfLight;
    private DrawerLayout _drawerLayout;

    private class ViewHolder {
        TextView _stockName, _stockNameAndSymbol, _stockPrice, _stockPercent;

        public ViewHolder() {
            _stockName = findViewById(R.id.stock_name);
            _stockNameAndSymbol = findViewById(R.id.stock_name_and_symbol);
            _stockPrice = findViewById(R.id.stock_price);
            _stockPercent = findViewById(R.id.stock_percent);
        }
    }
    private ViewHolder _vh;
    private IStock _stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        _drawerLayout = findViewById(R.id.drawer_layout);
        _vh = new ViewHolder();
        if (getIntent().getExtras() != null) {
            System.out.println("STOCK DATA HERE\n");
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            _stock = (IStock) bundle.getSerializable("stock");
            setupStockView();

            Toast.makeText(this, _stock.getSymbol() + " was Launched!", Toast.LENGTH_SHORT).show();
        }else{
            throw new RuntimeException("Stock not found!");
        }
        setTitle("CubicLineChartActivity");

//        tvX = findViewById(R.id.tvXMax);
//        tvY = findViewById(R.id.tvYMax);

//        seekBarX = findViewById(R.id.seekBar1);
//        seekBarY = findViewById(R.id.seekBar2);

        chart = findViewById(R.id.chart1);
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setBackgroundColor(Color.rgb(46, 46, 51));

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);

        XAxis x = chart.getXAxis();
        x.setEnabled(false);

        YAxis y = chart.getAxisLeft();
        y.setTypeface(tfLight);
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);

        // add data
//        seekBarY.setOnSeekBarChangeListener(this);
//        seekBarX.setOnSeekBarChangeListener(this);
//
//        // lower max, as cubic runs significantly slower than linear
//        seekBarX.setMax(700);
//
//        seekBarX.setProgress(45);
//        seekBarY.setProgress(100);
        setData(45, new Float(100));

        chart.getLegend().setEnabled(false);

        chart.animateXY(2000, 2000);

        // don't forget to refresh the drawing
        chart.invalidate();
    }

    private void setupStockView(){
        _vh._stockName.setText(_stock.getCompName());
        _vh._stockNameAndSymbol.setText(_stock.getCompName() + " (" + _stock.getSymbol() + ")");
        _vh._stockPrice.setText(_stock.getPrice().toString());
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * (range + 1)) + 20;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.rgb(159, 125, 225));
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.rgb(159, 125, 225));
            set1.setFillColor(Color.rgb(159, 125, 225));
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
            data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        return;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    public void clickMenu(View view) {
        MainActivity.openDrawer(_drawerLayout);
    }

    public void clickCloseSideMenu(View view) {
        MainActivity.closeDrawer(_drawerLayout);
    }

    public void clickHome(View view) {
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void clickPortfolio(View view) {
        MainActivity.redirectActivity(this, PortfolioActivity.class);
    }

    public void clickWatchlist(View view) {
        MainActivity.redirectActivity(this, WatchlistActivity.class);
    }

    public void clickSettings(View view) {
        MainActivity.redirectActivity(this, SettingsActivity.class);
    }

    public void clickHelp(View view) {
        MainActivity.redirectActivity(this, HelpActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(_drawerLayout);
    }
}
