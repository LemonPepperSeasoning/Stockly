package com.larkspur.stockly.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.security.identity.DocTypeNotSupportedException;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.MostViewAdapter;
import com.larkspur.stockly.Adaptors.StockAdaptor;
import com.larkspur.stockly.Adaptors.StockCategoriesMainAdatper;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;
import com.larkspur.stockly.Data.StockHandler;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewHolder _vh;

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
            _mostPopular = (RecyclerView) findViewById(R.id.most_popular_view);
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
        fetchStockMostView();

        setupCategoryViews();
    }

    private void setupCategoryViews(){
        fetchStockByCategory(Category.HealthCare);
        fetchStockByCategory(Category.InformationTechnology);
        fetchStockByCategory(Category.ConsumerDiscretionary);
        fetchStockByCategory(Category.Industrials);
    }

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

//                    System.out.println("============================");
//                    System.out.println(stockList.size());
//                    for (IStock i : stockList) {
//                        Log.d("== Stock : ", i.getCompName() + " " + i.getCategory() + " " + i.getSymbol() + " == ");
//                    }
//                    System.out.println("============================");

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

    private void fetchStockMostView(){
        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        List<DocumentReference> stockRef = new ArrayList<>();
        db.collection("viewcount")
                .orderBy("viewcount", Query.Direction.DESCENDING)
                .limit(10)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot results = task.getResult();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("----------", document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();

                        stockRef.add((DocumentReference) data.get("company"));
                        DocumentReference ref = (DocumentReference)data.get("company");
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot results = task.getResult();
                                    Log.d("============", results.getId() + " => " + results.getData());
                                    Map<String, Object> data = results.getData();

                                    HistoricalPrice tmpHistoricalPrice = new HistoricalPrice((List<Double>) data.get("Price"));
                                    IStock tmpStock = new Stock(
                                            ((String) data.get("Name")),
                                            ((String) data.get("Symbol")),
                                            (Category.getValue((String) data.get("Category"))),
                                            ((String) data.get("Subindustry")),
                                            ((String) data.get("location")),
                                            tmpHistoricalPrice);
                                    stockList.add(tmpStock);
                                    Log.i("?????", String.valueOf(stockList.size()) );

                                    if(stockList.size() == 10){
                                        propogateMostViewAdapter(stockList);
                                    }

                                } else {
//                    Toast.makeText(getBaseContext(), "Loading colors collection failed from Firestore!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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

    private void propogateMostViewAdapter(List<IStock> data){
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        MostViewAdapter adapter = new MostViewAdapter(data);
        _vh._mostPopular.setAdapter(adapter);
        _vh._mostPopular.setLayoutManager(lm);
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
