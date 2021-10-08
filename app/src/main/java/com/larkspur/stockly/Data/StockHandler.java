package com.larkspur.stockly.Data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.IStock;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.larkspur.stockly.Models.Stock;

public class StockHandler extends DataHandler {

    private FirebaseFirestore _db;

    public StockHandler(FirebaseFirestore db) {
       _db = db;
    }
    public StockHandler() {
        _db = FirebaseFirestore.getInstance();
    }

    public List<IStock> getStocksList() {
        List<IStock> stockList = new LinkedList<IStock>();
        _db.collection("company_v2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot results = task.getResult();
                            for (IStock stock : results.toObjects(Stock.class)) {
                                stockList.add(stock);
                                Log.d("Parsing stocks", stock.getCompName() + "loaded");
                            }
                        } else {
                            Log.d("Error getting stock: ", String.valueOf(task.getException()));
                        }
                    }
                });
        return stockList;
    }



    public IStock getStock(String name) {
        final IStock[] stock = {new Stock()};
        _db.collection("company_v2")
                .whereEqualTo("Name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        System.out.println("=======2===========");
                        if (task.isSuccessful()) {
                            QuerySnapshot results = task.getResult();
                            System.out.println(results.toString());
                            if(results.size() == 1) {
                                stock[0] = results.toObjects(Stock.class).get(0);
                                Log.d("Parsing stocks", stock[0].getCompName() + "loaded");
                            }
                        } else {
                            Log.d("Error getting stock: ", String.valueOf(task.getException()));
                        }
                    }
                });
        return stock[0];
    }

    public IStock getStock2(String name) {
        _db.collection("company_v2")
                .whereEqualTo("Name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("+++++", document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                List<Float> tmpList = (List<Float>)data.get("Price");
                                System.out.println(tmpList);
                                System.out.println(tmpList.getClass());
                            }
                        } else {
                            Log.d("+++++", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return null;
    }
}
