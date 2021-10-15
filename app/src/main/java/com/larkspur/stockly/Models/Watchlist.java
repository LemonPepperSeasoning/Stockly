package com.larkspur.stockly.Models;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Watchlist implements IWatchlist {

    private Map<String,IStock> _watchlist;

    public Watchlist() {
        _watchlist = new HashMap<>();
    }

    @Override
    public void addStock(IStock stock) {
        if (_watchlist.containsKey(stock.getSymbol())) {
            //TODO : Might want to throw exception
            return;
        }
        _watchlist.put(stock.getSymbol(),stock);
    }

    @Override
    public void removeStock(IStock stock) {
        if (!_watchlist.containsKey(stock.getSymbol())){
            //TODO : Might want to throw exception
            return;
        }
        _watchlist.remove(stock.getSymbol());
    }

    @Override
    public List<IStock> getWatchlist() {
        List<IStock> stockList = new ArrayList<>(_watchlist.values());
        return stockList;
    }

    @Override
    public void removeAllStocks() {
        _watchlist.clear();
    }

    @Override
    public boolean hasStock(IStock stock) {
        return _watchlist.containsKey(stock.getSymbol());
    }

}
