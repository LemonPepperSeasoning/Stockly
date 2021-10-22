package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.larkspur.stockly.Activities.MainActivity;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.Portfolio;
import com.larkspur.stockly.R;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Basic adaptor for Stock item
 * Author: Takahiro
 */
public class StockAdaptor extends ArrayAdapter{

    private class ViewHolder{
        TextView _stockName, _stockSymbol, _stockPrice;
        LinearLayout _removeStock;

        public ViewHolder(View currentListViewItem){
            _stockName = currentListViewItem.findViewById(R.id.stock_name_view);
            _stockSymbol = currentListViewItem.findViewById(R.id.stock_symbol_view);
            _stockPrice = currentListViewItem.findViewById(R.id.stock_price);
            _removeStock = currentListViewItem.findViewById(R.id.remove_item_view);
        }


        private class PortfolioViewHolder extends ViewHolder{
            TextView  _stockTotalPrice, _quantityStock;
            CardView _stockColor;
            LinearLayout _stockSide;

            public PortfolioViewHolder(View currentListViewItem){
                super(currentListViewItem);

                _stockTotalPrice = currentListViewItem.findViewById(R.id.stock_total_price);
                _quantityStock = currentListViewItem.findViewById(R.id.stock_quantity);
                _stockSide = currentListViewItem.findViewById(R.id.stock_view);
                _stockColor = currentListViewItem.findViewById(R.id.status_view);

            }

        }


    }


    int _layoutID;
    Context _context;
    private List<IStock> _stocks;

    public StockAdaptor(@NonNull Context context, int resource, List<IStock> objects) {
        super(context, resource, objects);
        _layoutID = resource;
        _context = context;
        _stocks = objects;
    }
}
