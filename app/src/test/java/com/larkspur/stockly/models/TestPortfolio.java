package com.larkspur.stockly.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TestPortfolio {

    IPortfolio portfolio;
    IStock x;
    IStock y;

    @Before
    public void setupPortfolio(){
        portfolio = new Portfolio();
        portfolio.removeAllStocks();

        List<String> imageLinkApple = new ArrayList<>();
        List<Double> pricesApple = new ArrayList<>();
        HistoricalPrice historicalPriceApple = new HistoricalPrice(pricesApple);
        x = new Stock("Apple", "AAPL", Category.InformationTechnology,
                "Technology Hardware, Storage & Peripherals", "Cupertino Califonia",
                "some long desciption of apple, this is just a place holder text",imageLinkApple,
                historicalPriceApple);

        List<String> imageLinkAmazon = new ArrayList<>();
        List<Double> pricesAmazon = new ArrayList<>();
        HistoricalPrice historicalPriceAmazon = new HistoricalPrice(pricesAmazon);
        y = new Stock("Amazon", "AMZN", Category.ConsumerDiscretionary,
                "Internet & Direct Marketing Retail", "Seattle Washington",
                "some long desciption of amazon, this is just a place holder text",imageLinkAmazon,
                historicalPriceAmazon);
    }

    @Test
    public void TestCorrectAdd(){

        portfolio.addStock(x,10);

        portfolio.addStock(y,20);

        Hashtable hashtableP = portfolio.getPortfolio();
        assertEquals(2, hashtableP.size());
        assertEquals(10, hashtableP.get(x));
        assertEquals(20, hashtableP.get(y));
    }

    @Test
    public void TestCorrectRemove(){
        portfolio.addStock(x,15);

        portfolio.removeStock(x,5);
        Hashtable hashtableP = portfolio.getPortfolio();
        assertEquals(10, hashtableP.get(x));

        portfolio.removeStock(x,10);
        Hashtable hashtableP2 = portfolio.getPortfolio();
        assertEquals(null, hashtableP2.get(x));
    }

    @Test
    public void TestIncorrectRemoveStock(){
        portfolio.addStock(x,10);

        // y does not exist in portflio
        portfolio.removeStock(y,10);

        Hashtable hashtableP = portfolio.getPortfolio();
        assertEquals(1, hashtableP.size());
        assertEquals(10, hashtableP.get(x));
    }

    @Test
    public void TestIncorrectRemoveQuantity(){
        portfolio.addStock(x,10);

        portfolio.removeStock(x,15);

        Hashtable hashtableP = portfolio.getPortfolio();
        assertEquals(10, hashtableP.get(x));
    }

}
