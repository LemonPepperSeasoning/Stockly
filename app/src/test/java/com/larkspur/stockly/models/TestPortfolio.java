package com.larkspur.stockly.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.larkspur.stockly.Models.IPortfolio;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Portfolio;
import com.larkspur.stockly.Models.Stock;

import java.util.Hashtable;

public class TestPortfolio {

    IPortfolio portfolio;

    @Before
    public void setupPortfolio(){
        portfolio = new Portfolio();
        portfolio.removeAllStocks();
    }

    @Test
    public void TestCorrectAdd(){
        IStock x = new Stock(1, "apple","AAPL");
        portfolio.addStock(x,10);

        IStock y = new Stock(1, "amazon","AMZN");
        portfolio.addStock(y,20);

        Hashtable hashtableP = portfolio.getPortfolio();
        assertEquals(2, hashtableP.size());
        assertEquals(10, hashtableP.get(x));
        assertEquals(20, hashtableP.get(y));
    }

    @Test
    public void TestCorrectRemove(){
        IStock x = new Stock(1, "apple","AAPL");
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
        IStock x = new Stock(1, "apple","AAPL");
        portfolio.addStock(x,10);

        IStock y = new Stock(1, "amazon","AMZN");
        // y does not exist in portflio
        portfolio.removeStock(y,10);

        Hashtable hashtableP = portfolio.getPortfolio();
        assertEquals(1, hashtableP.size());
        assertEquals(10, hashtableP.get(x));
    }

    @Test
    public void TestIncorrectRemoveQuantity(){
        IStock x = new Stock(1, "apple","AAPL");
        portfolio.addStock(x,10);

        portfolio.removeStock(x,15);

        Hashtable hashtableP = portfolio.getPortfolio();
        assertEquals(10, hashtableP.get(x));
    }

}
