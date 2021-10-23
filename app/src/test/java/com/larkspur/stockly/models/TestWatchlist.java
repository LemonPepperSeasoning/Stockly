package com.larkspur.stockly.models;

import static org.junit.Assert.assertEquals;

import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.Watchlist;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestWatchlist {

    IWatchlist watchlist;
    IStock x;
    IStock y;
    IStock z;

    @Before
    public void setupPortfolio(){
        watchlist = new Watchlist();
        watchlist.removeAllStocks();

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

        List<String> imageLinkGoogle = new ArrayList<>();
        List<Double> pricesGoogle = new ArrayList<>();
        HistoricalPrice historicalPriceGoogle = new HistoricalPrice(pricesGoogle);
        z = new Stock("Alphabet", "GOOGL", Category.CommunicationServices,
                "Interactive Media & Services", "Mount view Califonia",
                "some long desciption of google, this is just a place holder text",imageLinkGoogle,
                historicalPriceGoogle);
    }

    @Test
    public void TestCorrectAdd(){
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        watchlist.addStock(y);

        assertEquals(2, watchlist.getWatchlist().size());
    }

    @Test
    public void TestIncorrectAdd(){
        assertEquals(0, watchlist.getWatchlist().size());

        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        // Adding duplicate stock
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());
    }

    @Test
    public void TestCorrectRemove(){
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        watchlist.removeStock(x);

        assertEquals(0, watchlist.getWatchlist().size());
    }

    @Test
    public void TestIncorrectRemove(){
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        // trying to remove something that is not in watchlist
        watchlist.removeStock(y);

        assertEquals(1, watchlist.getWatchlist().size());
    }

    @Test
    public void testClearAllStocks(){
        watchlist.addStock(x);

        assertEquals(1, watchlist.getWatchlist().size());

        watchlist.addStock(y);

        assertEquals(2, watchlist.getWatchlist().size());

        watchlist.addStock(z);

        assertEquals(3, watchlist.getWatchlist().size());

        watchlist.removeAllStocks();

        assertEquals(0, watchlist.getWatchlist().size());


    }
}

