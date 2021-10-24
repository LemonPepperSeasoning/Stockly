package com.larkspur.stockly.models;

import java.util.List;

public class HistoricalPrice implements IHistoricalPrice{

    private List<Double> _historicalPrice;

    public HistoricalPrice(List<Double> historicalPrice){
        _historicalPrice = historicalPrice;
    }

    @Override
    public Double getPrice() {
        return _historicalPrice.get(_historicalPrice.size()-1);
    }

    @Override
    public Double getYesterdaysPrice() {
        return _historicalPrice.get(_historicalPrice.size()-2);
    }

    @Override
    public List<Double> getHistoricalPrice() {
        return _historicalPrice;
    }

    @Override
    public Double getLast24HourChange() {
        Double todayPrice = _historicalPrice.get(_historicalPrice.size()-1);
        Double yesterdayPrice = _historicalPrice.get(_historicalPrice.size()-2);

        Double change = (todayPrice-yesterdayPrice)/yesterdayPrice;
        return change*100;
    }

    @Override
    public Double getLastWeekChange() {
        Double todayPrice = _historicalPrice.get(_historicalPrice.size()-1);
        Double weekBeforePrice = _historicalPrice.get(_historicalPrice.size()-6);

        Double change = (todayPrice-weekBeforePrice)/weekBeforePrice;
        return change*100;
    }
}