package com.larkspur.stockly.Models;

import android.os.Parcel;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class Stock implements IStock {

    private int _stockId;
    private String _companyName;
    private String _symbol;
    private String _description;
    private Category _category;
    private IHistoricalPrice _historicalPrice;
    private List<String> _imageLink;

    //Extra field
    private String _subindustry;
    private String _location;

    public Stock() {
    }

    public Stock(int stockId, String companyName, String symbol) {
        // constructor for lazy fetching or something like that
        _stockId = stockId;
        _companyName = companyName;
        _symbol = symbol;
    }

    // The constructor I will be using for creating Stock object from Firebase.
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
    public void setCompName(String name) {
        _companyName = name;
    }

    public void setname(String name) {
        _companyName = name;
    }

    @Override
    public String getSymbol() {
        return this._symbol;
    }

    @Override
    public void setSymbol(String symbol) {
        _symbol = symbol;
    }

    @Override
    public void setImageLink(List<String> links) {
        _imageLink = links;
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
    public void setDesc(String desc) {
        _description = desc;
    }

    @Override
    public Category getCategory() {
        return this._category;
    }

    @Override
    public void setCategory(Category category) {
        _category = category;
    }


    @Override
    public Double getPrice() {
        // THIS HAS HIGH DEPENDENCY
        return this._historicalPrice.getPrice();
    }

    @Override
    public void setPrice(Double price) {

    }


    @Override
    public IHistoricalPrice getHistoricalPrice() {
        return this._historicalPrice;
    }

    @Override
    public void setHistoricalPrice(HistoricalPrice historicalPrice) {
        _historicalPrice = historicalPrice;
    }

    @Override
    public String getSubindustry() {
        return _subindustry;
    }

    @Override
    public void setSubindustry(String subindustry) {
        _subindustry = subindustry;
    }

    @Override
    public String getLocation() {
        return _location;
    }

    @Override
    public void setLocation(String location) {
        _location = location;
    }
}
