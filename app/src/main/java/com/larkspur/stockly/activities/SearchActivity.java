package com.larkspur.stockly.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larkspur.stockly.adaptors.SearchAdapter;
import com.larkspur.stockly.adaptors.utils.VerticalSpaceItemDecoration;
import com.larkspur.stockly.data.DataFetcher;
import com.larkspur.stockly.models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends CoreActivity implements SearchView.OnQueryTextListener {

    private class ViewHolder {
        RecyclerView _searchResultView;
        SearchView _searchView;
        EditText _searchText;
        TextView _noResults;

        public ViewHolder() {
            _searchResultView = (RecyclerView) findViewById(R.id.searchResult);
            _searchView = (SearchView) findViewById(R.id.searchBar);

            // View for text for search user input
            _searchText = (EditText) findViewById(androidx
                    .appcompat.R.id.search_src_text);

            _noResults = (TextView) findViewById(R.id.no_results);
        }
    }

    private ViewHolder _vh;
    SearchAdapter _adapter;
    List<IStock> _searchContext;
    List<IStock> _searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        _vh = new ViewHolder();
        setupSearchTextField();

        _searchContext = new ArrayList<>();
        _searchResult = new ArrayList<>();
        _adapter = new SearchAdapter(_searchContext, _searchResult, this);

        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        _vh._searchResultView.setAdapter(_adapter);
        _vh._searchResultView.setLayoutManager(lm);
        _vh._searchView.setOnQueryTextListener(this);

        _vh._searchResultView.addItemDecoration(new VerticalSpaceItemDecoration(15));
        
        // Set text colour to white for search user input
        _vh._searchText.setTextColor(Color.WHITE);

        // Hide the "no search results found" text.
        _vh._noResults.setVisibility(View.GONE);

        DataFetcher.fetchAllStocks(_searchContext, _searchResult, _adapter);

    }

    /**
     *
     */
    public void setupSearchTextField(){
        _vh._searchView.setIconified(false);
        _vh._searchView.setFocusable(false);
    }
    /**
     *Allows for search screen to return back to the main page.
     */
    public void clickBack(View view){
        redirectActivity(this,MainActivity.class);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * OnQueryTextSubmit is when the user presses "enter" on the keyboard
     * @param query - the string the user enters in the search bar
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * As text is entered in the search bar, this method is being called, and filters the list of stocks dynamically as the user is typing, when there is no input - display all stocks, else we filter.
     * @param newText - the string the user enters in the search bar
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        _adapter.notifyDataSetChanged();

        if(newText.equals("")){
            _searchResult.clear();
            _searchResult.addAll(_searchContext);
        }else{
            _adapter.getFilter().filter(newText);
            _adapter.notifyDataSetChanged();
        }

        return false;
    }

}