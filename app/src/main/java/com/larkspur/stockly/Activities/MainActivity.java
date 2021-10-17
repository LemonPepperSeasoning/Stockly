package com.larkspur.stockly.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.CategoryAdapter;
import com.larkspur.stockly.Adaptors.CategoryItemDecoration;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Adaptors.MostViewAdapter;
import com.larkspur.stockly.Adaptors.StockAdaptor;
import com.larkspur.stockly.Adaptors.StockCategoriesMainAdatper;
import com.larkspur.stockly.Adaptors.utils.LineChartHandler;
import com.larkspur.stockly.Data.DataFetcher;
import com.larkspur.stockly.Data.mappers.StockMapper;
import com.larkspur.stockly.Models.IHistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IUser;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.R;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.Stock;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

        TextView _usernameText, _topGainerName, _topGainerSymbol, _topGainerPrice,
                _topLoserName, _topLoserSymbol, _topLoserPrice;

        LineChart _gainerChart, _loserChart;
        View _topGainer, _topLoser;

        public ViewHolder() {
            _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            _categories = (RecyclerView) findViewById(R.id.category_recycler_view);
            _mostPopular = (RecyclerView) findViewById(R.id.most_popular_view);

            _usernameText = (TextView) findViewById(R.id.username);
            _usernameText.setText("Hi " + _user.getUsername());

            _topGainer = findViewById(R.id.top_gainer);
            _topGainerName = (TextView) _topGainer.findViewById(R.id.stock_name);
            _topGainerSymbol = (TextView) _topGainer.findViewById(R.id.stock_symbol);
            _topGainerPrice = (TextView) _topGainer.findViewById(R.id.stock_price);
            _gainerChart = (LineChart) _topGainer.findViewById(R.id.chart1);

            _topLoser = findViewById(R.id.top_loser);
            _topLoserName = (TextView) _topLoser.findViewById(R.id.stock_name);
            _topLoserSymbol = (TextView) _topLoser.findViewById(R.id.stock_symbol);
            _topLoserPrice = (TextView) _topLoser.findViewById(R.id.stock_price);
            _loserChart = (LineChart) _topLoser.findViewById(R.id.chart1);

//            _categories.setNestedScrollingEnabled(false);
        }
    }
  
    /**
     * Initialises all processes for the screen once screen is launched.
     * @param savedInstanceState default input (Any saved stock or user information)
     */
    
    private ViewHolder _vh;

    private ShimmerFrameLayout _shimmerView;
    private List<IStock> _mostViewedStocks;
    private MostViewAdapter _mostViewAdapater;

    private CategoryAdapter _categoryAdapter;
    //        =======================Search functionality=============================
    ListView list;
    ListView _searchListView;
    EditText _searchEditText;

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
        _vh._categories.addItemDecoration(new CategoryItemDecoration(40));

        LineChartHandler.setupGraph(_vh._loserChart,false, Color.BLACK);
        LineChartHandler.setupGraph(_vh._gainerChart,false,Color.BLACK);

        getStockMostView();
        getGainer();
        getLoser();


        //        =======================Search functionality=============================
        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.searchList);

        _adaptor = new SearchListViewAdaptor(this, R.layout.search_list_item, new ArrayList<>());

        // Binds the Adapter to the ListView
        list.setAdapter(_adaptor);

        // Locate SearchView
        _editSearch = (SearchView) findViewById(R.id.search);
        _editSearch.setOnQueryTextListener(this);

        // Setup the listview and EditText
        _searchListView = findViewById(R.id.searchList);
        _searchEditText = (EditText) _editSearch.findViewById(androidx.appcompat.R.id.search_src_text);

        // Set up onclick listener for close button
        // - Get the search close button image view
        ImageView closeButton = (ImageView) _editSearch.findViewById(R.id.search_close_btn);
        // - Set up on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Collapse searchList
                Log.d("closed", "button is pressed");

                _searchListView.setVisibility(View.GONE);

                //Hide keyboard
                _editSearch.clearFocus();
                _editSearch.requestFocusFromTouch();

                //Stop blinking in searchbar
                _searchEditText.setCursorVisible(false);

                //Clear text in searchbar
                _searchEditText.setText("");
            }
        });

        // Set up the searchbar settings
        _editSearch.clearFocus();
        _editSearch.requestFocusFromTouch();
        //        =======================--------------------=============================
    }


    private void getStockMostView(){
        List<IStock> stockList = _stockHandler.getTopNMostViewed(10);
        if (stockList == null){
            DataFetcher.fetchStockMostView(_mostViewedStocks,_mostViewAdapater,_shimmerView);
        }else{
            _mostViewedStocks.clear();;
            _mostViewedStocks.addAll(stockList);
            _mostViewAdapater.notifyDataSetChanged();
        }
    }

    private void getGainer(){
        IStock stock = _stockHandler.getTopGainer();
        if (stock == null){
            fetchTopChange(Query.Direction.DESCENDING);
        }else{
            setData(true,stock);
        }
    }

    private void getLoser(){
        IStock stock = _stockHandler.getTopLoser();
        if (stock == null){
            fetchTopChange(Query.Direction.ASCENDING);
        }else{
            setData(false,stock);
        }
    }


    //TODO : Add base adapter to the top gainer/loser component and move this to DataFetcher class.
    private void fetchTopChange(Query.Direction direction) {
        final IStock[] stock = new IStock[1];

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .orderBy("Name", direction) // TODO : Add % change to DB and query.
                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        stock[0] = StockMapper.toStock(document.getData());
                    }

                    if (stock[0] != null) {
                        if (direction.equals(Query.Direction.DESCENDING)){
                            setData(true,stock[0]);
                            _stockHandler.addTopGainer(stock[0]);
                        }else if(direction.equals(Query.Direction.ASCENDING)){
                            setData(false,stock[0]);
                            _stockHandler.addTopLoser(stock[0]);
                        }else{
                            Log.d("Fetch Failed", "return value was empty");
                        }
                    } else {
                        Log.d("Fetch Failed", "return value was empty");
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch stocks by category");
                }
            }
        });
    }

    public void setData(Boolean isGainer, IStock stock){
        View view;
        if (isGainer){
            _vh._topGainerName.setText(stock.getCompName());
            _vh._topGainerSymbol.setText(stock.getSymbol());
            _vh._topGainerPrice.setText("$" + String.format("%.2f", stock.getPrice()) + " "
                    + String.format("%.2f", stock.getHistoricalPrice().getLast24HourChange()) + "%");
            LineChartHandler.setData(stock.getHistoricalPrice(), _vh._gainerChart);
            view = _vh._topGainer;
        }else{
            _vh._topLoserName.setText(stock.getCompName());
            _vh._topLoserSymbol.setText(stock.getSymbol());
            _vh._topLoserPrice.setText("$" + String.format("%.2f", stock.getPrice()) + " "
                    + String.format("%.2f", stock.getHistoricalPrice().getLast24HourChange()) + "%");
            LineChartHandler.setData(stock.getHistoricalPrice(), _vh._loserChart);
            view = _vh._topLoser;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StockActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("stock", stock);
                Log.e("stock",stock.getCompName());
                intent.putExtra("Screen", "Home");
                intent.putExtra("Class", MainActivity.class);
                IStock test = (IStock) bundle.getSerializable("stock");
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
                Toast.makeText(getBaseContext(), stock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void clickHome(View view) {
        closeDrawer(_drawerLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _shimmerView.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _shimmerView.stopShimmer();
        closeDrawer(_drawerLayout);
    }




}
