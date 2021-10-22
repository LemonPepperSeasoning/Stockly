package com.larkspur.stockly.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.MostViewAdapter;
import com.larkspur.stockly.Adaptors.SearchAdapter;
import com.larkspur.stockly.Data.DataFetcher;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
//        _vh._searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String text) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String text) {
//                _adapter.getFilter().filter(text);
//                return true;
//            }
//        });
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