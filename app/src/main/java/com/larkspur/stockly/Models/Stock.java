package com.larkspur.stockly.Models;

import java.util.List;

public class Stock implements IStock{

    private int _stockId;
    private String _companyName;
    private String _symbol;
    private String _description;
    private Category _category;
    private IHistoricalPrice _historicalPrice;

    private List<Float> price;

    //Extra field
    private String _subindustry;
    private String _location;

    public Stock(){}

    public Stock(int stockId, String companyName, String symbol){
        // contructor for lazy fetching or something like that
        _stockId = stockId;
        _companyName = companyName;
        _symbol = symbol;
    }

    // The consturctor I will be using for creating Stock object from Firebase.
    public Stock(String companyName,
                 String symbol,
                 Category category,
                 String subIndustry,
                 String location,
                 HistoricalPrice historicalPrice){
        _companyName = companyName;
        _symbol = symbol;
        _category = category;
        _subindustry = subIndustry;
        _location = location;
        _historicalPrice = historicalPrice;
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


    @Override
    public Float getPrice() {
        // THIS HAS HIGH DEPENDENCY
        return this._historicalPrice.getPrice();
    }


    @Override
    public IHistoricalPrice getHistoricalPrice() {
        return this._historicalPrice;
    }

//    public void setSector(String sector){
//        _category = Category.valueOf(sector);
//    }
}
