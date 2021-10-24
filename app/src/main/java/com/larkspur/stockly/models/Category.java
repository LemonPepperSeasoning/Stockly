package com.larkspur.stockly.models;

import android.graphics.Color;

import com.larkspur.stockly.R;

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

    //private static Category _category;
    private final String _categoryName;
    private final int _color;
    private final int _drawableId;

    private Category(String categoryName, int rgb, int drawableId ) {
        this._categoryName = categoryName;
        this._color = rgb;
        this._drawableId = drawableId;
    }

    public String getCategoryName() {
        return this._categoryName;
    }

    public int getDrawableId(){
        return this._drawableId;
    }

    public int getColor(){
        return this._color;
    }

    @Override
    public String toString(){
        return _categoryName;
    }

    public static Category getValue(String value){
        Category category;
        String newValue = value.replaceAll(" ", "").replace("\n","");
        category = Category.valueOf(newValue);
        return category;
    }
}
