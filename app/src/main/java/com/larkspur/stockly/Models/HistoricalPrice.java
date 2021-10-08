package com.larkspur.stockly.Models;

import java.util.List;

public class HistoricalPrice implements IHistoricalPrice{

    private int _historicalPriceId;
    private IStock _stock;
    private List<Float> _historicalPrice;

    public HistoricalPrice(List<Float> historicalPrice){
        _historicalPrice = historicalPrice;
    }

    @Override
    public Float getPrice() {
        return _historicalPrice.get(_historicalPrice.size()-1);
    }

    @Override
    public List<Float> getHistoricalPrice() {
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
