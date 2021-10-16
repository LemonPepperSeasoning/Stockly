package com.larkspur.stockly.Data.mappers;

import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;

import java.util.List;
import java.util.Map;

public class StockMapper {

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
