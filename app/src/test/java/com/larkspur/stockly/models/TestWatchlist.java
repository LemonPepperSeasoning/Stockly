package com.larkspur.stockly.models;

import static org.junit.Assert.assertEquals;

import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.Watchlist;

import org.junit.Before;
import org.junit.Test;

public class TestWatchlist {

    IWatchlist watchlist;

    @Before
    public void setupPortfolio(){
        watchlist = new Watchlist();
        watchlist.removeAllStocks();
    }

    @Test
    public void TestCorrectAdd(){
        IStock x = new Stock(1, "apple","AAPL");
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        IStock y = new Stock(1, "amazon","AMZN");
        watchlist.addStock(y);

        assertEquals(2, watchlist.getWatchlist().size());
    }

    @Test
    public void TestIncorrectAdd(){
        assertEquals(0, watchlist.getWatchlist().size());

        IStock x = new Stock(1, "apple","AAPL");
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        // Adding duplicate stock
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());
    }

    @Test
    public void TestCorrectRemove(){
        IStock x = new Stock(1, "apple","AAPL");
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        watchlist.removeStock(x);

        assertEquals(0, watchlist.getWatchlist().size());
    }

    @Test
    public void TestIncorrectRemove(){
        IStock x = new Stock(1, "apple","AAPL");
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        IStock y = new Stock(1, "amazon","AMZN");
        // trying to remove something that is not in watchlist
        watchlist.removeStock(y);

        assertEquals(1, watchlist.getWatchlist().size());
    }

    @Test
    public void testClearAllStocks(){
        IStock x = new Stock(1, "apple","AAPL");
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        IStock y = new Stock(1, "amazon","AMZN");
        watchlist.addStock(y);

        assertEquals(2, watchlist.getWatchlist().size());
        IStock z = new Stock(1, "google","GOOGL");
        watchlist.addStock(z);

        assertEquals(3, watchlist.getWatchlist().size());

        watchlist.removeAllStocks();

        assertEquals(0, watchlist.getWatchlist().size());


    }
}