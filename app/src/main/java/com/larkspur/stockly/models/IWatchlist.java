package com.larkspur.stockly.models;

import java.util.List;

public interface IWatchlist {

    /**
     * Add stock to the watchlist
     * @param stock : Stock to add.
     */
    void addStock(IStock stock);

    /**
     * Remove stock from the watchlist
     * @param stock : Stock to remove.
     */
    void removeStock(IStock stock);

    /**
     * Get current state of the wastchlist
     * @return List<IStock> : Representing the list of stocks that is currently in watchlist
     */
    List<IStock> getWatchlist();

    /**
     * Remove all stocks from watchlist.
     */
    void removeAllStocks();

    /**
     * checks if stock is currently in watchlist.
     * @param stock : stock to check for.
     * @return boolean : True - if stock is in watchlist
     *                   False - if stock is not in the watchlist
     */
    boolean hasStock(IStock stock);

}
