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
    private Typeface tfLight;


    private ShimmerFrameLayout mShimmerViewContainer;
    private List<IStock> _mostViewedStocks;
    private MostViewAdapter _mostViewAdapater;
    //        =======================Search functionality=============================
    ListView list;
    //        =======================--------------------=============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _vh = new ViewHolder();

        this.setTitle("Home");

        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        _vh._mostPopular.setItemAnimator(new DefaultItemAnimator());
        _mostViewedStocks = new ArrayList<>();
        _mostViewAdapater = new MostViewAdapter(_mostViewedStocks);
        _vh._mostPopular.setAdapter(_mostViewAdapater);
        _vh._mostPopular.setLayoutManager(lm);
//        shimmerContainer.startShimmer();

        setupGraph(_vh._loserChart);
        setupGraph(_vh._gainerChart);

        getStockMostView();
        propogateCategoryAdapter();

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
        if (stockList == null){
            fetchStockMostView();
        }else{
            propogateMostViewAdapter(stockList);
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
            setData(stock.getHistoricalPrice(), _vh._gainerChart);
            view = _vh._topGainer;
        }else{
            _vh._topLoserName.setText(stock.getCompName());
            _vh._topLoserSymbol.setText(stock.getSymbol());
            _vh._topLoserPrice.setText("$" + String.format("%.2f", stock.getPrice()) + " "
                    + String.format("%.2f", stock.getHistoricalPrice().getLast24HourChange()) + "%");
            setData(stock.getHistoricalPrice(), _vh._loserChart);
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
        mShimmerViewContainer.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmer();
        closeDrawer(_drawerLayout);
    }

//    private void fetchStockByCategory(Category category) {
//        List<IStock> stockList = new LinkedList<>();
//
//        // Getting numbers collection from Firestore
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("company")
//                .whereEqualTo("Category", category.toString())
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        stockList.add(StockMapper.toStock(document.getData()));
//                    }
//
//                    if (stockList.size() > 0) {
//                        propogateCatAdapter(stockList, category);
//                        _stockHandler.addCategoryStock(category,stockList);
//                    } else {
//                        Log.d("Fetch Failed", "return value was empty");
//                    }
//                } else {
//                    Log.e("Fetch Error", "failed to fetch stocks by category");
//                }
//            }
//        });
//    }
//

    /**
     *  Makes a query to Firestore database for stock information on one thread while
     *  another thread executes the java functions (creating stock items using onComplete
     *  function). Stock items are created and put inside a list for use. All stock items are
     *  called in order
     */
    private void fetchStockMostView(){
        List<IStock> stockList = new LinkedList<>();
        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("viewcount")
                .orderBy("viewcount", Query.Direction.DESCENDING)
                .limit(10)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();

                        DocumentReference ref = (DocumentReference)data.get("company");
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    IStock stock = StockMapper.toStock(task.getResult().getData());
                                    _mostViewedStocks.add(stock);
                                    _stockHandler.addMostViewStock(stock);
                                    _mostViewAdapater.notifyDataSetChanged();

                                    mShimmerViewContainer.stopShimmer();
                                    mShimmerViewContainer.setVisibility(View.GONE);
                                } else {
                                    Log.e("Fetch Error", "failed to fetch stocks by mostView's reference");
                                }
                            }
                        });
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch most viewed");
                }
            }
        });
    }
    private void propogateCategoryAdapter(){
        CategoryAdapter adapter = new CategoryAdapter();
        _vh._categories.setAdapter(adapter);
//        _vh._categories.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        _vh._categories.setLayoutManager(new GridLayoutManager(this, 2));
        _vh._categories.addItemDecoration(new CategoryItemDecoration(40));
    }

    /**
     * Creates adaptor for ListViews which displays stocks in the RecyclerView for most viewed.
     * @param data Stock information list
     */
    private void propogateMostViewAdapter(List<IStock> data){

    }


//    private void propogateCatAdapter(List<IStock> data, Category category) {
//        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
//        StockCategoriesMainAdatper adapter = new StockCategoriesMainAdatper(data);
//        switch (category) {
//            case InformationTechnology:
//                _vh._techView.setAdapter(adapter);
//                _vh._techView.setLayoutManager(lm);
//                break;
//            case HealthCare:
//                _vh._healthView.setAdapter(adapter);
//                _vh._healthView.setLayoutManager(lm);
//                break;
//            case Industrials:
//                _vh._industryView.setAdapter(adapter);
//                _vh._industryView.setLayoutManager(lm);
//                break;
//            case ConsumerDiscretionary:
//                _vh._financeView.setAdapter(adapter);
//                _vh._financeView.setLayoutManager(lm);
//                break;
//            default:
//                throw new IllegalArgumentException("Category not supported at the moment");
//        }
//    }


    /**
     * This method browses every category.
     * @param view
     */
    public void browseAll(View view){
        System.out.println(view.getResources().getResourceName(view.getId()));
        System.out.println(view.getTag());
        String categoryView = view.getTag().toString();
        Intent intent = new Intent(view.getContext(),ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Screen", "Home");
        intent.putExtra("Class",this.getClass());
        switch (categoryView){
            case "technology":
                intent.putExtra("Category","Information Technology");
                view.getContext().startActivity(intent);
                break;
            case "finance":
                intent.putExtra("Category","Consumer Discretionary");
                view.getContext().startActivity(intent);
                break;
            case "industrials":
                intent.putExtra("Category","Industrials");
                view.getContext().startActivity(intent);
                break;
            case "health care":
                intent.putExtra("Category","Health Care");
                view.getContext().startActivity(intent);
                    break;
            default:
                throw new IllegalArgumentException("category not found");
        }
    }

    private void setupGraph(LineChart chart) {
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setBackgroundColor(Color.BLACK);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);

        XAxis x = chart.getXAxis();
        x.setEnabled(false);

        YAxis y = chart.getAxisLeft();
//        y.setTypeface(tfLight);
//        y.setLabelCount(6, false);
//        y.setTextColor(Color.WHITE);
//        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
//        y.setAxisLineColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);

        // don't forget to refresh the drawing
        chart.invalidate();
    }

    private void setData(@NonNull IHistoricalPrice prices, LineChart chart) {
        ArrayList<Entry> values = new ArrayList<>();

        int index = 0;
        for (Double i : prices.getHistoricalPrice()) {
            values.add(new Entry(index, i.floatValue()));
            index++;
        }
        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.rgb(159, 125, 225));
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.rgb(159, 125, 225));
            set1.setFillColor(Color.rgb(159, 125, 225));
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
            data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
            chart.notifyDataSetChanged();
        }
    }
}
