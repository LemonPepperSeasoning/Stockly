package com.larkspur.stockly.models;

public interface IUser {

    /**
     * Set the username of the user.
     * @param username : Representing the username
     */
    void setUsername(String username);

    /**
     * Get the username of the user.
     * @return String : Representing the username
     */
    String getUsername();

    /**
     * Get the portfolio of the user
     * @return IPortfolio : representing the portfolio object of the user.
     */
    IPortfolio getPortfolio();

    /**
     * Get the watchlist of the user
     * @return IWatchlist : representing the watchlist object of the user.
     */
    IWatchlist getWatchlist();
}
