package com.larkspur.stockly.Activities;

import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.larkspur.stockly.Adaptors.BasicStockAdapter;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity handles the WatchList Screen. The watchlist holds the stocks in which the
 * user may be interested in, but has not put inside their portfolio. The user has the option
 * to remove a
 */
public class WatchlistActivity extends CoreActivity {

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        ListView _watchlistView;
        LinearLayout _return;
        TextView _previousScreen, _usernameText;

        public ViewHolder() {
            _watchlistView = findViewById(R.id.watchlist_view);
            _drawerLayout = findViewById(R.id.drawer_layout);
            _return = findViewById(R.id.return_view);
            _previousScreen = findViewById(R.id.previous_screen_text_view);
            _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            _usernameText = (TextView) findViewById(R.id.username);
            _usernameText.setText("Hi " + _user.getUsername());
        }
    }

    private ViewHolder _vh;
    private BasicStockAdapter _listAdapter;
    private List<IStock> _watchlistStocks;
  
     /**
     * Initialises all processes for the screen once screen is launched.
     * @param savedInstanceState default input (Any saved stock or user information)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        _vh = new ViewHolder();
        if (getIntent().getExtras() != null) {
            _vh._previousScreen.setText("Return to Home");
        } else {
            throw new RuntimeException("Stock not found!");
        }

        _watchlistStocks = new ArrayList<>();
        _listAdapter = new BasicStockAdapter(this, R.layout.watchlist_item, _watchlistStocks);
        _vh._watchlistView.setAdapter(_listAdapter);
        _vh._watchlistView.setVisibility(View.VISIBLE);

        getWatchList();

    }

    /**
     * Fetches watchlist data and displays the watchlist size.
     */
    private void getWatchList() {
        IWatchlist watchlist = _user.getWatchlist();
        List<IStock> stockList = watchlist.getWatchlist();
        Toast.makeText(this, "watchlist size is " + stockList.size(), Toast.LENGTH_SHORT).show();

        if (stockList.size() > 0) {
            _watchlistStocks.clear();;
            _watchlistStocks.addAll(stockList);
        }
    }

    /**
     * Click functionality for watchlist button for side menu (overwritten to avoid the
     * watchlist screen from having to reinitialise if clicked in side menu)
     * @param view watchlist_button from main_nav_drawer.xml
     */
    @Override
    public void clickWatchlist(View view) {
        closeDrawer(_drawerLayout);
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
            redirectActivity(this, MainActivity.class);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}