package com.larkspur.stockly.Models;

public interface IUser {

    void setUsername(String username);

    String getUsername();

    IPortfolio getPortfolio();

    IWatchlist getWatchlist();
}
