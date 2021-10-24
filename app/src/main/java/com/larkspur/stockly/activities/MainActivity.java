package com.larkspur.stockly.activities;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.firestore.Query;
import com.larkspur.stockly.adaptors.CategoryAdapter;
import com.larkspur.stockly.adaptors.MostViewAdapter;
import com.larkspur.stockly.adaptors.TopChangeAdapter;
import com.larkspur.stockly.data.DataFetcher;
import com.larkspur.stockly.models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the main home screen. It has a most viewed RecyclerView for "Most Viewed",
 * a list of categories to choose from and a sidebar menu.
 * Author: Takahiro, Alan, Jonathon
 */

public class MainActivity extends CoreActivity {

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
        _vh._categories.setLayoutManager(new GridLayoutManager(this, 2,GridLayoutManager.HORIZONTAL,false));

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

    }

    /**
     * GetStockMostView returns the top 10 most viewed stocks from our firebase firestore collection, and displays it onto our recycler view.
     */
    private void getStockMostView(){
        List<IStock> stockList = _dataCache.getTopNMostViewed(10);
        if (stockList.isEmpty()){

            DataFetcher.fetchStockMostView(_mostViewedStocks,_mostViewAdapater,_shimmerView);

        }else{
            _mostViewedStocks.clear();;
            _mostViewedStocks.addAll(stockList);
            _mostViewAdapater.notifyDataSetChanged();
            _shimmerView.stopShimmer();
            _shimmerView.setVisibility(View.GONE);
        }
    }

    /**
     * getGainer returns the stock that has had their value increase the most in a week, and displays it in the "top gainer" recycler view.
     */
    private void getGainer(){
        IStock stock = _dataCache.getTopGainer();
        if (stock == null){
            DataFetcher.fetchTopChange(Query.Direction.DESCENDING,_topGainerList,_topGainerAdapter,_shimmerViewGainer);
        }else{

            _topGainerList.clear();
            _topGainerList.add(stock);
            _topGainerAdapter.notifyDataSetChanged();
            _shimmerViewGainer.stopShimmer();
            _shimmerViewGainer.setVisibility(View.GONE);
        }
    }

    /**
     * getLoser returns the stock that has had their value decrease the most in a week, and displays it in the "top loser" recycler view.
     */
    private void getLoser(){
        IStock stock = _dataCache.getTopLoser();
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

    /**
     * click home is overriden from the parent method to stop redirection back to the same screen - instead the drawer just closes.
     * @param view home_button from main_nav_drawer.xml
     */
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

    /**
     * Default method for committing any user interaction with screen when the screen is
     * closed or the user switches to another screen. This allows the screen to "resume" once
     * the user returns to the screen.
     */
    @Override
    protected void onPause() {
        super.onPause();
        _shimmerView.stopShimmer();
        _shimmerViewGainer.stopShimmer();
        _shimmerViewLoser.stopShimmer();
        closeDrawer(_drawerLayout);
    }
}
