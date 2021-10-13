package com.larkspur.stockly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.ListViewAdapter;
import com.larkspur.stockly.Adaptors.StockCategoriesMainAdatper;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    private class ViewHolder{
        DrawerLayout _drawerLayout;
        ListView _listView;
        TextView _categoryText;


        public ViewHolder(){
            _drawerLayout = findViewById(R.id.drawer_layout);
            _listView = findViewById(R.id.list_view);
            _categoryText = findViewById(R.id.category_text_view);
        }
    }

    private ViewHolder _vh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        _vh = new ViewHolder();
        if (getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            String stringCategory = intent.getStringExtra("Category");
            _vh._categoryText.setText(stringCategory);
            Category category = Category.getValue(stringCategory);
            fetchCategoryStocks(category);
        }else{
            throw new RuntimeException("Stock not found!");
        }
    }

    private void fetchCategoryStocks(Category category){
        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company_v2")
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
                                tmpHistoricalPrice);
                        stockList.add(tmpStock);
                    }

                    System.out.println("============================");
                    System.out.println(stockList.size());
                    for (IStock i : stockList) {
                        Log.d("== Stock : ", i.getCompName() + " " + i.getCategory() + " " + i.getSymbol() + " == ");
                    }
                    System.out.println("============================");

                    if (stockList.size() > 0) {
                        Log.i("Getting colors", "Success");

                        propogateCatAdapter(stockList);
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

    private void propogateCatAdapter(List<IStock> data) {
        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.list_item,data);
        _vh._listView.setAdapter(adapter);
        _vh._listView.setVisibility(View.VISIBLE);
    }

    public void clickMenu(View view){
        MainActivity.openDrawer(_vh._drawerLayout);
    }

    public void clickCloseSideMenu(View view){
        MainActivity.closeDrawer(_vh._drawerLayout);
    }

    public void clickHome(View view){
        MainActivity.redirectActivity(this,MainActivity.class);
    }

    public void clickPortfolio(View view){
        recreate();
    }

    public void clickWatchlist(View view){
        MainActivity.redirectActivity(this,WatchlistActivity.class);
    }

    public void clickSettings(View view){
        MainActivity.redirectActivity(this,SettingsActivity.class);
    }

    public void clickHelp(View view) {
        MainActivity.redirectActivity(this,HelpActivity.class);
    }

    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.closeDrawer(_vh._drawerLayout);
    }
}
