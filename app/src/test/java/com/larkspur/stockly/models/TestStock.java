package com.larkspur.stockly.models;

import static org.junit.Assert.assertEquals;

import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IHistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Stock;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestStock {

    // TODO : Write a test for getDesc()
    private IHistoricalPrice _historicalPrice;
    private IStock _stock;

    @Before
    public void setUp(){
        List<Double> prices = new ArrayList<>(Arrays.asList(
                41.469, 99.752, 10.502, 52.627, 18.745, 53.177, 89.306, 40.298,
                82.028, 87.085, 58.935, 23.418, 91.558, 48.409, 35.101, 42.286,
                27.821, 29.412, 26.532, 16.937, 33.113, 85.538, 7.446, 32.942, 62.408
        ));
        _historicalPrice = new HistoricalPrice(prices);

        _stock = new Stock("Apple", "AAPL",
                Category.InformationTechnology,
                "Technology Hardware, Storage & Peripherals",
                "Cupertino, California",
                _historicalPrice);
    }

    @Test
    public void TestGetCompName(){
        assertEquals("Apple", _stock.getCompName());
    }

    @Test
    public void TestGetSymbol(){
        assertEquals("AAPL", _stock.getSymbol());
    }

    @Test
    public void TestGetPrice(){
        assertEquals((Double)62.408, _stock.getPrice());
    }

    @Test
    public void TestGetHistoricalPrice(){
        assertEquals(_historicalPrice, _stock.getHistoricalPrice());
    }

    @Test
    public void TestGetSubindustry(){
        assertEquals("Technology Hardware, Storage & Peripherals", _stock.getSubindustry());
    }

    @Test
    public void TestGetLocation(){
        assertEquals("Cupertino, California", _stock.getLocation());
    }
}
