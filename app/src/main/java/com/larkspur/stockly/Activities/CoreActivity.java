package com.larkspur.stockly.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.larkspur.stockly.Data.DataCache;
import com.larkspur.stockly.Data.IDataCache;
import com.larkspur.stockly.Models.IUser;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.R;

/**
 * This class is an abstract class which defines the implementation of the drawer layout for the
 * side menu and the search bar (removes duplicate code).
 * Author: Jonathon, Alan
 */

public abstract class CoreActivity extends AppCompatActivity {

    protected DrawerLayout _drawerLayout;
    protected IUser _user;
    protected IDataCache _dataCache;

    /**
     * Default constructor
     */
    public CoreActivity(){
        _dataCache = DataCache.getInstance();
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

    /**
     * Handles click functionality for redirecting to the search screen.
     * @param view SearchView
     */
    public void clickSearch(View view) {
        redirectActivity(this, SearchActivity.class);
    }

    
}
