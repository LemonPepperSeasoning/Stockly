package com.larkspur.stockly.Activities;

import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.firestore.Query;
import com.larkspur.stockly.Adaptors.CategoryAdapter;
import com.larkspur.stockly.Adaptors.CategoryItemDecoration;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Adaptors.MostViewAdapter;
import com.larkspur.stockly.Adaptors.TopChangeAdapter;
import com.larkspur.stockly.Data.DataFetcher;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the main home screen. It has a most viewed RecyclerView for "Most Viewed",
 * a list of categories to choose from and a sidebar menu.
 * Author: Takahiro, Alan, Jonathon
 */

public class MainActivity extends CoreActivity implements SearchView.OnQueryTextListener {

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        RecyclerView _mostPopular;
        RecyclerView _categories;
        RecyclerView _topGainer, _topLoser;

        TextView _usernameText;

        LineChart _gainerChart, _loserChart;

        public ViewHolder() {
            _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            _categories = (RecyclerView) findViewById(R.id.category_recycler_view);
            _mostPopular = (RecyclerView) findViewById(R.id.most_popular_view);

            _usernameText = (TextView) findViewById(R.id.username);
            _usernameText.setText("Hi " + _user.getUsername());

            _topGainer = findViewById(R.id.top_gainer);
            _topLoser = findViewById(R.id.top_loser);
        }
    }
  
    /**
     * Initialises all processes for the screen once screen is launched.
     * @param savedInstanceState default input (Any saved stock or user information)
     */
    
    private ViewHolder _vh;

    private ShimmerFrameLayout _shimmerView;
    private ShimmerFrameLayout _shimmerViewGainer;
    private ShimmerFrameLayout _shimmerViewLoser;

    private List<IStock> _mostViewedStocks;
    private MostViewAdapter _mostViewAdapater;

    private CategoryAdapter _categoryAdapter;
    private TopChangeAdapter _topGainerAdapter;
    private TopChangeAdapter _topLoserAdapter;
    private List<IStock> _topGainerList;
    private List<IStock> _topLoserList;

    //        =======================Search functionality=============================
    ListView list;
    //        =======================--------------------=============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _vh = new ViewHolder();

        this.setTitle("Home");


        _shimmerView = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        _vh._mostPopular.setItemAnimator(new DefaultItemAnimator());
        _mostViewedStocks = new ArrayList<>();
        _mostViewAdapater = new MostViewAdapter(_mostViewedStocks);
        _vh._mostPopular.setAdapter(_mostViewAdapater);
        _vh._mostPopular.setLayoutManager(lm);

        _categoryAdapter = new CategoryAdapter();
        _vh._categories.setAdapter(_categoryAdapter);
//        _vh._categories.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        _vh._categories.setLayoutManager(new GridLayoutManager(this, 2));
//        _vh._categories.setLayoutManager(new GridLayoutManager(this, 2,GridLayoutManager.HORIZONTAL,false));
        _vh._categories.addItemDecoration(new CategoryItemDecoration(40));

        _shimmerViewGainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_gainer);
        _shimmerViewLoser = (ShimmerFrameLayout) findViewById(R.id.shimmer_loser);

        _topGainerList = new ArrayList<>();
        _topGainerAdapter = new TopChangeAdapter(_topGainerList);
        _vh._topGainer.setAdapter(_topGainerAdapter);
        _vh._topGainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        _topLoserList = new ArrayList<>();
        _topLoserAdapter = new TopChangeAdapter(_topLoserList);
        _vh._topLoser.setAdapter(_topLoserAdapter);
        _vh._topLoser.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        getStockMostView();

        getGainer();
        getLoser();


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


    private void getStockMostView(){
        List<IStock> stockList = _stockHandler.getTopNMostViewed(10);
        if (stockList.isEmpty()){
            Log.e("new data", "HERE============");

            DataFetcher.fetchStockMostView(_mostViewedStocks,_mostViewAdapater,_shimmerView);
        }else{
            Log.e("Prefetched data", "HERE============");
            _mostViewedStocks.clear();;
            _mostViewedStocks.addAll(stockList);
            _mostViewAdapater.notifyDataSetChanged();
            _shimmerView.stopShimmer();
            _shimmerView.setVisibility(View.GONE);
        }
    }

    private void getGainer(){
        IStock stock = _stockHandler.getTopGainer();
        if (stock == null){
            DataFetcher.fetchTopChange(Query.Direction.DESCENDING,_topGainerList,_topGainerAdapter,_shimmerViewGainer);
        }else{
            Log.e("Prefetched data", "HERE============");

            _topGainerList.clear();
            _topGainerList.add(stock);
            _topGainerAdapter.notifyDataSetChanged();
            _shimmerViewGainer.stopShimmer();
            _shimmerViewGainer.setVisibility(View.GONE);
        }
    }

    private void getLoser(){
        IStock stock = _stockHandler.getTopLoser();
        if (stock == null){
            DataFetcher.fetchTopChange(Query.Direction.ASCENDING,_topLoserList,_topLoserAdapter,_shimmerViewLoser);
        }else{
            _topLoserList.clear();
            _topLoserList.add(stock);
            _topLoserAdapter.notifyDataSetChanged();
            _shimmerViewLoser.stopShimmer();
            _shimmerViewLoser.setVisibility(View.GONE);
        }
    }

    @Override
    public void clickHome(View view) {
        closeDrawer(_drawerLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _shimmerView.startShimmer();
        _shimmerViewGainer.startShimmer();
        _shimmerViewLoser.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _shimmerView.stopShimmer();
        _shimmerViewGainer.stopShimmer();
        _shimmerViewLoser.stopShimmer();
        closeDrawer(_drawerLayout);
    }
}
