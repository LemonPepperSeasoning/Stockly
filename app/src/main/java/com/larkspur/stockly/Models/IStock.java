package com.larkspur.stockly.Models;

import android.os.Parcelable;

import java.io.Serializable;

public interface IStock extends Serializable {

    public String getCompName();

    public void setCompName(String name);

    public String getSymbol();

    public void setSymbol(String symbol);


    public String getDesc();

    public void setDesc(String desc);

    public Category getCategory();

    public void setCategory(Category category);

    public Double getPrice();

    public void setPrice(Double price);

    public IHistoricalPrice getHistoricalPrice();

    public void setHistoricalPrice(HistoricalPrice historicalPrice);

    public String getSubindustry();

    public void setSubindustry(String subindustry);

    public String getLocation();

    public void setLocation(String location);
}
