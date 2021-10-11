package com.larkspur.stockly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.larkspur.stockly.Adaptors.WatchlistAdapter;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.util.List;

public class WatchlistActivity extends AppCompatActivity {


    private class ViewHolder {
        ListView _watchlistView;
        DrawerLayout _drawerLayout;

        public ViewHolder() {
            _watchlistView = findViewById(R.id.watchlist_view);
            _drawerLayout = findViewById(R.id.drawer_layout);
        }
    }

    private ViewHolder _vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        _vh = new ViewHolder();
        getWatchList();
    }

    private void getWatchList(){
        IWatchlist watchlist = Watchlist.getInstance();
        List<IStock> stockList = watchlist.getWatchlist();
        Toast.makeText(this,"watchlist size is " + stockList.size(), Toast.LENGTH_SHORT).show();;
        if(stockList.size()>0) {
            propagateAadapter(stockList);
        }
    }

    private void propagateAadapter(List<IStock> data){
        WatchlistAdapter stockAdapter = new WatchlistAdapter(this, R.layout.watchlist_item,data);
        _vh._watchlistView.setAdapter(stockAdapter);
        _vh._watchlistView.setVisibility(View.VISIBLE);
    }

    public void clickMenu(View view) {
        MainActivity.openDrawer(_vh._drawerLayout);
    }

    public void clickCloseSideMenu(View view) {
        MainActivity.closeDrawer(_vh._drawerLayout);
    }

    public void clickHome(View view) {
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void clickPortfolio(View view) {
        MainActivity.redirectActivity(this, PortfolioActivity.class);
    }

    public void clickWatchlist(View view) {
        recreate();
    }

    public void clickSettings(View view) {
        MainActivity.redirectActivity(this, SettingsActivity.class);
    }

    public void clickHelp(View view) {
        MainActivity.redirectActivity(this, HelpActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(_vh._drawerLayout);
    }
}