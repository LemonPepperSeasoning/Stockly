package com.larkspur.stockly.Activities.Search;

/**
 * This class is for storing the names of stocks.
 * Author: Jonathon
 */

public class StockNames {
    private String _stockName;

    public StockNames (String stockName) {
        _stockName = stockName;
    }

    public String getStockName() {
        return _stockName;
    }
}
