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
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.R;

import java.util.List;

/**
 * Basic adaptor for Stock item
 * Author: Takahiro & Alan
 */
public class BasicStockAdapter extends ArrayAdapter {

    private class ViewHolder {
        TextView _stockName, _stockSymbol, _stockPrice;
        LinearLayout _stockDetails;

        public ViewHolder(View currentListViewItem) {
            _stockName = currentListViewItem.findViewById(R.id.stock_name_view);
            _stockSymbol = currentListViewItem.findViewById(R.id.stock_symbol_view);
            _stockPrice = currentListViewItem.findViewById(R.id.stock_price_view);
            _stockDetails = currentListViewItem.findViewById(R.id.stock_details_view);
        }

    }


    private class WatchlistViewHolder extends ViewHolder {
        LinearLayout _removeStock;


        public WatchlistViewHolder(View currentListViewItem) {
            super(currentListViewItem);
            _removeStock = currentListViewItem.findViewById(R.id.remove_item_view);

        }
    }

    private class ListViewHolder extends ViewHolder {

        public ListViewHolder(View currentListViewItem) {
            super(currentListViewItem);
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
    public BasicStockAdapter(@NonNull Context context, int resource, List<IStock> objects) {
        super(context, resource, objects);
        _layoutID = resource;
        _context = context;
        _stocks = objects;
    }

    /**
     * Uses layoutInflater to initialise the cardView in listview and populates the card fields
     * with stock information using populateList
     * @param position position of stock in list
     * @param convertView the listView item you wish to create dynamically
     * @param parent the layout in which the listView item is created
     * @return CardViews inside the ListView
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentListViewItem = convertView;

        if (currentListViewItem == null) {
            currentListViewItem = LayoutInflater.from(getContext()).inflate(_layoutID, parent, false);
        }
        IStock currentStock = _stocks.get(position);
        System.out.println(parent.getContext().getClass().getSimpleName());
        if (parent.getContext().getClass().getSimpleName().equals("ListActivity") || parent.getContext().getClass().getSimpleName().equals("WatchlistActivity")) {

            return populateListItem(currentStock, currentListViewItem);

        } else {
            return null;
        }

    }
    /**
     * Populates the CardView items with data and implements click functionality for the CardViews
     * inside the listView
     * @param currentStock current stock being populated in the CardView
     * @param currentListView current cardView inside listView
     * @return CardView inside the ListView with information inside them
     */
    private View populateListItem(IStock currentStock, View currentListView) {
        ViewHolder vh = null;
        String parentClass = currentListView.getContext().getClass().getSimpleName();

        System.out.println("parent class is called: \n");
        System.out.println(parentClass);

        if (parentClass.equals("WatchlistActivity")) {
            vh = new WatchlistViewHolder(currentListView);

        } else if (parentClass.equals("ListActivity")) {

            vh = new ListViewHolder(currentListView);

        }

        vh._stockName.setText(currentStock.getCompName());
        vh._stockSymbol.setText(currentStock.getSymbol());
        //cut price to two decimal places
        double percentChange = currentStock.getHistoricalPrice().getLast24HourChange();
        vh._stockPrice.setText("$" + String.format("%.2f", currentStock.getPrice()) + " +" + String.format("%.2f", percentChange) + "%");

        if (percentChange < 0) {
            vh._stockPrice.setText("$" + String.format("%.2f", currentStock.getPrice()) + " " + String.format("%.2f", percentChange) + "%");//
            vh._stockPrice.setTextColor(Color.RED);
        } else {
            vh._stockPrice.setTextColor(_context.getResources().getColor(R.color.colorPrimaryBlue));
        }


        if (parentClass.equals("WatchlistActivity")) {
            WatchlistViewHolder watchlistVH = (WatchlistViewHolder) vh;
            vh._stockDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                    intent.putExtra("Screen", "Watchlist");
                    intent.putExtra("Class", _context.getClass());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("stock", currentStock);
                    IStock test = (IStock) bundle.getSerializable("stock");

                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                    Activity activity = (Activity) v.getContext();
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    Toast.makeText(_context, currentStock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();
                }
            });

            watchlistVH._removeStock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    IWatchlist watchlist = User.getInstance().getWatchlist();
                    watchlist.removeStock(currentStock);
                    _stocks.remove(currentStock);
                    notifyDataSetChanged();
                }
            });
        } else if ((parentClass.equals("ListActivity"))) {

            vh._stockDetails.setOnClickListener(new View.OnClickListener() {
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
                    Activity activity = (Activity) v.getContext();
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    Toast.makeText(_context, currentStock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();

                }
            });
        }

        return currentListView;

    }


}
