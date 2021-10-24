package com.larkspur.stockly.adaptors;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.larkspur.stockly.activities.DetailsActivity;
import com.larkspur.stockly.models.IStock;
import com.larkspur.stockly.models.IWatchlist;
import com.larkspur.stockly.models.User;
import com.larkspur.stockly.R;

import java.util.List;

/**
 * Basic adaptor for Stock item
 * Currently used by Watchlist and list activity, as they share many of the same components and code. 
 * Author: Takahiro & Alan
 */
public class BasicStockAdapter extends ArrayAdapter {

    /**
     * contains all shared elements between watchlist and listview items
     */
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

    /**
     * initiates the watchlist view holder with an additional component - the remove stock button.
     */
    private class WatchlistViewHolder extends ViewHolder {
        LinearLayout _removeStock;


        public WatchlistViewHolder(View currentListViewItem) {
            super(currentListViewItem);
            _removeStock = currentListViewItem.findViewById(R.id.remove_item_view);

        }
    }
    /**
     * initiates the list view holder currently ,there are no additional components.
     */
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
        //if the current class is a ListActivity or a WatchListActivity
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
            //ensures sent intent is related to the watchlist
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
                }
            });
            //add the remove button functionality

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
            //ensures sent intent is related to the list activity

            vh._stockDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("stock", currentStock);

                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                    Activity activity = (Activity) v.getContext();
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }
            });
        }

        return currentListView;

    }


}