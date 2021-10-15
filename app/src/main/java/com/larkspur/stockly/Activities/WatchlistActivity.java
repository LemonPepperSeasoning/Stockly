package com.larkspur.stockly.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class WatchlistActivity extends DrawerActivity implements SearchView.OnQueryTextListener {

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
    SearchListViewAdaptor _adaptor;
    SearchView editsearch;
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

    //        =======================Search functionality=============================

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        _adaptor.filter(text);
        return false;
    }

    public void clickSearch(View view) {
        ListView listview = findViewById(R.id.searchList);
        listview.setVisibility(View.VISIBLE);

        // Show the keyboard
        editsearch.setFocusable(true);
        editsearch.setIconified(false);
        editsearch.requestFocusFromTouch();

        // Show text
        EditText searchEditText = (EditText) editsearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setCursorVisible(true);

        // Fetch the stock data for suggestions
        fetchAllStocks();
    }

    public void closeSearch(View view) {
        // Collapse searchList
        ListView listview = findViewById(R.id.searchList);
        listview.setVisibility(View.GONE);

        //Hide keyboard
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

        //Stop blinking in searchbar
        EditText searchEditText = (EditText) editsearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setCursorVisible(false);

        //Clear text in searchbar
        searchEditText.setText("");
    }

    //        =======================--------------------=============================

    //        =======================Search functionality=============================

    private void fetchAllStocks() {
        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company_v2")
                .whereEqualTo("Category", Category.InformationTechnology.toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot results = task.getResult();
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("+++++", document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();
                        HistoricalPrice tmpHistoricalPrice = new HistoricalPrice((List<Double>) data.get("Price"));
                        IStock tmpStock = new Stock(
                                ((String) data.get("Name")),
                                ((String) data.get("Symbol")),
                                (Category.getValue((String) data.get("Category"))),
                                ((String) data.get("Subindustry")),
                                ((String) data.get("location")),
                                tmpHistoricalPrice);
                        stockList.add(tmpStock);
                    }

                    System.out.println("============================");
                    System.out.println(stockList.size());
                    for (IStock i : stockList) {
                        Log.d("== Stock : ", i.getCompName() + " " + i.getCategory() + " " + i.getSymbol() + " == ");
                    }
                    System.out.println("============================");

                    if (stockList.size() > 0) {
                        Log.i("Getting colors", "Success");

                        _adaptor.addData(stockList);
                        // Once the task is successful and data is fetched, propagate the adaptor
                        //  propagateAdaptor(stockList);

                        // Hide the ProgressBar
//                        vh.progressBar.setVisibility(View.GONE);
                    } else {
//                        Toast.makeText(getBaseContext(), "Colors Collection was empty!", Toast.LENGTH_LONG).show();
                    }
                } else {
//                    Toast.makeText(getBaseContext(), "Loading colors collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //        =======================--------------------=============================

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

//    public void clickMenu(View view) {
//        MainActivity.openDrawer(_vh._drawerLayout);
//    }
//
//    public void clickCloseSideMenu(View view) {
//        MainActivity.closeDrawer(_vh._drawerLayout);
//    }
//
//    public void clickHome(View view) {
//        MainActivity.redirectActivity(this, MainActivity.class);
//    }
//
//    public void clickPortfolio(View view) {
//        MainActivity.redirectActivity(this, PortfolioActivity.class);
//    }
//
//    public void clickWatchlist(View view) {
//        recreate();
//    }
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