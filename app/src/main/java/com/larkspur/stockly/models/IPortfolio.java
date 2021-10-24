package com.larkspur.stockly.models;


import java.util.Hashtable;

public interface IPortfolio {

    void addStock(IStock stock, int quantity);

    void removeStock(IStock stock, int quantity);

    Hashtable<IStock,Integer> getPortfolio();

    void removeAllStocks();

    int getQuantity(String stockSymbol);

    Double getTotalValue();

    Double getTotal24HrChange();
}
