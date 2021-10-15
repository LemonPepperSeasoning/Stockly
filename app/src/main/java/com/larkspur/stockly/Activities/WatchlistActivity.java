package com.larkspur.stockly.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Adaptors.WatchlistAdapter;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.UserInfo;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WatchlistActivity extends CoreActivity implements SearchView.OnQueryTextListener {

    private class ViewHolder {
        ListView _watchlistView;
        LinearLayout _return;
        TextView _previousScreen;

        public ViewHolder() {
            _watchlistView = findViewById(R.id.watchlist_view);
            _drawerLayout = findViewById(R.id.drawer_layout);
            _return = findViewById(R.id.return_view);
            _previousScreen = findViewById(R.id.previous_screen_text_view);
            _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        }
    }

    private ViewHolder _vh;

    //        =======================Search functionality=============================

    ListView list;
    String[] stockNameList;
    private UserInfo _userInfo;

    //        =======================--------------------=============================


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
            System.out.println("previous class: " + intent.getExtras().getSerializable("Class"));
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
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

        // Set up the searchbar settings
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

        //        =======================--------------------=============================
    }

    private void getWatchList() {
        IWatchlist watchlist = Watchlist.getInstance();
        List<IStock> stockList = watchlist.getWatchlist();
        Toast.makeText(this, "watchlist size is " + stockList.size(), Toast.LENGTH_SHORT).show();
        ;
        if (stockList.size() > 0) {
            propagateAadapter(stockList);
        }
    }

    private void propagateAadapter(List<IStock> data) {
        WatchlistAdapter stockAdapter = new WatchlistAdapter(this, R.layout.watchlist_item, data);
        _vh._watchlistView.setAdapter(stockAdapter);
        _vh._watchlistView.setVisibility(View.VISIBLE);
    }

    @Override
    public void clickWatchlist(View view) {
        closeDrawer(_drawerLayout);
    }
//
//    public void clickSettings(View view) {
//        MainActivity.redirectActivity(this, SettingsActivity.class);
//    }
//
//    public void clickHelp(View view) {
//        MainActivity.redirectActivity(this, HelpActivity.class);
//    }
//
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(_drawerLayout);
    }

    public void clickReturn(View view) {
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        if (activity == StockActivity.class) {
            Bundle bundle = intent.getExtras();
            System.out.println(bundle);
            System.out.println("watch list stock is");
            System.out.println(bundle.getSerializable("stock"));
            intent.putExtras(bundle);
            redirectActivity(this, activity, bundle);
        } else {
            redirectActivity(this, activity);
        }
    }
}