package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.larkspur.stockly.Activities.Search.StockNames;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class is the adaptor for the search list.
 * Author: Jonathon
 * Source: https://abhiandroid.com/ui/searchview
 *
 */

public class SearchListViewAdaptor extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<StockNames> _stockNamesList = null;
    private ArrayList<StockNames> _arraylist;

    public SearchListViewAdaptor(Context context, List<StockNames> stockNamesList) {
        mContext = context;
        _stockNamesList = stockNamesList;
        inflater = LayoutInflater.from(mContext);
        _arraylist = new ArrayList<StockNames>();
        _arraylist.addAll(stockNamesList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return _stockNamesList.size();
    }

    @Override
    public StockNames getItem(int position) {
        return _stockNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.search_list_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(_stockNamesList.get(position).getStockName());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        _stockNamesList.clear();
        if (charText.length() == 0) {
            _stockNamesList.addAll(_arraylist);
        } else {
            for (StockNames wp : _arraylist) {
                if (wp.getStockName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    _stockNamesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}