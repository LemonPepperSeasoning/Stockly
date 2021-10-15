package com.larkspur.stockly.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
        LinearLayout _return;
        TextView  _previousScreen;

        public ViewHolder() {
            _watchlistView = findViewById(R.id.watchlist_view);
            _drawerLayout = findViewById(R.id.drawer_layout);
            _return = findViewById(R.id.return_view);
            _previousScreen = findViewById(R.id.previous_screen_text_view);
        }
    }

    private ViewHolder _vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        _vh = new ViewHolder();
        if (getIntent().getExtras() != null) {
            System.out.println("STOCK DATA HERE\n");
            Intent intent = this.getIntent();
            System.out.println("STOCK STARTS HERE");
            System.out.println(intent.getStringExtra("Screen"));
            System.out.println("previous class: "+ intent.getExtras().getSerializable("Class"));
            String previousScreen = intent.getStringExtra("Screen");
            _vh._previousScreen.setText("Return to " + previousScreen);
        }else{
            throw new RuntimeException("Stock not found!");
        }
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

    public void clickReturn(View view){
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        if(activity == StockActivity.class){
            Bundle bundle = intent.getExtras();
            System.out.println(bundle);
            System.out.println("watch list stock is");
            System.out.println(bundle.getSerializable("stock"));
           intent.putExtras(bundle);
           MainActivity.redirectActivity(this,activity,bundle);
        }else {
            MainActivity.redirectActivity(this, activity);
        }
    }
}