package com.larkspur.stockly.data.mappers;

import com.larkspur.stockly.models.Category;
import com.larkspur.stockly.models.HistoricalPrice;
import com.larkspur.stockly.models.IStock;
import com.larkspur.stockly.models.Stock;

import java.util.List;
import java.util.Map;

/**
 * This class returns Stock object by fetching the data inside a map between a stock
 * and its contents such as name, symbol.
 * Author: Takahiro
 */
public class StockMapper {

    /**
     * Returns Stock object by fetching the data inside a map between a stock
     * and its contents such as name, symbol.
     * @param data map between a stock and its fields
     * @return Stock object
     */
    public static IStock toStock(Map<String, Object> data) {
        HistoricalPrice tmpHistoricalPrice = new HistoricalPrice((List<Double>) data.get("Price"));
        return new Stock(
                ((String) data.get("Name")),
                ((String) data.get("Symbol")),
                (Category.getValue((String) data.get("Category"))),
                ((String) data.get("Subindustry")),
                ((String) data.get("location")),
                ((String) data.get("Description")),
                ((List<String>) data.get("ImageLink")),
                tmpHistoricalPrice);
    }
}
