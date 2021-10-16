package com.larkspur.stockly.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Data.StockHandler;
import com.larkspur.stockly.Data.mappers.StockMapper;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IUser;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class CoreActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    protected DrawerLayout _drawerLayout;
    protected SearchListViewAdaptor _adaptor;
    protected SearchView _editSearch;
    protected IUser _user;
    protected StockHandler _stockHandler;

    public CoreActivity(){
        _stockHandler = StockHandler.getInstance();
        _user = User.getInstance();
    }

    public void clickMenu(View view) {
        openDrawer(_drawerLayout);
    }

    public void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void clickCloseSideMenu(View view) {
        closeDrawer(_drawerLayout);
    }

    public void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //When drawer is open, close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void clickHome(View view){
       redirectActivity(this,MainActivity.class);
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

    public void redirectActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        String screenName = activity.getTitle().toString();
        System.out.println("this is the screen "+ screenName);
        intent.putExtra("Screen", screenName);
        intent.putExtra("Class",activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void redirectActivity(Activity activity, Class aClass, Bundle stock){
        Intent intent = new Intent(activity, aClass);
        String screenName = activity.getTitle().toString();
        intent.putExtra("Screen", screenName);
        intent.putExtra("Class",activity.getClass());
        intent.putExtras(stock);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
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
        _editSearch.setFocusable(true);
        _editSearch.setIconified(false);
        _editSearch.requestFocusFromTouch();

        // Show text
        EditText searchEditText = (EditText) _editSearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setCursorVisible(true);

        // Fetch the stock data for suggestions
        fetchAllStocks();
    }

    public void closeSearch(View view) {
        // Collapse searchList
        ListView listview = findViewById(R.id.searchList);
        listview.setVisibility(View.GONE);

        //Hide keyboard
        _editSearch.clearFocus();
        _editSearch.requestFocusFromTouch();

        //Stop blinking in searchbar
        EditText searchEditText = (EditText) _editSearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setCursorVisible(false);

        //Clear text in searchbar
        searchEditText.setText("");
    }

    private void fetchAllStocks() {
        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        stockList.add(StockMapper.toStock(document.getData()));
                    }

                    if (stockList.size() > 0) {
                        _adaptor.addData(stockList);
                        _stockHandler.addAllStock(stockList);
                    } else {
                        Log.d("Fetch Failed", "return value was empty");
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch all stocks");
                }
            }
        });
    }
}
