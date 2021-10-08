package com.larkspur.stockly.Models;

import java.io.Serializable;
import java.util.List;

public interface IHistoricalPrice extends Serializable {

    public Double getPrice();

    public List<Double> getHistoricalPrice();

    public Float getLast24HourChange();

    public Float getLastWeekChange();

}
