package com.larkspur.stockly.Adaptors.utils;

import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.larkspur.stockly.Models.IHistoricalPrice;

import java.util.ArrayList;

public class LineChartHandler {

    private Typeface tfLight;

    /**
     * Initialises the historical data graph.
     */
    public static void setupGraph(LineChart chart, Boolean displayYaxis, int backgroundColor) {
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setBackgroundColor(backgroundColor);

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
        y.setDrawGridLines(false);
        if (displayYaxis){
            y.setLabelCount(6, false);
            y.setTextColor(Color.WHITE);
            y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            y.setAxisLineColor(Color.WHITE);
        }
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);

        // don't forget to refresh the drawing
        chart.invalidate();
    }

    /**
     * Fetches historical data for the graph.
     * @param prices Historical data for a stock from the last month (25 days)
     */
    public static void setData(@NonNull IHistoricalPrice prices, LineChart chart) {
        ArrayList<Entry> values = new ArrayList<>();

        int index = 0;
        for (Double i : prices.getHistoricalPrice()) {
            values.add(new Entry(index, i.floatValue()));
            index++;
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
//            data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
            chart.invalidate();
        }
    }
}
