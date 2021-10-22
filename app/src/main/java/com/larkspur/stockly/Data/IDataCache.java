package com.larkspur.stockly.Data;

import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.IStock;

import java.util.List;

public interface IDataCache {

    void addAllStock(List<IStock> stocks);

    void addCategoryStock(Category category, List<IStock> stocks);

    void addMostViewStock(IStock stocks);

    void addMostViewStocks(List<IStock> stocks);

    List<IStock> getAllStock();

    List<IStock> getTopNMostViewed(int n);

    List<IStock> getCategoryStock(Category category);

    IStock getTopGainer();

    IStock getTopLoser();

    void addTopGainer(IStock stock);

    void addTopLoser(IStock stock);
}
