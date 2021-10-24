package com.larkspur.stockly.models;

public interface IUser {

    void setUsername(String username);

    String getUsername();

    IPortfolio getPortfolio();

    IWatchlist getWatchlist();
}
