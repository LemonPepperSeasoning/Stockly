package com.larkspur.stockly.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
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
import com.larkspur.stockly.Adaptors.PorfolioAdapter;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IPortfolio;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Portfolio;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.UserInfo;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class handles the Portfolio screen which contains the stock in a user's portfolio.
 * The user has the ability to remove and got to the stock screen for the stocks in the portfolio.
 * There is also a doughnut graph with an interactive legend.
 * Author: Jonathon, Alan
 * Reference: https://github.com/PhilJay/MPAndroidChart (used library for graph)
 */
public class PortfolioActivity extends CoreActivity implements SearchView.OnQueryTextListener {

    private ViewHolder _vh;
    private PieChart chart;
    protected Typeface tfRegular;
    protected Typeface tfLight;
    private IPortfolio _portfolio;
    ListView list;
    String[] stockNameList;
    private UserInfo _userInfo;

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        ListView _stockList;
        TextView _previousScreen;
        LinearLayout _return;

        public ViewHolder() {
            _stockList = findViewById(R.id.portfolio_stocklist);
            _drawerLayout = findViewById(R.id.drawer_layout);
            _return = findViewById(R.id.return_view);
            _previousScreen = findViewById(R.id.previous_screen_text_view);
            _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        }
    }

    /**
     * Initialises all processes for the screen once screen is launched.
     * @param savedInstanceState default input (Any saved stock or user information)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_portfolio);
        _vh = new ViewHolder();
      
        _portfolio = Portfolio.getInstance();

        if (getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            String previousScreen = intent.getStringExtra("Screen");
            _vh._previousScreen.setText("Return to " + previousScreen);
        }else{
            throw new RuntimeException("Something went wrong : previous screen not found");
        }

        setPiechart();
        displayData();

        //        =======================Search functionality=============================

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.searchList);

        _adaptor = new SearchListViewAdaptor(this, R.layout.search_list_item, new ArrayList<>());

        // Binds the Adapter to the ListView
        list.setAdapter(_adaptor);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

        // Set up the searchbar settings
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

        //        =======================--------------------=============================
    }

    /**
     * Handles display of watchlist data (size of watchlist)
     */
    private void displayData(){
        setData(_portfolio.getPortfolio());
        Toast.makeText(this,"watchlist size is " + _portfolio.getPortfolio().size(), Toast.LENGTH_SHORT).show();;
        if(_portfolio.getPortfolio().size()>0){
            List<IStock> data = new ArrayList<>();
            data.addAll( _portfolio.getPortfolio().keySet() );
            propagateAadapter(data);
        }
    }

    /**
     * Creates adaptor for ListViews which displays stocks in the RecyclerView.
     * @param data Stock information list
     */
    private void propagateAadapter(List<IStock> data){
        PorfolioAdapter stockAdapter = new PorfolioAdapter(this, R.layout.portfolio_card,data,chart);
        _vh._stockList.setAdapter(stockAdapter);
        _vh._stockList.setVisibility(View.VISIBLE);
    }

    /**
     * Displays the doughnut graph displaying the percentage of the stocks
     */
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

        chart.animateY(1400, Easing.EaseInOutQuad);
        chart.getLegend().setEnabled(false);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);
    }

    /**
     * Pulls the data and populates the pie chart with data.
     * @param portfolio Portfolio
     */
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

    /**
     * Handles center text generation
     * @return Spannable String
     */
    private SpannableString generateCenterSpannableText() {
        Double totalValue = _portfolio.getTotalValue();
        Double percentageChange =_portfolio.getTotal24HrChange();

        String topLine = "Total value:\n";
        String middleLine = "$"+String.format("%.2f",totalValue) +"\n";
        String percentage = String.format("%.2f",percentageChange)+"%";
        String bottomLine  = "Today:"+percentage;

        SpannableString s = new SpannableString(topLine+middleLine+bottomLine);

        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(2.2f), 0, topLine.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), topLine.length(), s.length() - bottomLine.length(), 0);
        s.setSpan(new RelativeSizeSpan(2.0f), topLine.length(), s.length() - bottomLine.length(), 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length()-bottomLine.length(), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.8f), s.length()-bottomLine.length(), s.length(), 0);
        if (percentageChange >= 0){
            s.setSpan(new ForegroundColorSpan(Color.GREEN), s.length()-percentage.length(), s.length(), 0);
        }else{
            s.setSpan(new ForegroundColorSpan(Color.RED), s.length()-percentage.length(), s.length(), 0);
        }
        return s;
    }

    /**
     * Click functionality for portfolio button for side menu (overwritten to avoid the
     * portfolio screen from having to reinitialise)
     * @param view portfolio_button from main_nav_drawer.xml
     */
    @Override
    public void clickPortfolio(View view){
        closeDrawer(_drawerLayout);
    }

    /**
     * Default method for committing any user interaction with screen when the screen is
     * closed or the user switches to another screen. This allows the screen to "resume" once
     * the user returns to the screen.
     */
    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(_drawerLayout);
    }

    /**
     * Handles click functionality for return text
     * @param view TextView
     */
    public void clickReturn(View view){
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        if(activity == StockActivity.class){
            Bundle bundle = intent.getExtras();
            intent.putExtras(bundle);
            redirectActivity(this,activity,bundle);
        }else {
            redirectActivity(this, activity);
        }
    }
}