package com.larkspur.stockly.models;

import java.util.List;

public class Stock implements IStock {

    private String _companyName;
    private String _symbol;
    private String _description;
    private Category _category;
    private IHistoricalPrice _historicalPrice;
    private List<String> _imageLink;

    //Extra field
    private String _subindustry;
    private String _location;

    public Stock(String companyName,
                 String symbol,
                 Category category,
                 String subIndustry,
                 String location,
                 String description,
                 List<String> imageLink,
                 IHistoricalPrice historicalPrice) {
        _companyName = companyName;
        _symbol = symbol;
        _category = category;
        _subindustry = subIndustry;
        _location = location;
        _historicalPrice = historicalPrice;
        _description = description;
        _imageLink = imageLink;
    }

    @Override
    public String getCompName() {
        return this._companyName;
    }

    @Override
    public String getSymbol() {
        return this._symbol;
    }

    @Override
    public List<String> getImageLink() {
        return this._imageLink;
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
    public Double getPrice() {
        // THIS HAS HIGH DEPENDENCY
        return this._historicalPrice.getPrice();
    }

    @Override
    public IHistoricalPrice getHistoricalPrice() {
        return this._historicalPrice;
    }

    @Override
    public String getSubindustry() {
        return _subindustry;
    }


    @Override
    public String getLocation() {
        return _location;
    }
}
