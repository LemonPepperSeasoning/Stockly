package com.larkspur.stockly.Models;

import java.util.List;

public class HistoricalPrice implements IHistoricalPrice{

    private int _historicalPriceId;
    private IStock _stock;
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
    public Float getLast24HourChange() {
        //TODO : implement computation
        return null;
    }

    @Override
    public Float getLastWeekChange() {
        //TODO : implement computation
        return null;
    }
}
