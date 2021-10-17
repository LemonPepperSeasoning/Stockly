package com.larkspur.stockly.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.widget.ListView;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;

import com.larkspur.stockly.Data.DataFetcher;
import com.larkspur.stockly.Models.IHistoricalPrice;
import com.larkspur.stockly.Models.IPortfolio;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Portfolio;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This class handles the Stock Screen which holds the information on a specific stock. This screen
 * can be accessed in multiple ways: from watchlist, portfolio, main screen, the searchList and the
 * list screen. This screen displays the stock symbol, name, description, category as well as a
 * historical data table. The user also has the option to add/remove stocks from the watchlist
 * and portfolio.
 * Author: Takahiro, Alin, Jonathon
 */
public class StockActivity extends CoreActivity implements SeekBar.OnSeekBarChangeListener,
        SearchView.OnQueryTextListener {
                
    private class ViewHolder {
        TextView _stockName, _stockNameAndSymbol, _stockPrice, _stockPercent, _previousScreen, _description;
        LinearLayout _return;
        ImageView _stockImage, _addWatchList, _addPortfolio;

        public ViewHolder() {
            _stockName = findViewById(R.id.stock_name);
            _stockNameAndSymbol = findViewById(R.id.stock_name_and_symbol);
            _stockPrice = findViewById(R.id.stock_price);
            _stockPercent = findViewById(R.id.stock_percent);
            _return = findViewById(R.id.return_view);
            _previousScreen = findViewById(R.id.previous_screen_text_view);
            _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            _stockImage = findViewById(R.id.stock_image_view);
            _addWatchList = findViewById(R.id.watchlist_image_view);
            _addPortfolio = findViewById(R.id.portfolio_image_view);
            _description = findViewById(R.id.stock_description_view);
        }
    }

    private LineChart chart;
    private Typeface tfLight;
    private ViewHolder _vh;
    private IStock _stock;
    private boolean _watchlisted;
    private IWatchlist _watchlist;
    private int _currentImageIndex;
    ListView list;

    /**
     * Initialises all processes for the screen once screen is launched.
     * @param savedInstanceState default input (Any saved stock or user information)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        _vh = new ViewHolder();
        _currentImageIndex = 0;

        if (getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            _stock = (IStock) bundle.getSerializable("stock");

            DataFetcher.downloadImage(_stock.getImageLink().get(_currentImageIndex),_vh._stockImage);

            _watchlist = User.getInstance().getWatchlist();
            _watchlisted = _watchlist.hasStock(_stock);
            if (_watchlisted == false) {
                _vh._addWatchList.setImageResource(R.drawable.grey_watchlist_button);
            } else {
                _vh._addWatchList.setImageResource(R.drawable.red_watchlist_button);
            }

            System.out.println("STOCK STARTS HERE");
            System.out.println(intent.getStringExtra("Screen"));
            System.out.println("previous class: " + intent.getExtras().getSerializable("Class"));
            String previousScreen = intent.getStringExtra("Screen");
            if (previousScreen != null) {
                _vh._previousScreen.setText("Return to " + previousScreen);
            }else{
                _vh._previousScreen.setText("Return to Home");
            }

            _vh._description.setText(_stock.getDesc());
            System.out.println(_stock.getDesc());
            setupStockView();
            setupGraph();

            Toast.makeText(this, _stock.getSymbol() + " was Launched!", Toast.LENGTH_SHORT).show();
        } else {
            throw new RuntimeException("Stock not found!");
        }

        //        =======================Search functionality=============================

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.searchList);

        _adaptor = new SearchListViewAdaptor(this, R.layout.search_list_item, new ArrayList<>());

        // Binds the Adapter to the ListView
        list.setAdapter(_adaptor);

        // Locate the EditText in listview_main.xml
        _editSearch = (SearchView) findViewById(R.id.search);
        _editSearch.setOnQueryTextListener(this);

        // Set up the searchbar settings
        _editSearch.clearFocus();
        _editSearch.requestFocusFromTouch();

        if (_user.getPortfolio().getQuantity(_stock.getSymbol()) >= 1) {
            _vh._addPortfolio.setImageResource(R.drawable.blue_portfolio_button);
        } else {
            _vh._addPortfolio.setImageResource(R.drawable.grey_portfolio_button);
        }

        //        =======================--------------------=============================
    }


    /**
     * Initialises the stock view (contains the company name, symbol and price of stock)
     */
    //        =======================Search functionality=============================
    private void setupStockView() {
        _vh._stockName.setText(_stock.getCompName());
        _vh._stockNameAndSymbol.setText(_stock.getCompName() + " (" + _stock.getSymbol() + ")");

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedPrice = df.format(_stock.getPrice());
        _vh._stockPrice.setText(formattedPrice);
    }

    /**
     * Initialises the historical data graph.
     */
    private void setupGraph() {
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
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);

        setData(_stock.getHistoricalPrice());

        // don't forget to refresh the drawing
        chart.invalidate();
    }

    /**
     * Fetches historical data for the graph.
     * @param prices Historical data for a stock from the last month (25 days)
     */
    private void setData(@NonNull IHistoricalPrice prices) {
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
            data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
        }
    }

    /**
     * Initialises the contents of the activity's stnadard options menu.
     * @param menu the option menu where you place your items.
     * @return true if menu is displayed, false if it is not shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    /**
     * Called whenever the items in the menu is selected by a user
     * @param item the menu item which was selected.
     * @return true if intiate action, false to resume processes and ignore.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    /**
     * Called when process is changed.
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        return;
    }

    /**
     *
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /**
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }



    /**
     * Click functionality for home button in side menu
     * @param view home_button from main_nav_drawer.xml
     */
    @Override
    public void clickHome(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        redirectActivity(this, MainActivity.class, bundle);
    }

    /**
     * Click functionality for portfolio button in side menu
     * @param view portfolio_button from main_nav_drawer.xml
     */
    @Override
    public void clickPortfolio(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        redirectActivity(this, PortfolioActivity.class, bundle);
    }

    /**
     * Click functionality for watchlist button in side menu
     * @param view watchlist_button from main_nav_drawer.xml
     */
    @Override
    public void clickWatchlist(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        redirectActivity(this, WatchlistActivity.class, bundle);
    }

    /**
     * Click functionality for settings button in side menu
     * @param view settings_button from main_nav_drawer.xml
     */
    @Override
    public void clickSettings(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        redirectActivity(this, SettingsActivity.class, bundle);
    }

    /**
     * Click functionality for help button in side menu
     * @param view help_button from main_nav_drawer.xml
     */
    @Override
    public void clickHelp(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        redirectActivity(this, HelpActivity.class, bundle);
    }

    /**
     * Default method for committing any user interaction with screen when the screen is
     * closed or the user switches to another screen. This allows the screen to "resume" once
     * the user returns to the screen.
     */
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(_drawerLayout);
    }

    /**
     * Handles click functionality for return text
     * @param view TextView
     */
    public void clickReturn(View view) {
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        if (activity == null){
            activity = MainActivity.class;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        IStock test = (IStock) bundle.getSerializable("stock");

        intent.putExtras(bundle);
        redirectActivity(this, activity, bundle);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    /**
     * Handles click functionality for adding and removing stocks from the watchlist
     * @param view LinearLayout with ImageView and TextView
     */
    public void clickAddWatchlist(View view) {
        if (_watchlisted == false) {
            _watchlist.addStock(_stock);
            _vh._addWatchList.setImageResource(R.drawable.red_watchlist_button);
            Toast.makeText(this, "added " + _stock.getCompName() + " to watchlist", Toast.LENGTH_SHORT).show();
            _watchlisted = true;

        } else {
            _watchlist.removeStock(_stock);
            _vh._addWatchList.setImageResource(R.drawable.grey_watchlist_button);
            Toast.makeText(this, "removed " + _stock.getCompName() + " to watchlist", Toast.LENGTH_SHORT).show();
            _watchlisted = false;
        }
    }

    /**
     * Handles click functionality for adding and removing stocks from the portfolio
     * @param view LinearLayout with ImageView and TextView
     */
    public void clickAddPortfolio(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_portfolio_popup, null);
        PopupWindow popupWindow = new PopupWindow(popupView, DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        IPortfolio portfolio = User.getInstance().getPortfolio();
        Button closePopup = (Button) popupView.findViewById(R.id.add_to_portfolio_confirmbutton);
        EditText numberOfStocks = (EditText) popupView.findViewById(R.id.add_to_portfolio_edittext);
        ImageView plus = (ImageView) popupView.findViewById(R.id.plus_view);
        ImageView minus = (ImageView) popupView.findViewById(R.id.minus_view);
        numberOfStocks.setText("0");

        Toast.makeText(this, "added " + _stock.getCompName() + " to portfolio", Toast.LENGTH_SHORT).show();
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(numberOfStocks.getText().toString());
                if (!numberOfStocks.getText().toString().equals("")) {
                    int numStocks = Integer.parseInt(numberOfStocks.getText().toString());
                    if (numStocks != 0) {
                        portfolio.addStock(_stock, numStocks);
                    }
                } else {
                    Toast.makeText(view.getContext(), "A number needs to be input", Toast.LENGTH_SHORT).show();
                }

                if (portfolio.getQuantity(_stock.getSymbol()) > 0) {
                    _vh._addPortfolio.setImageResource(R.drawable.blue_portfolio_button);
                } else {
                    _vh._addPortfolio.setImageResource(R.drawable.grey_portfolio_button);
                }

                popupWindow.dismiss();
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(numberOfStocks.getText().toString());
                if (numberOfStocks.getText().toString().equals("") || numberOfStocks.getText().toString().equals("0")) {
                } else {
                    int newNumStocks = Integer.parseInt(numberOfStocks.getText().toString()) - 1;
                    numberOfStocks.setText(Integer.toString(newNumStocks));
                }
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(numberOfStocks.getText().toString());
                if (numberOfStocks.getText().toString().equals("")) {
                    numberOfStocks.setText("1");
                } else {
                    int newNumStocks = Integer.parseInt(numberOfStocks.getText().toString()) + 1;
                    numberOfStocks.setText(Integer.toString(newNumStocks));
                }
            }
        });
    }

    /**
     * Allows the user to navigate to the image on the left
     * @param view ImageView
     */
    public void clickNextImageLeft(View view) {
        _currentImageIndex += 2; //same as -1
        DataFetcher.downloadImage(_stock.getImageLink().get(_currentImageIndex % 3),_vh._stockImage);
    }

    /**
     * Allows the user to navigate to the image on the right
     * @param view ImageView
     */
    public void clickNextImageRight(View view) {
        _currentImageIndex += 1;
        DataFetcher.downloadImage(_stock.getImageLink().get(_currentImageIndex % 3),_vh._stockImage);
    }
}
