package com.larkspur.stockly.Models;


import java.util.Hashtable;
import java.util.List;

public class Portfolio implements IPortfolio{

    private static Portfolio single_instance = null;

    private Hashtable<String,IStock> _stocks;
    private Hashtable<String,Integer> _quantity;

    private Portfolio() {
        _quantity = new Hashtable<>();
        _stocks = new Hashtable<>();
    }

    public static Portfolio getInstance() {
        if (single_instance == null) {
            single_instance = new Portfolio();
        }
        return single_instance;
    }

    @Override
    public void addStock(IStock stock, int quantity) {

        Integer cQuantity = _quantity.get(stock.getSymbol());

        if (cQuantity == null) {
            _quantity.put(stock.getSymbol(), quantity);
            _stocks.put(stock.getSymbol(),stock);
        } else {
            _quantity.put(stock.getSymbol(), cQuantity + quantity);
        }
    }

    @Override
    public void removeStock(IStock stock, int quantity) {

        Integer cQuantity = _quantity.get(stock.getSymbol());

        if (cQuantity == null) {
            //TODO : throw some type of Exception - No stock in portfolio
        } else if ( cQuantity - quantity < 0) {
            //TODO : throw exception - No quantity in portfolio
        } else {
            if (cQuantity - quantity == 0){
                _quantity.remove(stock.getSymbol());
                _stocks.remove(stock.getSymbol());
            }else{
                _quantity.put(stock.getSymbol(), cQuantity - quantity);
            }
        }
    }

    @Override
    public Hashtable<IStock, Integer> getPortfolio() {
        Hashtable<IStock,Integer> x = new Hashtable<>();
        for (String s : _quantity.keySet()){
            x.put(_stocks.get(s),_quantity.get(s));
        }
        return x;
    }

    @Override
    public void removeAllStocks() {
        _stocks.clear();
        _quantity.clear();
    }
}
