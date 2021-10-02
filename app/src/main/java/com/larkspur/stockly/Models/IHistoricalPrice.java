package com.larkspur.stockly.Models;

import java.util.List;

public interface IHistoricalPrice {

    public Float getPrice();

    public List<Float> getHistoricalPrice();

    public Float getLast24HourChange();

    public Float getLastWeekChange();

}
