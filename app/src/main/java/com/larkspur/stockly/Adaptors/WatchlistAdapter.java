package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.util.List;

public class WatchlistAdapter extends ArrayAdapter {

    private class ViewHolder {
        TextView _stockName, _stockSymbol, _stockPrice;
        LinearLayout _removeStock;


        public ViewHolder(View currentListViewItem) {
            _stockName = currentListViewItem.findViewById(R.id.stock_name_view);
            _stockSymbol = currentListViewItem.findViewById(R.id.stock_symbol_view);
            _stockPrice = currentListViewItem.findViewById(R.id.stock_price_view);
            _removeStock = currentListViewItem.findViewById(R.id.remove_item_view);
        }
    }

    int _layoutID;
    Context _context;
    private List<IStock> _stocks;

    public WatchlistAdapter(@NonNull Context context, int resource, @NonNull List<IStock> objects) {
        super(context, resource, objects);
        _layoutID = resource;
        _context = context;
        _stocks = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentListViewItem = convertView;
        if (currentListViewItem == null) {
            currentListViewItem = LayoutInflater.from(getContext()).inflate(_layoutID, parent, false);
        }

        IStock currentStock = _stocks.get(position);



        return populateWatchList(currentStock, currentListViewItem);
    }

    private View populateWatchList(IStock currentStock, View currentListView){
        System.out.println("stock list size is " + _stocks.size());

        ViewHolder vh = new ViewHolder(currentListView);

//        int i = _context.getResources().getIdentifier(currentItem.getCompName(),"drawable",_context.getPackageName());

        vh._stockName.setText(currentStock.getCompName());
        vh._stockSymbol.setText(currentStock.getSymbol());
        vh._stockPrice.setText(currentStock.getPrice().toString());

        vh._removeStock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                IWatchlist watchlist = Watchlist.getInstance();
                watchlist.removeStock(currentStock);
                _stocks.remove(currentStock);
                notifyDataSetChanged();
            }
        });

        return currentListView;
    }
}
