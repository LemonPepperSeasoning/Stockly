package com.larkspur.stockly.Models;

public class Stock implements IStock{

    private int stockId;
    private String companyName;
    private String symbol;
    private String description;
    private Category category;
    private IHistoricalPrice historicalPrice;

    @Override
    public String getCompName() {
        return this.companyName;
    }

    @Override
    public String getSymbol() {
        return this.symbol;
    }

    @Override
    public String getDesc() {
        return this.description;
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    @Override
    public Float getPrice() {
        // THIS HAS HIGH DEPENDENCY
        return this.historicalPrice.getPrice();
    }

    @Override
    public IHistoricalPrice getHistoricalPrice() {
        return this.historicalPrice;
    }
}
