package com.larkspur.stockly.models;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the watchlist and handles all functionality relating to watchlist.
 * Author: Takahiro
 */
public class Watchlist implements IWatchlist {

    private Map<String,IStock> _watchlist;

    public Watchlist() {
        _watchlist = new HashMap<>();
    }

    public void addStock(IStock stock) {
        if (_watchlist.containsKey(stock.getSymbol())) {
            //TODO : Might want to throw exception
            return;
        }
        _watchlist.put(stock.getSymbol(),stock);
    }

    public void removeStock(IStock stock) {
        if (!_watchlist.containsKey(stock.getSymbol())){
            //TODO : Might want to throw exception
            return;
        }
        _watchlist.remove(stock.getSymbol());
    }

    public List<IStock> getWatchlist() {
        List<IStock> stockList = new ArrayList<>(_watchlist.values());
        return stockList;
    }

    public void removeAllStocks() {
        _watchlist.clear();
    }

    public boolean hasStock(IStock stock) {
        return _watchlist.containsKey(stock.getSymbol());
    }
}
