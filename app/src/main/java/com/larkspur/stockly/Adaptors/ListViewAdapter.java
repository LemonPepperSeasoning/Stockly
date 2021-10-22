package com.larkspur.stockly.Adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.larkspur.stockly.Activities.DetailsActivity;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.List;

/**
 * This adaptor loads the cardViews in the listView in ListScreen.
 * Author: Alan Lin
 */
public class ListViewAdapter extends ArrayAdapter {

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        TextView _stockName, _stockSymbol, _stockPrice;
        LinearLayout _stockView;

        public ViewHolder(View currentListView) {
            _stockName = currentListView.findViewById(R.id.stock_name_view);
            _stockSymbol = currentListView.findViewById(R.id.stock_symbol_view);
            _stockPrice = currentListView.findViewById(R.id.stock_price_view);
            _stockView = currentListView.findViewById(R.id.stock_details_view);
        }
    }

    int _layoutID;
    Context _context;
    private List<IStock> _stocks;

    /**
     * Default constructor
     * @param context this information required to access the xml files
     * @param resource the resource id for a layout file containing the relevant ListView
     * @param objects stock data objects list
     */
    public ListViewAdapter(@NonNull Context context, int resource, @NonNull List<IStock> objects) {
        super(context, resource, objects);
        _layoutID = resource;
        _context = context;
        _stocks = objects;
    }

    /**
     * Uses layoutInflater to initialise the cardView in listview and populates the card fields
     * with stock information using populateList
     * @param position position in list
     * @param convertView the listView item you wish to create dynamically
     * @param parent the layout in which the listView item is created
     * @return CardViews inside the ListView
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentListViewItem = convertView;
        System.out.println("listview text");
        System.out.println(convertView);
        if (currentListViewItem == null) {
            currentListViewItem = LayoutInflater.from(getContext()).inflate(_layoutID, parent, false);
        }
        IStock currentStock = _stocks.get(position);

        return populateList(currentStock, currentListViewItem);
    }

    /**
     * Populates the stock items with data and implements click functionality for the CardViews
     * inside the listView
     * @param currentStock current stock being populated in the CardView
     * @param currentListView current cardView inside listView
     * @return CardView inside the ListView with information inside them
     */
    private View populateList(IStock currentStock, View currentListView) {
        System.out.println("stock list size is " + _stocks.size());

        ViewHolder vh = new ViewHolder(currentListView);

        vh._stockName.setText(currentStock.getCompName());
        vh._stockSymbol.setText(currentStock.getSymbol());

        double percentChange = currentStock.getHistoricalPrice().getLast24HourChange();
        vh._stockPrice.setText("$" + String.format("%.2f", currentStock.getPrice()) + " +" + String.format("%.2f", percentChange) + "%");

        if(percentChange < 0){
            vh._stockPrice.setText("$" + String.format("%.2f", currentStock.getPrice()) + " " + String.format("%.2f", percentChange) + "%");//
            vh._stockPrice.setTextColor(Color.RED);
        }else{
            vh._stockPrice.setTextColor(_context.getResources().getColor(R.color.colorPrimaryBlue));
        }

        vh._stockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                System.out.println("serializing stock");
                Bundle bundle = new Bundle();
                bundle.putSerializable("stock", currentStock);
                System.out.println(bundle.getSerializable("stock"));
                IStock test = (IStock) bundle.getSerializable("stock");
                System.out.println(test.getCompName());

                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
                Activity activity =(Activity) v.getContext();
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Toast.makeText(_context, currentStock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();

            }
        });


        return currentListView;
    }
}
