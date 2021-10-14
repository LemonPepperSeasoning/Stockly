package com.larkspur.stockly.Activities.Search;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.larkspur.stockly.Activities.HelpActivity;
import com.larkspur.stockly.Activities.MainActivity;
import com.larkspur.stockly.Activities.PortfolioActivity;
import com.larkspur.stockly.Activities.SettingsActivity;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.R;

import java.util.ArrayList;

/**
 * This is a mockup of the search functionality. Once this is completed, the search bar will
 * be implemented on every screen with an interface for all the methods in this class.
 * Author: Jonathon
 * Source: https://abhiandroid.com/ui/searchview
 */

public class SearchTemplateActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    DrawerLayout _drawerLayout;
    ListView list;
    SearchListViewAdaptor adaptor;
    SearchView editsearch;
    String[] stockNameList;
    ArrayList<StockNames> arraylist = new ArrayList<StockNames>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_template);

        _drawerLayout = findViewById(R.id.drawer_layout);

//        =======================Search functionality=============================

        // Generate sample data

        stockNameList = new String[]{"Amazon", "Apple", "Microsoft",
                "Facebook", "Google", "Alphabet", "Tesla", "NVIDIA",
                "Berkshire","JPMorgan","VISA"};

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.listview);

        for (int i = 0; i < stockNameList.length; i++) {
            StockNames stockNames = new StockNames(stockNameList[i]);
            // Binds all strings into an array
            arraylist.add(stockNames);
        }

        // Pass results to SearchListViewAdapter Class
        adaptor = new SearchListViewAdaptor(this, arraylist);

        // Binds the Adapter to the ListView
        list.setAdapter(adaptor);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);


//        ========================================================================
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adaptor.filter(text);
        return false;
    }


    public void clickMenu(View view){
        MainActivity.openDrawer(_drawerLayout);
    }

    public void clickCloseSideMenu(View view){
        MainActivity.closeDrawer(_drawerLayout);
    }

    public void clickHome(View view){
        MainActivity.redirectActivity(this,MainActivity.class);
    }

    public void clickPortfolio(View view){
        MainActivity.redirectActivity(this, PortfolioActivity.class);
    }

    public void clickWatchlist(View view){
        recreate();
    }

    public void clickSettings(View view){
        MainActivity.redirectActivity(this, SettingsActivity.class);
    }

    public void clickHelp(View view) {
        MainActivity.redirectActivity(this, HelpActivity.class);
    }

    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.closeDrawer(_drawerLayout);
    }

}
