package com.larkspur.stockly.data;

import com.larkspur.stockly.models.Category;
import com.larkspur.stockly.models.IStock;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class DataCache implements IDataCache{

    private static DataCache single_instance = null;

    private List<IStock> _stockCollection;
    private List<IStock> _mostViewCollection;
    private Hashtable<Category,List<IStock>> _categoricalCollection;
    private IStock _topGainer;
    private IStock _topLoser;

    private DataCache() {
        _stockCollection = new ArrayList<>();
        _mostViewCollection = new ArrayList<>();
        _categoricalCollection = new Hashtable<>();
    }

    public static DataCache getInstance() {
        if (single_instance == null) {
            single_instance = new DataCache();
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
    public void addMostViewStock(IStock stocks){
        _mostViewCollection.add(stocks);
    }

    public void addMostViewStocks(List<IStock> stocks){
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
        return _mostViewCollection;
    }

    public List<IStock> getCategoryStock(Category category){
        if (!_categoricalCollection.containsKey(category)){
            return null;
        }
        return _categoricalCollection.get(category);
    }

    public IStock getTopGainer(){
        return _topGainer;
    }

    public IStock getTopLoser(){
        return _topLoser;
    }

    public void addTopGainer(IStock stock){
        _topGainer = stock;
    }

    public void addTopLoser(IStock stock){
        _topLoser = stock;
    }
}
