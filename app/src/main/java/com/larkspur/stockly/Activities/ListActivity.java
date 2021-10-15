package com.larkspur.stockly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.UserInfo;
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

    private ViewHolder _vh;
    ListView list;
    String[] stockNameList;
    private UserInfo _userInfo;

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

    /**
     * Initialises all processes for the screen once screen is launched.
     * @param savedInstanceState default input
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
            fetchCategoryStocks(category);
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
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

        // Set up the searchbar settings
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

        //        =======================--------------------=============================
    }

    /**
     * Makes a query to Firestore database for stock information on one thread while
     * another thread executes the java functions (creating stock items using onComplete
     * function). Stock items are created and put inside a list for use. All stock items are
     * called for one specific category.
     */
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
                                ((String) data.get("Description")),
                                ((List<String>) data.get("ImageLink")),
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

                        propogateCatAdapter(stockList);
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
