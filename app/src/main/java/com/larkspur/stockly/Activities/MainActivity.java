package com.larkspur.stockly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.larkspur.stockly.Adaptors.StockAdaptor;
import com.larkspur.stockly.R;

public class MainActivity extends AppCompatActivity {

    DrawerLayout _drawerLayout;
    StockAdaptor _stockAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _drawerLayout = findViewById(R.id.drawer_layout);

    }

    public void clickMenu(View view){
        openDrawer(_drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void clickCloseSideMenu(View view){
        closeDrawer(_drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            //When drawer is open, close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void clickHome(View view){
        closeDrawer(_drawerLayout);
    }

    public void clickPortfolio(View view){
        redirectActivity(this,PortfolioActivity.class);
    }

    public void clickWatchlist(View view){
        redirectActivity(this,WatchlistActivity.class);
    }

    public void clickSettings(View view){
        redirectActivity(this,SettingsActivity.class);
    }

    public void clickHelp(View view) {
        redirectActivity(this,HelpActivity.class);
    }


    public static void redirectActivity(Activity activity, Class aClass){
        Intent intent = new Intent(activity,aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(_drawerLayout);
    }
}
