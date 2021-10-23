package com.larkspur.stockly.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larkspur.stockly.Adaptors.SearchAdapter;
import com.larkspur.stockly.Adaptors.utils.VerticalSpaceItemDecoration;
import com.larkspur.stockly.Data.DataFetcher;
import com.larkspur.stockly.Models.IStock;
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
        _adapter = new SearchAdapter(_searchContext, _searchResult);

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
        _adapter.getFilter().filter("");
    }

    public void setupSearchTextField(){
        _vh._searchView.setIconified(false);
        _vh._searchView.setFocusable(false);
    }

    public void clickBack(View view){
        redirectActivity(this,MainActivity.class);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        _adapter.notifyDataSetChanged();

        // check the size of the list every time the input is changed
        // if the size is 0, display the no search results found.

        Log.e("SearchContextSize", String.valueOf(_adapter.getContextSize()));
        Log.e("SearchResultsSize",String.valueOf(_adapter.getItemCount()));

        // if list is empty, add display a cardView saying "No search results found"
        if ((_adapter.getItemCount() == 0)) {
            // Hide recycler view.
            _vh._searchResultView.setVisibility(View.GONE);
            // Set visibility for text view to true.
            _vh._noResults.setVisibility(View.VISIBLE);
        }
        else {
            // Set visibility for recycler view to true.
            _vh._searchResultView.setVisibility(View.VISIBLE);
            // Hide the "no search results found" text.
            _vh._noResults.setVisibility(View.GONE);
        }

        _adapter.getFilter().filter(newText);
        _adapter.notifyDataSetChanged();

        return false;
    }

}