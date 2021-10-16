package com.larkspur.stockly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.ListViewAdapter;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Data.mappers.StockMapper;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class handles the screen for the list of items in each category, once directed
 * from the main screen.
 * Author: Jonathon
 */
public class ListActivity extends CoreActivity implements SearchView.OnQueryTextListener {

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        ListView _listView;
        TextView _categoryText, _previousScreen;
        LinearLayout _return;

        public ViewHolder() {
            _drawerLayout = findViewById(R.id.drawer_layout);
            _listView = findViewById(R.id.list_view);
            _categoryText = findViewById(R.id.category_text_view);
            _return = findViewById(R.id.return_view);
            _previousScreen = findViewById(R.id.previous_screen_text_view);
        }
    }

    private ViewHolder _vh;
    ListView list;

    /**
     * Initialises all processes for the screen once screen is launched.
     * @param savedInstanceState default input (Any saved stock or user information)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        _vh = new ViewHolder();
        if (getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            System.out.println(intent.getExtras().getSerializable("Class"));
            String previousScreen = intent.getStringExtra("Screen");
            _vh._previousScreen.setText("Return to " + previousScreen);
            String stringCategory = intent.getStringExtra("Category");
            _vh._categoryText.setText(stringCategory);
            Category category = Category.getValue(stringCategory);
            getCategoryStocks(category);
        } else {
            throw new RuntimeException("Stock not found!");
        }

        //        =======================Search functionality=============================

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.searchList);
        _adaptor = new SearchListViewAdaptor(this, R.layout.search_list_item, new ArrayList<>());

        // Binds the Adapter to the ListView
        list.setAdapter(_adaptor);

        // Locate the EditText in listview_main.xml
        _editSearch = (SearchView) findViewById(R.id.search);
        _editSearch.setOnQueryTextListener(this);

        // Set up the searchbar settings
        _editSearch.clearFocus();
        _editSearch.requestFocusFromTouch();
        //        =======================--------------------=============================
    }

    /**
     * Makes a query to Firestore database for stock information on one thread while
     * another thread executes the java functions (creating stock items using onComplete
     * function). Stock items are created and put inside a list for use. All stock items are
     * called for one specific category.
     */

    public void getCategoryStocks(Category category){
        List<IStock> stockList= _stockHandler.getCategoryStock(category);
        if (stockList == null){
            fetchCategoryStocks(category);
        }else{
            propogateCatAdapter(stockList);
        }
    }
   
    private void fetchCategoryStocks(Category category){
        List<IStock> stockList = new LinkedList<>();
        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company_v2")
                .whereEqualTo("Category", category.toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("+++++", document.getId() + " => " + document.getData());
                        stockList.add(StockMapper.toStock(document.getData()));
                    }
                    if (stockList.size() > 0) {
                        propogateCatAdapter(stockList);
                        _stockHandler.addCategoryStock(category,stockList);
                    } else {
                        Log.d("Fetch Failed", "return value was empty");
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch stocks by category");
                }
            }
        });
    }

    /**
     * Creates adaptor for the list of items and makes them visible using the adaptor
     * @param data Stock information list
     */
    private void propogateCatAdapter(List<IStock> data) {
        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.list_item, data);
        _vh._listView.setAdapter(adapter);
        _vh._listView.setVisibility(View.VISIBLE);
    }

    /**
     * Default method for committing any user interaction with screen when the screen is
     * closed or the user switches to another screen. This allows the screen to "resume" once
     * the user returns to the screen.
     */
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(_drawerLayout);
    }

    /**
     * Handles click functionality for return text
     * @param view TextView
     */
    public void clickReturn(View view) {
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        redirectActivity(this, activity);
    }

}
