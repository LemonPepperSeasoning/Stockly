package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.larkspur.stockly.Models.IStock;
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
    private List<IStock> _stockNamesList = null;
    private List<IStock> _arraylist;

    public SearchListViewAdaptor(Context context, List<IStock> stockNamesList) {
        mContext = context;
        _stockNamesList = stockNamesList;
        inflater = LayoutInflater.from(mContext);
        _arraylist = new ArrayList<IStock>();
        _arraylist.addAll(stockNamesList);
    }

    public class ViewHolder {
        TextView name;
    }

    public void addData(List<IStock> data){
        _stockNamesList = data;
        _arraylist = new ArrayList<IStock>();
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
        holder.name.setText(_stockNamesList.get(position).getCompName());
        return view;
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

    public void printData(){
        for (IStock stock : _stockNamesList) {
            Log.d("", stock.getCompName());
        }
    }

}

