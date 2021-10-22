package com.larkspur.stockly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.larkspur.stockly.Adaptors.BasicStockAdapter;
import com.larkspur.stockly.Adaptors.ListViewAdapter;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Data.DataFetcher;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;

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
    private BasicStockAdapter _listAdapter;
    private List<IStock> _categoryStocks;
    ListView list;

    /**
     * Initialises all processes for the screen once screen is launched.
     *
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

            _categoryStocks = new ArrayList<>();
            _listAdapter = new BasicStockAdapter(this, R.layout.list_item, _categoryStocks);
            _vh._listView.setAdapter(_listAdapter);
            _vh._listView.setVisibility(View.VISIBLE);

            getCategoryStocks(category);
        } else {
            throw new RuntimeException("Stock not found!");
        }

    }

    /**
     * Makes a query to Firestore database for stock information on one thread while
     * another thread executes the java functions (creating stock items using onComplete
     * function). Stock items are created and put inside a list for use. All stock items are
     * called for one specific category.
     */

    public void getCategoryStocks(Category category) {
        List<IStock> stockList = _dataCache.getCategoryStock(category);
        if (stockList == null) {
            DataFetcher.fetchCategoryStocks(category, _categoryStocks, _listAdapter);
        } else {
            _categoryStocks.clear();
            _categoryStocks.addAll(stockList);
            _listAdapter.notifyDataSetChanged();
            ;
        }
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
     *
     * @param view TextView
     */
    public void clickReturn(View view) {
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        if (activity == DetailsActivity.class) {
            Bundle bundle = intent.getExtras();
            intent.putExtras(bundle);
            redirectActivity(this, activity, bundle);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } else {
            redirectActivity(this, MainActivity.class);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

}
