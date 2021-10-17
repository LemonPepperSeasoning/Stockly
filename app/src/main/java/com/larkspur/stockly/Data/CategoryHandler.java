package com.larkspur.stockly.Data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;

import java.util.LinkedList;
import java.util.List;

/**
 * This classes fetches data for the stocks inside a specific category from FireStore.
 * Author: Takahiro
 */
public class CategoryHandler extends DataHandler{
    private FirebaseFirestore db;

    /**
     * Default constructor
     */
    public CategoryHandler(){
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Fetches the list of stock objects in a specific category
     * @param category Category of which the user wishes to fetch data from
     * @return
     */
    public List<IStock> getCategoryStocks(Category category) {
        List<IStock> stockList = new LinkedList<>();

        db.collection("company_v1")
                .whereEqualTo("sector",category.toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot results = task.getResult();
                            for (IStock stock : results.toObjects(Stock.class)) {
                                Log.d("Parsing stocks", stock.getCompName() + "loaded");
                            }
                        } else {
                            Log.d("Error getting stock: ", String.valueOf(task.getException()));
                        }
                    }
                });
        return stockList;
    }

    /**
     * Returns stock object
     * @return null
     */
    public IStock getStock() {
        return null;
    }
}
