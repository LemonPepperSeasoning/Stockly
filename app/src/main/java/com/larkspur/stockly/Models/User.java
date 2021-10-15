package com.larkspur.stockly.Models;

import java.util.HashMap;
import java.util.Map;

public class User implements IUser{

    private static IUser single_instance = null;

    private String _username;
    private IPortfolio _portfolio;
    private IWatchlist _watchlist;

    private User() {
        _username = "Guest";
        _portfolio = new Portfolio();
        _watchlist = new Watchlist();
    }

    public static IUser getInstance() {
        if (single_instance == null) {
            single_instance = new User();
        }
        return single_instance;
    }

    public void setUsername(String username){ _username = username; }

    public String getUsername(){ return _username; }

    public IPortfolio getPortfolio(){ return _portfolio; }

    public IWatchlist getWatchlist(){ return _watchlist; }
}
