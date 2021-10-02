package com.larkspur.stockly.Models;

import java.util.List;

public class HistoricalPrice implements IHistoricalPrice{

    private int historicalPriceId;
    private IStock stock;
    private List<Float> historicalPrice;

    @Override
    public Float getPrice() {
        return historicalPrice.get(historicalPrice.size()-1);
    }

    @Override
    public List<Float> getHistoricalPrice() {
        return historicalPrice;
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
