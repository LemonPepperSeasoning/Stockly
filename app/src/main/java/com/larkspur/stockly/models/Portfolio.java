package com.larkspur.stockly.models;


import java.util.Hashtable;

/**
 * This class represents the portfolio and handles all functionality relating to portfolio.
 * Author: Takahiro
 */
public class Portfolio implements IPortfolio{

    private Hashtable<String,IStock> _stocks;
    private Hashtable<String,Integer> _quantity;

    public Portfolio() {
        _quantity = new Hashtable<>();
        _stocks = new Hashtable<>();
    }

    public void addStock(IStock stock, int quantity) {

        Integer cQuantity = _quantity.get(stock.getSymbol());

        if (cQuantity == null) {
            _quantity.put(stock.getSymbol(), quantity);
            _stocks.put(stock.getSymbol(),stock);
        } else {
            _quantity.put(stock.getSymbol(), cQuantity + quantity);
        }
    }

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

    public Hashtable<IStock, Integer> getPortfolio() {
        Hashtable<IStock,Integer> x = new Hashtable<>();
        for (String s : _quantity.keySet()){
            x.put(_stocks.get(s),_quantity.get(s));
        }
        return x;
    }

    public void removeAllStocks() {
        _stocks.clear();
        _quantity.clear();
    }

    public int getQuantity(String stockSymbol) {
        try{
            int x = _quantity.get(stockSymbol);
            return x;
        }catch(NullPointerException e){
            return -1;
        }
    }

    public Double getTotalValue(){
        Double total = 0.0;
        for (String s : _stocks.keySet()){
            total += _stocks.get(s).getPrice() * _quantity.get(s);
        }
        return total;
    }

    public Double getYesterdaysValue(){
        Double total = 0.0;
        for (String s : _stocks.keySet()){
            total += _stocks.get(s).getHistoricalPrice().getYesterdaysPrice() * _quantity.get(s);
        }
        return total;
    }

    public Double getTotal24HrChange(){

        Double todayValue = getTotalValue();
        Double yesterday = getYesterdaysValue();
        Double change = (todayValue-yesterday)/yesterday;
        return change*100;
    }

}
