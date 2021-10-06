package com.larkspur.stockly.Models;

import java.util.ArrayList;
import java.util.List;

public class Watchlist implements IWatchlist{

    private List<IStock> _watchlist;

    public Watchlist(){
        _watchlist = new ArrayList<>();
    }

    @Override
    public void addStock(IStock stock) {
        if (_watchlist.contains(stock)){
            //TODO : Might want to throw exception
            return;
        }
        _watchlist.add(stock);
    }

    @Override
    public void removeStock(IStock stock) {
        if (!_watchlist.contains(stock)){
            //TODO : Might want to throw exception
            return;
        }
        _watchlist.remove(stock);
    }

    @Override
    public List<IStock> getWatchlist() {
        return _watchlist;
    }
}
