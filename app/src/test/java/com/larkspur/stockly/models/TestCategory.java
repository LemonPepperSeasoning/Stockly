package com.larkspur.stockly.models;

import static org.junit.Assert.assertEquals;

import com.larkspur.stockly.Models.Category;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCategory {

    private final List<Category> _categroyList = new ArrayList<>(Arrays.asList(
            Category.Industrials,
            Category.HealthCare,
            Category.InformationTechnology,
            Category.CommunicationServices,
            Category.ConsumerDiscretionary,
            Category.Utilities,
            Category.Materials,
            Category.RealEstate,
            Category.ConsumerStaples,
            Category.Energy));

    private final List<String> _stringList = new ArrayList<>(Arrays.asList(
            "Industrials",
            "Health Care",
            "Information Technology",
            "Communication Services",
            "Consumer Discretionary",
            "Utilities",
            "Materials",
            "RealEstate",
            "ConsumerStaples",
            "Energy"));

    @Test
    public void TestToString(){
        for (int i=0; i<_categroyList.size(); i++){
            assertEquals(_stringList.get(i), _categroyList.get(i).toString());
        }
    }

    @Test
    public void TestGetValue(){
        for (int i=0; i<_categroyList.size(); i++){
            assertEquals(_categroyList.get(i), Category.getValue(_stringList.get(i)));
        }
    }
}
