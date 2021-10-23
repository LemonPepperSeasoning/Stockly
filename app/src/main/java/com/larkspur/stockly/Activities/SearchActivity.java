package com.larkspur.stockly.Activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.larkspur.stockly.Adaptors.SearchAdapter;
import com.larkspur.stockly.Data.DataFetcher;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends CoreActivity implements SearchView.OnQueryTextListener {

    private class ViewHolder {
        RecyclerView _searchResultView;
        SearchView _searchView;

        public ViewHolder() {
            _searchResultView = (RecyclerView) findViewById(R.id.searchResult);
            _searchView = (SearchView) findViewById(R.id.searchBar);
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

        DataFetcher.fetchAllStocks(_searchContext, _searchResult, _adapter);
        _adapter.getFilter().filter("");
    }

    public void setupSearchTextField(){
        _vh._searchView.setIconified(false);
        _vh._searchView.setFocusable(false);
    }

    public void clickBack(View view){
        redirectActivity(this,MainActivity.class);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        _adapter.getFilter().filter(newText);
        return false;
    }

}