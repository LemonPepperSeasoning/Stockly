package com.larkspur.stockly.Models;


import java.util.Hashtable;

public interface IPortfolio {

    void addStock(IStock stock, int quantity);

    void removeStock(IStock stock, int quantity);

    Hashtable<IStock,Integer> getPortfolio();
}
