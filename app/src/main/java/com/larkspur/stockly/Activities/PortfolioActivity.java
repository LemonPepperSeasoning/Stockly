package com.larkspur.stockly.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.StockAdaptor;
import com.larkspur.stockly.Adaptors.StockCategoriesMainAdatper;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IPortfolio;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Portfolio;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PortfolioActivity extends AppCompatActivity {

    DrawerLayout _drawerLayout;
    private PieChart chart;
    private Typeface tf;
    private TextView tvX, tvY;
    private SeekBar seekBarX, seekBarY;
    protected Typeface tfRegular;
    protected Typeface tfLight;

    private IPortfolio _portfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_portfolio);
        _drawerLayout = findViewById(R.id.drawer_layout);

        _portfolio = Portfolio.getInstance();
        setPiechart();
//            setData(_portfolio.getPortfolio());

        // THIS CODE BELOW IS TEMPLORY TO GENERATE PORTFOLIO
        fetchStockByCategory(Category.HealthCare);
    }

    private void fetchStockByCategory(Category category) {

        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company_v2")
                .whereEqualTo("Category", category.toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot results = task.getResult();
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("+++++", document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();
                        HistoricalPrice tmpHistoricalPrice = new HistoricalPrice((List<Double>) data.get("Price"));
                        IStock tmpStock = new Stock(
                                ((String) data.get("Name")),
                                ((String) data.get("Symbol")),
                                (Category.getValue((String) data.get("Category"))),
                                ((String) data.get("Subindustry")),
                                ((String) data.get("location")),
                                tmpHistoricalPrice);
                        stockList.add(tmpStock);
                    }

                    System.out.println("============================");
                    System.out.println(stockList.size());
                    for (IStock i : stockList) {
                        Log.d("== Stock : ", i.getCompName() + " " + i.getCategory() + " " + i.getSymbol() + " == ");
                    }
                    System.out.println("============================");

                    if (stockList.size() > 0) {
                        Log.i("Getting colors", "Success");

                        int counter = 1;
                        for (IStock i : stockList){
                            _portfolio.addStock(i,counter);
                            counter++;
                            if (counter >= 7){
                                break;
                            }
                        }
                        setData(_portfolio.getPortfolio());

                    } else {
//                        Toast.makeText(getBaseContext(), "Colors Collection was empty!", Toast.LENGTH_LONG).show();
                    }
                } else {
//                    Toast.makeText(getBaseContext(), "Loading colors collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void setPiechart(){
        chart = findViewById(R.id.doughnut_chart);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(0, 0, 0, 0);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        //Transparent
//        chart.setHoleColor(Color.parseColor("#00ff0000"));
        chart.setHoleColor(Color.BLACK);

        chart.setTransparentCircleColor(Color.parseColor("#00ff0000"));
        chart.setTransparentCircleAlpha(0);

        chart.setHoleRadius(68f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

//        // add a selection listener
//        chart.setOnChartValueSelectedListener(this);
//        seekBarX.setProgress(4);
//        seekBarY.setProgress(10);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

//        Legend l = chart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);

        chart.getLegend().setEnabled(false);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);
    }

    private void setData(Hashtable<IStock, Integer> portfolio) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        //TODO : Sort portfolio by total price
        List<IStock> sortedPortfolio = new ArrayList<>();
        sortedPortfolio.addAll(portfolio.keySet());

        int index = 0;
        for (IStock s : sortedPortfolio) {
            Double x = s.getPrice()*portfolio.get(s);
            entries.add(new PieEntry( x.floatValue(),s.getSymbol()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Portfolio");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("Total value:\n$10640\nToday:+0.6%");
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);

        s.setSpan(new RelativeSizeSpan(1.7f), 0, 13, 0);

        s.setSpan(new StyleSpan(Typeface.NORMAL), 13, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(1.2f), 13, s.length() - 11, 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 11, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 11, s.length(), 0);
        return s;
    }

    public void clickMenu(View view){
        MainActivity.openDrawer(_drawerLayout);
    }

    public void clickCloseSideMenu(View view){
        MainActivity.closeDrawer(_drawerLayout);
    }

    public void clickHome(View view){
        MainActivity.redirectActivity(this,MainActivity.class);
    }

    public void clickPortfolio(View view){
        recreate();
    }

    public void clickWatchlist(View view){
        MainActivity.redirectActivity(this,WatchlistActivity.class);
    }

    public void clickSettings(View view){
        MainActivity.redirectActivity(this,SettingsActivity.class);
    }

    public void clickHelp(View view) {
        MainActivity.redirectActivity(this,HelpActivity.class);
    }

    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.closeDrawer(_drawerLayout);
    }
}