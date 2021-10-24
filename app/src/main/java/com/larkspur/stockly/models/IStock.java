package com.larkspur.stockly.models;

import java.io.Serializable;
import java.util.List;

public interface IStock extends Serializable {

    /**
     * Get company name of the stock
     * @return String : Representing the company name
     */
    public String getCompName();

    /**
     * Get stock symbol
     * @return String : Representing the stock symbol.
     */
    public String getSymbol();

    /**
     * Get links to the images, stored in the firebase cloud storage.
     * @return List<String> : Representing the location of images.
     *                        Each string is a location of a image.
     */
    public List<String> getImageLink();

    /**
     * Get the description of the stock.
     * @return String : Representing the stock description.
     */
    public String getDesc();

    /**
     * Get the category of the stock.
     * @return Category : Representing the stock category.
     */
    public Category getCategory();

    /**
     * Get the current price of the stock.
     * @return Double : Representing the current price.
     */
    public Double getPrice();

    /**
     * Get historical price of the stock.
     * @return IHistoricalPrice : Representing the historical price of the stock.
     */
    public IHistoricalPrice getHistoricalPrice();

    /**
     * Get the sub-industry of the stock
     * @return String : Representing the sub-industry of the stock.
     */
    public String getSubindustry();

    /**
     * Get the head-quarter location of the company
     * @return String : Representing the company location.
     */
    public String getLocation();
}
