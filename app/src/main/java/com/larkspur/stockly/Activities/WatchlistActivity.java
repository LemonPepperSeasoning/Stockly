package com.larkspur.stockly.Activities;

import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Adaptors.WatchlistAdapter;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;


/**
 * This activity handles the WatchList Screen. The watchlist holds the stocks in which the
 * user may be interested in, but has not put inside their portfolio. The user has the option
 * to remove a
 */
public class WatchlistActivity extends CoreActivity implements SearchView.OnQueryTextListener {

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
    ListView list;
  
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
            Intent intent = this.getIntent();
            String previousScreen = intent.getStringExtra("Screen");
            _vh._previousScreen.setText("Return to " + previousScreen);
        } else {
            throw new RuntimeException("Stock not found!");
        }
        getWatchList();

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
        //        =======================--------------------=============================
    }

    /**
     * Fetches watchlist data and displays the watchlist size.
     */
    private void getWatchList() {
        IWatchlist watchlist = _user.getWatchlist();
        List<IStock> stockList = watchlist.getWatchlist();
        Toast.makeText(this, "watchlist size is " + stockList.size(), Toast.LENGTH_SHORT).show();

        if (stockList.size() > 0) {
            propagateAadapter(stockList);
        }
    }

    /**
     * Creates adaptor for ListViews which displays stocks in the RecyclerView.
     * @param data Stock information list
     */
    private void propagateAadapter(List<IStock> data) {
        WatchlistAdapter stockAdapter = new WatchlistAdapter(this, R.layout.watchlist_item, data);
        _vh._watchlistView.setAdapter(stockAdapter);
        _vh._watchlistView.setVisibility(View.VISIBLE);
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
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        if (activity == StockActivity.class) {
            Bundle bundle = intent.getExtras();
            intent.putExtras(bundle);
            redirectActivity(this, activity, bundle);
        } else {
            redirectActivity(this, activity);
        }
    }
}