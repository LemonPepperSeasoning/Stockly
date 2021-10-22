package com.larkspur.stockly.Activities;

import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.larkspur.stockly.Activities.utils.PieChartHandler;
import com.larkspur.stockly.Adaptors.PorfolioAdapter;
import com.larkspur.stockly.Models.IPortfolio;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the Portfolio screen which contains the stock in a user's portfolio. The
 * portfolio is essentially the stocks a user has invested in. The purpose of this screen is to
 * show the user a holistic perspective on their investment and calculate how much of their money
 * is invested in each specific stock. The user has the ability to also
 * The user has the ability to remove and go to the stock screen for the stocks in the portfolio.
 * There is also a doughnut graph with an interactive legend.
 * Author: Jonathon, Alan
 * Reference: https://github.com/PhilJay/MPAndroidChart (used library for graph)
 */
public class PortfolioActivity extends CoreActivity implements SearchView.OnQueryTextListener {

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        ListView _stockList;
        TextView _previousScreen,_usernameText;
        LinearLayout _return;

        public ViewHolder() {
            _stockList = findViewById(R.id.portfolio_stocklist);
            _drawerLayout = findViewById(R.id.drawer_layout);
            _return = findViewById(R.id.return_view);
            _previousScreen = findViewById(R.id.previous_screen_text_view);
            _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            _usernameText = (TextView) findViewById(R.id.username);
            _usernameText.setText("Hi " + _user.getUsername());
        }
    }

    private ViewHolder _vh;
    private PieChart _chart;
    private IPortfolio _portfolio;
    ListView list;

    private PorfolioAdapter _portfolioAdapter;
    private List<IStock> _portfolioStocks;

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
        _portfolio = _user.getPortfolio();

        if (getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
//            String previousScreen = intent.getStringExtra("Screen");
            _vh._previousScreen.setText("Return to Home");
        }else{
            throw new RuntimeException("Something went wrong : previous screen not found");
        }

        _chart = findViewById(R.id.doughnut_chart);
        _portfolioStocks = new ArrayList<>();
        _portfolioAdapter = new PorfolioAdapter(this, R.layout.portfolio_card,_portfolioStocks, _chart);
        _vh._stockList.setAdapter(_portfolioAdapter);
        _vh._stockList.setVisibility(View.VISIBLE);

        PieChartHandler.setPiechart(_chart,PieChartHandler.generateCenterSpannableText(_portfolio));
        displayData();

    }

    /**
     * Handles display of Portfolio data (size of Portfolio)
     */
    private void displayData(){
        PieChartHandler.setData(_portfolio.getPortfolio(), _chart);
        Toast.makeText(this,"watchlist size is " + _portfolio.getPortfolio().size(), Toast.LENGTH_SHORT).show();;
        if(_portfolio.getPortfolio().size()>0){
            _portfolioStocks.clear();
            _portfolioStocks.addAll(_portfolio.getPortfolio().keySet());
            _portfolioAdapter.notifyDataSetChanged();
        }
    }



    /**
     * Click functionality for portfolio button for side menu (overwritten to avoid the
     * portfolio screen from having to reinitialise if clicked in the side menu)
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
            redirectActivity(this, MainActivity.class);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//        }
    }
}