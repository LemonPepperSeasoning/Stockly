package com.larkspur.stockly.models;

import java.io.Serializable;
import java.util.List;

public interface IStock extends Serializable {

    public String getCompName();

    public String getSymbol();

    public List<String> getImageLink();

    public String getDesc();

    public Category getCategory();

    public Double getPrice();

    public IHistoricalPrice getHistoricalPrice();

    public String getSubindustry();

    public String getLocation();
}
