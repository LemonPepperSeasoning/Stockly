package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.larkspur.stockly.Activities.StockActivity;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter {

    private class ViewHolder {
        TextView _stockName, _stockSymbol, _stockPrice;
        LinearLayout _stockView;

        public ViewHolder(View currentListView) {
            _stockName = currentListView.findViewById(R.id.stock_name_view);
            _stockSymbol = currentListView.findViewById(R.id.stock_symbol_view);
            _stockPrice = currentListView.findViewById(R.id.stock_price_view);
            _stockView = currentListView.findViewById(R.id.stock_view);
        }
    }

    int _layoutID;
    Context _context;
    private List<IStock> _stocks;

    public ListViewAdapter(@NonNull Context context, int resource, @NonNull List<IStock> objects) {
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


        return populateList(currentStock, currentListViewItem);
    }

    private View populateList(IStock currentStock, View currentListView) {
        System.out.println("stock list size is " + _stocks.size());

        ViewHolder vh = new ViewHolder(currentListView);

        vh._stockName.setText(currentStock.getCompName());
        vh._stockSymbol.setText(currentStock.getSymbol());
        vh._stockPrice.setText(currentStock.getPrice().toString());

        vh._stockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StockActivity.class);
                System.out.println("serializing stock");
                Bundle bundle = new Bundle();
                bundle.putSerializable("stock", currentStock);
                System.out.println(bundle.getSerializable("stock"));
                IStock test = (IStock) bundle.getSerializable("stock");
                System.out.println(test.getCompName());

                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
                Toast.makeText(_context, currentStock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();

            }
        });


        return currentListView;
    }
}
