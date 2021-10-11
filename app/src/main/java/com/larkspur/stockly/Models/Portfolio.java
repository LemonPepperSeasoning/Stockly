package com.larkspur.stockly.Models;


import java.util.Hashtable;

public class Portfolio implements IPortfolio{

    private Hashtable<IStock,Integer> _portfolio;

    public Portfolio(){
        _portfolio = new Hashtable<>();
    }

    @Override
    public void addStock(IStock stock, int quantity) {

        Integer cQuantity = _portfolio.get(stock);

        if (cQuantity == null) {
            _portfolio.put(stock, quantity);
        } else {
            _portfolio.put(stock, cQuantity + quantity);
        }
    }

    @Override
    public void removeStock(IStock stock, int quantity) {

        Integer cQuantity = _portfolio.get(stock);

        if (cQuantity == null) {
            //TODO : throw some type of Exception - No stock in portfolio
        } else if ( cQuantity - quantity < 0) {
            //TODO : throw exception - No quantity in portfolio
        } else {
            _portfolio.put(stock, cQuantity - quantity);
        }
    }

    @Override
    public Hashtable<IStock, Integer> getPortfolio() {
        return _portfolio;
    }
}
