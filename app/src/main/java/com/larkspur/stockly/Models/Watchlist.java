package com.larkspur.stockly.Models;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Watchlist implements IWatchlist {

    private List<IStock> _watchlist;
    private static Watchlist single_instance = null;


    private Watchlist() {
        _watchlist = new ArrayList<>();
    }

    public static Watchlist getInstance() {
        if (single_instance == null) {
            single_instance = new Watchlist();
        }
        return single_instance;
    }


    @Override
    public void addStock(IStock stock) {
        if (_watchlist.contains(stock)) {
            //TODO : Might want to throw exception
            return;
        }
        _watchlist.add(stock);
    }

    @Override
    public void removeStock(IStock stock) {
        if (!_watchlist.contains(stock)) {
            //TODO : Might want to throw exception
            return;
        }
        _watchlist.remove(stock);
    }

    @Override
    public List<IStock> getWatchlist() {
        return _watchlist;
    }

    @Override
    public boolean hasStock(IStock stock) {
        return _watchlist.contains(stock);
    }
}
