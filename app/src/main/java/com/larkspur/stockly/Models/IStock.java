package com.larkspur.stockly.Models;

public interface IStock {

    public String getCompName();

    public String getSymbol();

    public String getDesc();

    public Category getCategory();

    public Float getPrice();

    public IHistoricalPrice getHistoricalPrice();
}
