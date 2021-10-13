package com.larkspur.stockly.Models;

import java.io.Serializable;
import java.util.List;

public interface IHistoricalPrice extends Serializable {

    public Double getPrice();

    public List<Double> getHistoricalPrice();

    public Double getLast24HourChange();

    public Double getLastWeekChange();

}
