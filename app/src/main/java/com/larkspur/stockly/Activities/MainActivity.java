package com.larkspur.stockly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.larkspur.stockly.Adaptors.StockAdaptor;
import com.larkspur.stockly.Adaptors.StockCategoriesMainAdatper;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout _drawerLayout;
    StockAdaptor _stockAdaptor;
    RecyclerView _mostPopular;
    //ListView _categories;
    private ViewHolder _vh;
    private class ViewHolder{
        RecyclerView _techView, _financeView, _industryView, _healthView;

        public ViewHolder(){
            _techView = findViewById(R.id.technology_recycle_view);
            _financeView = findViewById(R.id.finance_recycle_view);
            _industryView = findViewById(R.id.industrial_recycle_view);
            _healthView = findViewById(R.id.health_recycle_view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _mostPopular = (RecyclerView) findViewById(R.id.most_popular_view);
     //   _categories = (ListView) findViewById(R.id.categories_view);
        _drawerLayout = findViewById(R.id.drawer_layout);
        _vh = new ViewHolder();
        //set horizontal recycler view
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        _mostPopular.setLayoutManager(lm);

    }

    public void clickMenu(View view) {
        openDrawer(_drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void clickCloseSideMenu(View view) {
        closeDrawer(_drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //When drawer is open, close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void clickHome(View view) {
        closeDrawer(_drawerLayout);
    }

    public void clickPortfolio(View view) {
        redirectActivity(this, PortfolioActivity.class);
    }

    public void clickWatchlist(View view) {
        redirectActivity(this, WatchlistActivity.class);
    }

    public void clickSettings(View view) {
        redirectActivity(this, SettingsActivity.class);
    }

    public void clickHelp(View view) {
        redirectActivity(this, HelpActivity.class);
    }


    public static void redirectActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(_drawerLayout);
    }

    public void propgateAdapter(List<IStock> data){
        StockCategoriesMainAdatper stockCatAdatper = new StockCategoriesMainAdatper(data);
    }

//    private void propogateCategoriesAdapter(List<IStock> data, RecyclerView view){
//        StockCategoriesMainAdatper stockCatAdapter = new StockCategoriesMainAdatper(data);
//    }
}
