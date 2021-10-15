package com.larkspur.stockly.Models;

public enum Category {

    Industrials("Industrials"),
    HealthCare("Health Care"),
    InformationTechnology("Information Technology"),
    CommunicationServices("Communication Services"),
    ConsumerDiscretionary("Consumer Discretionary"),
    Utilities("Utilities"),
    Materials("Materials"),
    RealEstate("RealEstate"),
    ConsumerStaples("ConsumerStaples"),
    Energy("Energy"),
    Financials("Financials");

    //private static Category _category;
    private final String _categoryName;
    private Category (String categoryName){
        this._categoryName = categoryName;
    }

    public String getCategoryName() {
        return this._categoryName;
    }

    @Override
    public String toString(){
        return _categoryName;
    }

    public static Category getValue(String value){
        Category category;
        String newValue = value.replaceAll(" ", "");
        category = Category.valueOf(newValue);
        return category;
    }
}
