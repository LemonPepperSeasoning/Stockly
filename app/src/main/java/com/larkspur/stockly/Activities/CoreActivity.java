package com.larkspur.stockly.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Data.StockHandler;
import com.larkspur.stockly.Data.mappers.StockMapper;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IUser;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is an abstract class which defines the implementation of the drawer layout for the
 * side menu and the search bar (removes duplicate code).
 * Author: Jonathon, Alan
 */

public abstract class CoreActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener{

    protected DrawerLayout _drawerLayout;
    protected IUser _user;
    protected StockHandler _stockHandler;

    protected SearchListViewAdaptor _adaptor;
    protected SearchView _editSearch;
    protected ListView list;
    protected ListView _searchListView;
    protected EditText _searchEditText;
//    protected ImageView _closeButton;
//    protected ImageView _searchButton;

    /**
     * Default constructor
     */
    public CoreActivity(){
        _stockHandler = StockHandler.getInstance();
        _user = User.getInstance();
    }

    /**
     * Changes the screen without retaining the stock bundle
     * @param activity the activity screen which the user wishes to change to
     * @param aClass class for the screen
     */
    public void redirectActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        String screenName = activity.getTitle().toString();
        System.out.println("this is the screen "+ screenName);
//        Bundle options = ActivityOptions.makeCustomAnimation(activity, R.anim.slide_in_right,R.anim.slide_out_left).toBundle();
        intent.putExtra("Screen", screenName);
        intent.putExtra("Class",activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Changes the screen while retaining the stock bundle (in case stock information
     * must be retained while switching screens)
     * @param activity the activity screen which the user wishes to change to
     * @param aClass class for the screen
     */
    public void redirectActivity(Activity activity, Class aClass, Bundle stock){
        Intent intent = new Intent(activity, aClass);
        String screenName = activity.getTitle().toString();
        intent.putExtra("Screen", screenName);
        intent.putExtra("Class",activity.getClass());
        intent.putExtras(stock);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    //        =======================Side Menu functionality=============================

    /**
     * Click functionality for opening side menu
     * @param view menu button from main_toolbar.xml
     */
    public void clickMenu(View view) {
        openDrawer(_drawerLayout);
    }

    /**
     * Method for opening side menu
     * @param drawerLayout container for side menu
     */
    public void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Click functionality for closing side menu
     * @param view close_button from main_nav_drawer.xml
     */
    public void clickCloseSideMenu(View view) {
        closeDrawer(_drawerLayout);
    }

    /**
     * Method for closing side menu
     * @param drawerLayout container for side menu
     */
    public void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //When drawer is open, close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Click functionality for opening main page from side menu
     * @param view home_button from main_nav_drawer.xml
     */
    public void clickHome(View view){
       redirectActivity(this,MainActivity.class);
    }

    /**
     * Click functionality for opening portfolio screen from side menu
     * @param view portfolio_button from main_nav_drawer.xml
     */
    public void clickPortfolio(View view) {
        redirectActivity(this, PortfolioActivity.class);
    }

    /**
     * Click functionality for opening watchlist screen from side menu
     * @param view watchlist_button from main_nav_drawer.xml
     */
    public void clickWatchlist(View view) {
        redirectActivity(this, WatchlistActivity.class);
    }

    /**
     * Click functionality for opening settings screen from side menu
     * @param view settings_button from main_nav_drawer.xml
     */
    public void clickSettings(View view) {
        redirectActivity(this, SettingsActivity.class);
    }

    /**
     * Click functionality for opening help screen from side menu
     * @param view help_button from main_nav_drawer.xml
     */
    public void clickHelp(View view) {
        redirectActivity(this, HelpActivity.class);
    }

    //        =======================Search functionality=============================

    /**
     * Handles callbacks for changes to the text in the search bar.
     * @param query any changes in text
     * @return boolean of false to searchView to perform default actions (filter)
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Called when the user changes the text and calls the filter function for suggestions
     * @param newText the text input from the user in the search bar
     * @return boolean of false to searchView to perform default actions (filter)
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e("Changed","Text Changed");
        String text = newText;
        _adaptor.filter(text);
        return false;
    }

    /**
     * Handles click functionality for search bar and display changes such as opening
     * keyboard and text cursor and fetches stock data for filtering.
     * @param view SearchView
     */
    public void clickSearch(View view) {
        ListView listview = findViewById(R.id.searchList);
        listview.setVisibility(View.VISIBLE);

        // Show the keyboard
        _editSearch.setFocusable(true);
        _editSearch.setIconified(false);
        _editSearch.requestFocusFromTouch();

        // Show text
        EditText _searchEditText = (EditText) _editSearch.findViewById(androidx.appcompat.R.id.search_src_text);
        _searchEditText.setCursorVisible(true);

        // Show close button
        ImageView _closeButton = (ImageView) _editSearch.findViewById(R.id.search_close_btn);
        _closeButton.setVisibility(View.VISIBLE);

        // Fetch the stock data for suggestions
//        fetchAllStocks();
    }

//    /**
//     * Handles click functionality for search bar and display changes such as collapsing
//     * keyboard and text cursor. Also removes any input text.
//     * @param view SearchView
//     */
//    public void closeSearch(View view) {
//        // Collapse searchList
//        Log.d("closed", "button is pressed");
//        ListView listview = findViewById(R.id.searchList);
//        listview.setVisibility(View.GONE);
//
//        //Hide keyboard
//        _editSearch.clearFocus();
//        _editSearch.requestFocusFromTouch();
//
//        //Stop blinking in searchbar
//        EditText searchEditText = (EditText) _editSearch.findViewById(androidx.appcompat.R.id.search_src_text);
//        searchEditText.setCursorVisible(false);
//
//        //Clear text in searchbar
//        searchEditText.setText("");
//    }

    /**
     * Makes a query to Firestore database for stock information on one thread while
     * another thread executes the java functions (creating stock items using onComplete
     * function). Stock items are created and put inside a list for use. All stock items are
     * called.
     */
    protected void fetchAllStocks(List<IStock> list, SearchListViewAdaptor adapter) {
        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
                   // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        stockList.add(StockMapper.toStock(document.getData()));
                    }

                    if (stockList.size() > 0) {
                        list.addAll(stockList);
                        adapter.filter("");
                        adapter.notifyDataSetChanged();
                        _stockHandler.addAllStock(stockList);
                        Log.e("Size of stock list: ", String.valueOf(list.size()));
                    } else {
                        Log.d("Fetch Failed", "return value was empty");
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch all stocks");
                }
            }
        });
    }
}

//        =======================End of Search functionality=============================
