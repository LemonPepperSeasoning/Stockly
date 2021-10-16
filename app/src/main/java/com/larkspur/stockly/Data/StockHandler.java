package com.larkspur.stockly.Data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Data.mappers.StockMapper;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.larkspur.stockly.Models.IUser;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.User;

public class StockHandler extends DataHandler {

    private static StockHandler single_instance = null;

    private List<IStock> _stockCollection;
    private List<IStock> _mostViewCollection;
    private Hashtable<Category,List<IStock>> _categoricalCollection;

    private StockHandler() {
        _stockCollection = new ArrayList<>();
        _mostViewCollection = new ArrayList<>();
        _categoricalCollection = new Hashtable<>();
    }

    public static StockHandler getInstance() {
        if (single_instance == null) {
            single_instance = new StockHandler();
        }
        return single_instance;
    }

    public void addAllStock(List<IStock> stocks){
        _stockCollection.addAll(stocks);
    }

    public void addCategoryStock(Category category, List<IStock> stocks){
        List<IStock> tmp = new ArrayList<>();
        tmp.addAll(stocks);
        _categoricalCollection.put(category, tmp);
    }

    public void addMostViewStock(List<IStock> stocks){
        _mostViewCollection.addAll(stocks);
    }

    public List<IStock> getAllStock(){
        if (_stockCollection.isEmpty()){
            return null;
        }
        return _stockCollection;
    }

    public List<IStock> getTopNMostViewed(int n){
        // TODO : Sort by view count, return top N stocks
        return null;
    }

    public List<IStock> getCategoryStock(Category category){
        if (!_categoricalCollection.containsKey(category)){
            return null;
        }
        return _categoricalCollection.get(category);
    }
}
