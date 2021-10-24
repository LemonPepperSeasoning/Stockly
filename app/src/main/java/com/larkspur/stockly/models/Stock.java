package com.larkspur.stockly.models;

import java.util.List;

/**
 * This class represents the stock and handles all functionality relating to stock.
 * Author: Takahiro
 */
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

    public String getCompName() {
        return this._companyName;
    }

    public String getSymbol() {
        return this._symbol;
    }

    public List<String> getImageLink() {
        return this._imageLink;
    }

    public String getDesc() {
        return this._description;
    }

    public Category getCategory() {
        return this._category;
    }

    public Double getPrice() {
        // THIS HAS HIGH DEPENDENCY
        return this._historicalPrice.getPrice();
    }

    public IHistoricalPrice getHistoricalPrice() {
        return this._historicalPrice;
    }

    public String getSubindustry() {
        return _subindustry;
    }

    public String getLocation() {
        return _location;
    }
}
