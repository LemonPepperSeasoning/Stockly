package com.larkspur.stockly.models;

import static org.junit.Assert.assertEquals;

import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IHistoricalPrice;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestHitoricalPrice {

    IHistoricalPrice _historicalPriceA;
    IHistoricalPrice _historicalPriceB;

    @Before
    public void setUp(){
        List<Double> pricesA = new ArrayList<>(Arrays.asList(
                41.469, 99.752, 10.502, 52.627, 18.745, 53.177, 89.306, 40.298,
                82.028, 87.085, 58.935, 23.418, 91.558, 48.409, 35.101, 42.286,
                27.821, 29.412, 26.532, 16.937, 33.113, 85.538, 7.446, 32.942, 62.408
        ));
        _historicalPriceA = new HistoricalPrice(pricesA);

        List<Double> pricesB = new ArrayList<>(Arrays.asList(
                41.469, 99.752, 10.502, 52.627, 32.942, 53.177, 89.306, 40.298,
                82.028, 87.085, 58.935, 23.418, 91.558, 48.409, 35.101, 42.286,
                27.821, 29.412, 26.532, 16.937, 33.113, 85.538, 7.446, 62.408, 18.745
        ));
        _historicalPriceB = new HistoricalPrice(pricesB);
    }

    @Test
    public void TestGetPrice(){
        assertEquals((Double)62.408, _historicalPriceA.getPrice());
    }

    @Test
    public void TestGetHistoricalPrice(){
        assertEquals(Arrays.asList(
                41.469, 99.752, 10.502, 52.627, 18.745, 53.177, 89.306, 40.298,
                82.028, 87.085, 58.935, 23.418, 91.558, 48.409, 35.101, 42.286,
                27.821, 29.412, 26.532, 16.937, 33.113, 85.538, 7.446, 32.942, 62.408
        ), _historicalPriceA.getHistoricalPrice());
    }

    @Test
    public void TestGetLast24HourChangePositive(){
        assertEquals((Double)89.44812093983366, _historicalPriceA.getLast24HourChange());
    }

    @Test
    public void TestGetLastWeekChangePositive(){
        assertEquals((Double)268.4713939894904, _historicalPriceA.getLastWeekChange());
    }

    @Test
    public void TestGetLast24HourChangeNegative(){
        assertEquals((Double)(-69.96378669401359), _historicalPriceB.getLast24HourChange());
    }

    @Test
    public void TestGetLastWeekChangeNegative(){
        // TODO : this test needs to be fixed
        assertEquals((Double)(10.67485387022495), _historicalPriceB.getLastWeekChange());
    }
}
