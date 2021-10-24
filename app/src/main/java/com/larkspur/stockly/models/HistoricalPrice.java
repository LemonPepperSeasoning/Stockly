package com.larkspur.stockly.models;

import java.util.List;

/**
 * This class represents the historical price of a stock and handles all computation relating to price.
 * Author: Takahiro
 */
public class HistoricalPrice implements IHistoricalPrice{

    private List<Double> _historicalPrice;

    public HistoricalPrice(List<Double> historicalPrice){
        _historicalPrice = historicalPrice;
    }

    public Double getPrice() {
        return _historicalPrice.get(_historicalPrice.size()-1);
    }

    public Double getYesterdaysPrice() {
        return _historicalPrice.get(_historicalPrice.size()-2);
    }

    public List<Double> getHistoricalPrice() {
        return _historicalPrice;
    }

    public Double getLast24HourChange() {
        Double todayPrice = _historicalPrice.get(_historicalPrice.size()-1);
        Double yesterdayPrice = _historicalPrice.get(_historicalPrice.size()-2);

        Double change = (todayPrice-yesterdayPrice)/yesterdayPrice;
        return change*100;
    }

    public Double getLastWeekChange() {
        Double todayPrice = _historicalPrice.get(_historicalPrice.size()-1);
        Double weekBeforePrice = _historicalPrice.get(_historicalPrice.size()-6);

        Double change = (todayPrice-weekBeforePrice)/weekBeforePrice;
        return change*100;
    }
}
