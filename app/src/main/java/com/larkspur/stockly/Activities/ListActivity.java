package com.larkspur.stockly.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.larkspur.stockly.R;

public class ListActivity extends AppCompatActivity {

    DrawerLayout _drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _drawerLayout = findViewById(R.id.drawer_layout);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
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
