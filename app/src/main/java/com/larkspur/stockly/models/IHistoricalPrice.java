package com.larkspur.stockly.models;

import java.io.Serializable;
import java.util.List;

public interface IHistoricalPrice extends Serializable {

    /**
     * Get current price. (of the stock)
     * @return Double : representing the price of the stock
     */
    public Double getPrice();

    /**
     * Get yesterday's price
     * @return Double : representing yesterday's price
     */
    public Double getYesterdaysPrice();

    /**
     * Get historical price. (of the stock)
     * @return List<Double> : list of numbers representing the historical price.
     */
    public List<Double> getHistoricalPrice();

    /**
     * Get percentage change in price in last 24 hours.
     * @return Double : representing the percentage change.
     */
    public Double getLast24HourChange();

    /**
     * Get percentage change in price in a span of a week.
     * @return Double : representing the percentage change.
     */
    public Double getLastWeekChange();

}
