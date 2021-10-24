package com.larkspur.stockly.models;


import java.util.Hashtable;

public interface IPortfolio {

    /**
     * Add stock to the portfolio
     * @param stock : stock to add.
     * @param quantity : quantity to add
     */
    void addStock(IStock stock, int quantity);

    /**
     * Remove stock from portfolio
     * @param stock : stock to remove
     * @param quantity : quantity to remove
     */
    void removeStock(IStock stock, int quantity);

    /**
     * Get the current state of portfolio.
     * @return Hashtable<IStock,Integer> : where key is the stock that is
     *                       currently in portfolio and value is quantity.
     */
    Hashtable<IStock,Integer> getPortfolio();

    /**
     * Remove all stock from portfolio
     */
    void removeAllStocks();

    /**
     * Get quantity of stock that is currently in portfolio
     * @param stockSymbol : Symbol of the stock that we want to know quantity of.
     * @return int : representing the quantity.
     */
    int getQuantity(String stockSymbol);

    /**
     * Get the sum of total value that is in current portfolio
     * @return Double : representing the total value of the portfolio
     */
    Double getTotalValue();

    /**
     * Get percentage change in last 24 hours in total value of portfolio.
     * @return Double : representing the percentage change.
     */
    Double getTotal24HrChange();
}
