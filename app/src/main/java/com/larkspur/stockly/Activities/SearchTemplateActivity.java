package com.larkspur.stockly.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.larkspur.stockly.R;

/**
 * This is a mockup of the search functionality. Once this is completed, the search bar will
 * be implemented on every screen with an interface for all the methods in this class.
 * Author: Jonathon
 */

public class SearchTemplateActivity extends AppCompatActivity {
    DrawerLayout _drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        _drawerLayout = findViewById(R.id.drawer_layout);
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
        MainActivity.redirectActivity(this,PortfolioActivity.class);
    }

    public void clickWatchlist(View view){
        recreate();
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
