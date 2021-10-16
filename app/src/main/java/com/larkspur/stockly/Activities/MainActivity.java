package com.larkspur.stockly.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Adaptors.MostViewAdapter;
import com.larkspur.stockly.Adaptors.StockAdaptor;
import com.larkspur.stockly.Adaptors.StockCategoriesMainAdatper;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.UserInfo;
import com.larkspur.stockly.R;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.Stock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class handles the main home screen. It has a most viewed RecyclerView for "Most Viewed",
 * a list of categories to choose from and a sidebar menu.
 * Author: Takahiro, Alan, Jonathon
 */

public class MainActivity extends CoreActivity implements SearchView.OnQueryTextListener {

    private ViewHolder _vh;
    ListView list;
    String[] stockNameList;
    private UserInfo _userInfo;

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        RecyclerView _techView, _financeView, _industryView, _healthView;
        RecyclerView _mostPopular;
        TextView _usernameText;

        public ViewHolder() {
            _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            _techView = (RecyclerView) findViewById(R.id.technology_recycle_view);
            _financeView = (RecyclerView) findViewById(R.id.finance_recycle_view);
            _industryView = (RecyclerView) findViewById(R.id.industrial_recycle_view);
            _healthView = (RecyclerView) findViewById(R.id.health_recycle_view);
            _mostPopular = (RecyclerView) findViewById(R.id.most_popular_view);
            _usernameText = (TextView) findViewById(R.id.username);
        }
    }

    /**
     * Initialises all processes for the screen once screen is launched.
     * @param savedInstanceState default input (Any saved stock or user information)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _vh = new ViewHolder();
        this.setTitle("Home");
        _userInfo = UserInfo.getInstance();
        _vh._usernameText.setText("Hi " + _userInfo.getUsername());

        //set horizontal recycler view
        //  LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        fetchStockMostView();

        setupCategoryViews();

        //        =======================Search functionality=============================

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.searchList);

        _adaptor = new SearchListViewAdaptor(this, R.layout.search_list_item, new ArrayList<>());

        // Binds the Adapter to the ListView
        list.setAdapter(_adaptor);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

        // Set up the searchbar settings
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

        //        =======================--------------------=============================
    }

    /**
     * Initialises all categories
     */
    private void setupCategoryViews(){
        fetchStockByCategory(Category.HealthCare);
        fetchStockByCategory(Category.InformationTechnology);
        fetchStockByCategory(Category.ConsumerDiscretionary);
        fetchStockByCategory(Category.Industrials);
    }

    /**
     * Click functionality for opening main page from side menu
     * @param view home_button from main_nav_drawer.xml
     */
    @Override
    public void clickHome(View view) {
        closeDrawer(_drawerLayout);
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
     *  Makes a query to Firestore database for stock information on one thread while
     *  another thread executes the java functions (creating stock items using onComplete
     *  function). Stock items are created and put inside a list for use. All stock items are
     *  called for the specified category.
     * @param category Category options in the main screen.
     */
    private void fetchStockByCategory(Category category) {
        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .whereEqualTo("Category", category.toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot results = task.getResult();
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("+++++", document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();
                        HistoricalPrice tmpHistoricalPrice = new HistoricalPrice((List<Double>) data.get("Price"));
                        IStock tmpStock = new Stock(
                                ((String) data.get("Name")),
                                ((String) data.get("Symbol")),
                                (Category.getValue((String) data.get("Category"))),
                                ((String) data.get("Subindustry")),
                                ((String) data.get("location")),
                                ((String) data.get("Description")),
                                ((List<String>) data.get("ImageLink")),
                                tmpHistoricalPrice);
                        stockList.add(tmpStock);
                    }

//                    System.out.println("============================");
//                    System.out.println(stockList.size());
//                    for (IStock i : stockList) {
//                        Log.d("== Stock : ", i.getCompName() + " " + i.getCategory() + " " + i.getSymbol() + " == ");
//                    }
//                    System.out.println("============================");

                    if (stockList.size() > 0) {
                        Log.i("Getting colors", "Success");

                        propogateCatAdapter(stockList, category);
                        // Once the task is successful and data is fetched, propagate the adaptor
                        //  propagateAdaptor(stockList);

                        // Hide the ProgressBar
//                        vh.progressBar.setVisibility(View.GONE);
                    } else {
//                        Toast.makeText(getBaseContext(), "Colors Collection was empty!", Toast.LENGTH_LONG).show();
                    }
                } else {
//                    Toast.makeText(getBaseContext(), "Loading colors collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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

        List<DocumentReference> stockRef = new ArrayList<>();
        db.collection("viewcount")
                .orderBy("viewcount", Query.Direction.DESCENDING)
                .limit(10)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot results = task.getResult();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("----------", document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();

                        stockRef.add((DocumentReference) data.get("company"));
                        DocumentReference ref = (DocumentReference)data.get("company");
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot results = task.getResult();
                                    Log.d("============", results.getId() + " => " + results.getData());
                                    Map<String, Object> data = results.getData();

                                    HistoricalPrice tmpHistoricalPrice = new HistoricalPrice((List<Double>) data.get("Price"));
                                    IStock tmpStock = new Stock(
                                            ((String) data.get("Name")),
                                            ((String) data.get("Symbol")),
                                            (Category.getValue((String) data.get("Category"))),
                                            ((String) data.get("Subindustry")),
                                            ((String) data.get("location")),
                                            ((String) data.get("Description")),
                                            ((List<String>) data.get("ImageLink")),
                                            tmpHistoricalPrice);
                                    stockList.add(tmpStock);
                                    Log.i("?????", String.valueOf(stockList.size()) );

                                    if(stockList.size() == 10){
                                        propogateMostViewAdapter(stockList);
                                    }

                                } else {
//                    Toast.makeText(getBaseContext(), "Loading colors collection failed from Firestore!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                } else {
//                    Toast.makeText(getBaseContext(), "Loading colors collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Creates adaptor for ListViews which displays stocks in the RecyclerView.
     * @param data Stock information list
     */
    private void propagateAdaptor(List<IStock> data) {
        StockAdaptor stockAdapter = new StockAdaptor(this, R.layout.stock_most_viewed_recycler_view,
                data);
//        vh._mostPopular.setAdapter(stockAdapter);
//        vh.listView.setVisibility(View.VISIBLE);
    }

    /**
     * Creates adaptor for ListViews which displays stocks in the RecyclerView for most viewed.
     * @param data Stock information list
     */
    private void propogateMostViewAdapter(List<IStock> data){
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        MostViewAdapter adapter = new MostViewAdapter(data);
        _vh._mostPopular.setAdapter(adapter);
        _vh._mostPopular.setLayoutManager(lm);
    }

    /**
     * Creates adaptor for the LinearLayout which displays the category views
     * @param data stock information for each category
     * @param category category
     */
    private void propogateCatAdapter(List<IStock> data, Category category) {
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        StockCategoriesMainAdatper adapter = new StockCategoriesMainAdatper(data);
        switch (category) {
            case InformationTechnology:
                _vh._techView.setAdapter(adapter);
                _vh._techView.setLayoutManager(lm);
                break;
            case HealthCare:
                _vh._healthView.setAdapter(adapter);
                _vh._healthView.setLayoutManager(lm);
                break;
            case Industrials:
                _vh._industryView.setAdapter(adapter);
                _vh._industryView.setLayoutManager(lm);
                break;
            case ConsumerDiscretionary:
                _vh._financeView.setAdapter(adapter);
                _vh._financeView.setLayoutManager(lm);
                break;
            default:
                throw new IllegalArgumentException("Category not supported at the moment");
        }
    }

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

    public void propgateAdapter(List<IStock> data) {
        StockCategoriesMainAdatper stockCatAdatper = new StockCategoriesMainAdatper(data);
    }

//    private void propogateCategoriesAdapter(List<IStock> data, RecyclerView view){
//        StockCategoriesMainAdatper stockCatAdapter = new StockCategoriesMainAdatper(data);
//    }
}
