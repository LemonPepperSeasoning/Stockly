package com.larkspur.stockly.models;

import android.graphics.Color;

import com.larkspur.stockly.R;

/**
 * This class represents the category of the stocks.
 * Author: Alan
 */
public enum Category {

    Industrials("Industrials", Color.rgb(220,20,60), R.drawable.industrials),
    HealthCare("Health Care", Color.rgb(255,140,0), R.drawable.healthcare),
    InformationTechnology("Information Technology", Color.rgb(255,255,0), R.drawable.informationtechnology),
    CommunicationServices("Communication Services", Color.rgb(32,178,170), R.drawable.communicationservice),
    ConsumerDiscretionary("Consumer Discretionary", Color.rgb(244,164,96), R.drawable.consumerdiscretionary),
    Utilities("Utilities", Color.rgb(0,255,0), R.drawable.utilities),
    Materials("Materials", Color.rgb(0,255,255), R.drawable.materials),
    RealEstate("Real Estate", Color.rgb(0,191,255), R.drawable.realestate),
    ConsumerStaples("Consumer Staples", Color.rgb(123,104,238), R.drawable.consumerstaples),
    Energy("Energy", Color.rgb(255,20,147), R.drawable.energy),
    Financials("Financials", Color.rgb(255,250,205), R.drawable.financials);

    private final String _categoryName;
    private final int _color;
    private final int _drawableId;

    /**
     * Basic constructor for enum.
     * @param categoryName
     * @param rgb
     * @param drawableId
     */
    private Category(String categoryName, int rgb, int drawableId ) {
        this._categoryName = categoryName;
        this._color = rgb;
        this._drawableId = drawableId;
    }

    /**
     * Get category name
     * @return String : representing the category name
     */
    public String getCategoryName() {
        return this._categoryName;
    }

    /**
     * Get the icon of the category.
     * @return int : representing the drawable id.
     */
    public int getDrawableId(){
        return this._drawableId;
    }

    /**
     * Get color of the category. (this color is used to paint the category icons)
     * @return int : representing the color.
     */
    public int getColor(){
        return this._color;
    }

    @Override
    public String toString(){
        return _categoryName;
    }

    /**
     * Get Category value of a string.
     * Given string, return a Category object.
     * @param value : string representing the category.
     * @return Category : Category Enum object.
     */
    public static Category getValue(String value){
        Category category;
        String newValue = value.replaceAll(" ", "").replace("\n","");
        category = Category.valueOf(newValue);
        return category;
    }
}
