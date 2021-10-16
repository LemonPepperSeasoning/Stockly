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
import com.larkspur.stockly.Data.mappers.StockMapper;
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

public class MainActivity extends CoreActivity implements SearchView.OnQueryTextListener {

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
            _usernameText.setText("Hi " + _user.getUsername());
        }
    }
    
    private ViewHolder _vh;

    //        =======================Search functionality=============================
    ListView list;
    //        =======================--------------------=============================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _vh = new ViewHolder();
        this.setTitle("Home");

        getStockMostView();
        setupCategoryViews();

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

    private void setupCategoryViews(){
        getCategoryStock(Category.HealthCare);
        getCategoryStock(Category.InformationTechnology);
        getCategoryStock(Category.ConsumerDiscretionary);
        getCategoryStock(Category.Industrials);
    }

    private void getStockMostView(){
        List<IStock> stockList = _stockHandler.getTopNMostViewed(10);
        if (stockList == null){
            fetchStockMostView();
        }else{
            propogateMostViewAdapter(stockList);
        }
    }

    private void getCategoryStock(Category category){
        List<IStock> stockList = _stockHandler.getCategoryStock(category);
        if (stockList == null){
            fetchStockByCategory(category);
        }else{
            propogateCatAdapter(stockList, category);
        }
    }

    @Override
    public void clickHome(View view) {
        closeDrawer(_drawerLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(_drawerLayout);
    }


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
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        stockList.add(StockMapper.toStock(document.getData()));
                    }

                    if (stockList.size() > 0) {
                        propogateCatAdapter(stockList, category);
                        _stockHandler.addCategoryStock(category,stockList);
                    } else {
                        Log.d("Fetch Failed", "return value was empty");
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch stocks by category");
                }
            }
        });
    }

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

                    QuerySnapshot results = task.getResult();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();

                        DocumentReference ref = (DocumentReference)data.get("company");
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    stockList.add(StockMapper.toStock(task.getResult().getData()));

                                    if(stockList.size() == 10){
                                        propogateMostViewAdapter(stockList);
                                        _stockHandler.addMostViewStock(stockList);
                                    }
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

    private void propogateMostViewAdapter(List<IStock> data){
        LinearLayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        MostViewAdapter adapter = new MostViewAdapter(data);
        _vh._mostPopular.setAdapter(adapter);
        _vh._mostPopular.setLayoutManager(lm);
    }

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
}
