package com.larkspur.stockly.Models;

import java.util.List;

public interface IHistoricalPrice {

    public Double getPrice();

    public List<Double> getHistoricalPrice();

    public Float getLast24HourChange();

    public Float getLastWeekChange();

}
