package com.larkspur.stockly.models;

import java.util.List;

public interface IWatchlist {

    void addStock(IStock stock);

    void removeStock(IStock stock);

    List<IStock> getWatchlist();

    void removeAllStocks();

    boolean hasStock(IStock stock);

}