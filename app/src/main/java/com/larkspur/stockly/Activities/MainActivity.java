package com.larkspur.stockly.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Adaptors.StockAdaptor;
import com.larkspur.stockly.Adaptors.StockCategoriesMainAdatper;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.Stock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ViewHolder _vh;
    ListView list;
    SearchListViewAdaptor _adaptor;
    SearchView editsearch;
    String[] stockNameList;

    private class ViewHolder {
        RecyclerView _techView, _financeView, _industryView, _healthView;

        DrawerLayout _drawerLayout;
        // StockAdaptor _stockAdaptor;
        RecyclerView _mostPopular;

        public ViewHolder() {
            _techView = (RecyclerView) findViewById(R.id.technology_recycle_view);
            _financeView = (RecyclerView) findViewById(R.id.finance_recycle_view);
            _industryView = (RecyclerView) findViewById(R.id.industrial_recycle_view);
            _healthView = (RecyclerView) findViewById(R.id.health_recycle_view);
//            _mostPopular = (RecyclerView) findViewById(R.id.most_popular_view);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _vh = new ViewHolder();
        //   _categories = (ListView) findViewById(R.id.categories_view);
        _vh._drawerLayout = findViewById(R.id.drawer_layout);

        //set horizontal recycler view
        //  LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        setupCategoryViews();

        //        =======================Search functionality=============================

        // Generate sample data

//        stockNameList = new String[]{"Amazon", "Apple", "Microsoft",
//                "Facebook", "Google", "Alphabet", "Tesla", "NVIDIA",
//                "Berkshire","JPMorgan","VISA"};
//
        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.searchList);

//        for (int i = 0; i < stockNameList.length; i++) {
//            StockNames stockNames = new StockNames(stockNameList[i]);
//            // Binds all strings into an array
//            arraylist.add(stockNames);
//        }
//
//        // Pass results to SearchListViewAdapter Class

        _adaptor = new SearchListViewAdaptor(this, new ArrayList<>());

        // Binds the Adapter to the ListView
        list.setAdapter(_adaptor);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

        // Set up the searchbar settings
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

//        ========================================================================

    }

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

    private void setupCategoryViews(){
        fetchStockByCategory(Category.HealthCare);
        fetchStockByCategory(Category.InformationTechnology);
        fetchStockByCategory(Category.ConsumerDiscretionary);
        fetchStockByCategory(Category.Industrials);
    }

    public void clickSearch(View view) {
//        Log.d("fail","did not register ===========================================");
        ListView listview = findViewById(R.id.searchList);
        listview.setVisibility(View.VISIBLE);
        _adaptor.printData();

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
        Log.d("pass","registered ===========================================");
        // Collapse searchList
        ListView listview = findViewById(R.id.searchList);
        listview.setVisibility(View.GONE);

        //Hide keyboard
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

        //Stop blinking in searchbar
        EditText searchEditText = (EditText) editsearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setCursorVisible(false);

//        searchEditText.setTextColor(Color.TRANSPARENT);
//        searchEditText.setHintTextColor(Color.TRANSPARENT);

        //Clear text in searchbar
        searchEditText.setText("");
    }

//    public void getData(){
//        System.out.println("===========HER=============");
//        StockHandler x = new StockHandler();
//        IStock y = x.getStock2("FedEx");
//
//    }

    public void clickMenu(View view) {
        openDrawer(_vh._drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void clickCloseSideMenu(View view) {
        closeDrawer(_vh._drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //When drawer is open, close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void clickHome(View view) {
        closeDrawer(_vh._drawerLayout);
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
        closeDrawer(_vh._drawerLayout);
    }

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

    private void fetchStockByCategory(Category category) {

        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company_v2")
                .whereEqualTo("Category", category.toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                        propogateCatAdapter(stockList, category);
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

    private void propagateAdaptor(List<IStock> data) {
        StockAdaptor stockAdapter = new StockAdaptor(this, R.layout.stock_most_viewed_recycler_view,
                data);
//        vh._mostPopular.setAdapter(stockAdapter);
//        vh.listView.setVisibility(View.VISIBLE);
    }

    private void propogateCatAdapter(List<IStock> data, Category category) {
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        StockCategoriesMainAdatper adapter = new StockCategoriesMainAdatper(data);
        switch (category) {
            case InformationTechnology:

                _vh._techView.setAdapter(adapter);
                _vh._techView.setLayoutManager(lm);
                break;
            case HealthCare:
                _vh._healthView.setAdapter(adapter);
                _vh._healthView.setLayoutManager(lm);
                break;
            case Industrials:
                _vh._industryView.setAdapter(adapter);
                _vh._industryView.setLayoutManager(lm);
                break;
            case ConsumerDiscretionary:
                _vh._financeView.setAdapter(adapter);
                _vh._financeView.setLayoutManager(lm);
                break;
            default:
                throw new IllegalArgumentException("Category not supported at the moment");
        }

    }

    public void propgateAdapter(List<IStock> data) {
        StockCategoriesMainAdatper stockCatAdatper = new StockCategoriesMainAdatper(data);
    }

//    private void propogateCategoriesAdapter(List<IStock> data, RecyclerView view){
//        StockCategoriesMainAdatper stockCatAdapter = new StockCategoriesMainAdatper(data);
//    }
}
