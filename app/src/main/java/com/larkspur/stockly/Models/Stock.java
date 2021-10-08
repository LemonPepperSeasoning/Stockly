package com.larkspur.stockly.Models;

public class Stock implements IStock{

    private int _stockId;
    private String _companyName;
    private String _symbol;
    private String _description;
    private Category _category;
    private IHistoricalPrice _historicalPrice;

    public Stock(){}

    public Stock(int stockId, String companyName, String symbol){
        // contructor for lazy fetching or something like that
        _stockId = stockId;
        _companyName = companyName;
        _symbol = symbol;
    }

    @Override
    public String getCompName() {
        return this._companyName;
    }

    public void setname(String name){
        _companyName = name;
    }

    @Override
    public String getSymbol() {
        return this._symbol;
    }

    @Override
    public String getDesc() {
        return this._description;
    }

    @Override
    public Category getCategory() {
        return this._category;
    }

    public void setsector(String sector){
        _category = Category.valueOf(sector);
    }

    @Override
    public Float getPrice() {
        // THIS HAS HIGH DEPENDENCY
        return this._historicalPrice.getPrice();
    }


    @Override
    public IHistoricalPrice getHistoricalPrice() {
        return this._historicalPrice;
    }
}