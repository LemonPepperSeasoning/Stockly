package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.larkspur.stockly.Activities.StockActivity;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class is the adaptor for the search list.
 * Author: Jonathon
 * Reference: https://abhiandroid.com/ui/searchview
 */

public class SearchListViewAdaptor extends ArrayAdapter {

    // Declare Variables

    Context _context;
    int _layoutID;
    LayoutInflater inflater;
    private List<IStock> _stockNamesList = null;
    private List<IStock> _arraylist;

    public SearchListViewAdaptor(Context context, int resource, List<IStock> stockNamesList) {
        super(context, resource, stockNamesList);
        _context = context;
        _layoutID = resource;
        _stockNamesList = stockNamesList;
        inflater = LayoutInflater.from(_context);
        _arraylist = new ArrayList<IStock>();
        _arraylist.addAll(stockNamesList);
    }

    public class ViewHolder {
        public CardView _searchListCard;
        public TextView _stockSymbol, _stockName, _stockPrice;

        public ViewHolder(@NonNull View view) {
            _searchListCard = (CardView) view.findViewById(R.id.search_list_card);
            _stockSymbol = (TextView) view.findViewById(R.id.stock_symbol_view);
            _stockName = (TextView) view.findViewById(R.id.stock_name_view);
            _stockPrice = (TextView) view.findViewById(R.id.stock_price_view);

        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentListViewItem = convertView;
        if (currentListViewItem == null) {
            currentListViewItem = LayoutInflater.from(getContext()).inflate(_layoutID, parent, false);
        }

        IStock currentStock = _stockNamesList.get(position);


        return populateList(currentStock, currentListViewItem);
    }

    private View populateList(IStock currentStock, View currentListView) {
        System.out.println("stock list size is " + _stockNamesList.size());

        ViewHolder vh = new ViewHolder(currentListView);

        vh._stockName.setText(currentStock.getCompName());
        vh._stockSymbol.setText(currentStock.getSymbol());

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedPrice = df.format(currentStock.getPrice());
        vh._stockPrice.setText(formattedPrice);

        vh._searchListCard.setOnClickListener(new View.OnClickListener() {
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

    public void addData(List<IStock> data){
        _stockNamesList = data;
        _arraylist = new ArrayList<IStock>(); // create new object to avoid pointing to same object
        _arraylist.addAll(data);


    }

    @Override
    public int getCount() {
        return _stockNamesList.size();
    }

    @Override
    public IStock getItem(int position) {
        return _stockNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    // Filter Class
    public void filter(String charText) {
        // change to lower case
        charText = charText.toLowerCase(Locale.getDefault());
        // clear all stocks
        _stockNamesList.clear();
        if (charText.length() == 0) {
            // _stockNamesList refers to the stocks which contain the search query
            // _arrayList refers to the all stocks
            _stockNamesList.addAll(_arraylist);
        } else {
            for (IStock wp : _arraylist) {
                if (wp.getCompName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    _stockNamesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

