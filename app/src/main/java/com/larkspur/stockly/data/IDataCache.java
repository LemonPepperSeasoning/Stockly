package com.larkspur.stockly.data;

import com.larkspur.stockly.models.Category;
import com.larkspur.stockly.models.IStock;

import java.util.List;

public interface IDataCache {

    /**
     * Add all stock to the cache
     * @param stocks : representing list of stocks to add to the cache
     */
    void addAllStock(List<IStock> stocks);

    /**
     * Add a category stock to the cache.
     * @param category : category type of the stocks.
     * @param stocks : list of stocks that is of that category.
     */
    void addCategoryStock(Category category, List<IStock> stocks);

    /**
     * Add most viewed stock to the category.
     * @param stocks : representing the most viewed stock.
     */
    void addMostViewStock(IStock stocks);

    /**
     * -- UNUSED --
     * Add list of most viewed stocks.
     * @param stocks : representing the list of most viewed stocks
     */
    void addMostViewStocks(List<IStock> stocks);

    /**
     * Get all stocks that is currently in cache
     * @return List<IStock> : representing the list of stocks.
     */
    List<IStock> getAllStock();

    /**
     * Get top N most viewed stocks
     * @param n : number of stocks to get.
     * @return List<IStock> : representing the list of IStock.
     */
    List<IStock> getTopNMostViewed(int n);

    /**
     * Get list of stocks for a specific category. (If there are any in cache)
     * @param category : Category to get stocks for.
     * @return List<IStock> : list of stocks that is of the input category.
     */
    List<IStock> getCategoryStock(Category category);

    /**
     * Get a stock with the highest week gainer. (in terms of percentage change).
     * @return IStock : representing the top gained stock.
     */
    IStock getTopGainer();

    /**
     * Get a stock with the highest week loser. (in terms of percentage change).
     * @return IStock : representing the top stock stock.
     */
    IStock getTopLoser();

    /**
     * Add a stock with the highest week gainer. (in terms of percentage change).
     * @param stock : representing the top gained stock.
     */
    void addTopGainer(IStock stock);

    /**
     * Add a stock with the highest week loser. (in terms of percentage change).
     * @param stock : representing the top lost stock.
     */
    void addTopLoser(IStock stock);
}
