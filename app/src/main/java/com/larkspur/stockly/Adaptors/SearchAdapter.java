package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.larkspur.stockly.Activities.DetailsActivity;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _stockSymbol, _stockPrice, _stockName;

        private Class _parent;

        public ViewHolder(@NonNull View view) {
            super(view);
            _parent = view.getContext().getClass();

            view.setOnClickListener(this);
            _stockSymbol = (TextView) view.findViewById(R.id.stock_symbol_view);
            _stockPrice = (TextView) view.findViewById(R.id.stock_price_view);
            _stockName = (TextView) view.findViewById(R.id.stock_name_view);
        }



        @Override
        public void onClick(View view) {
            IStock stock = _searchResult.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), DetailsActivity.class);
            intent.putExtra("Screen", "Home");
            intent.putExtra("Class", _parent);
            System.out.println("serializing stock");
            Bundle bundle = new Bundle();
            bundle.putSerializable("stock", stock);
            System.out.println(bundle.getSerializable("stock"));
            IStock test = (IStock) bundle.getSerializable("stock");


            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        }
    }

    List<IStock> _searchContext;
    List<IStock> _searchResult;
    private Context _context;

    public SearchAdapter(List<IStock> searchContext, List<IStock> searchResult) {
        _searchContext = searchContext;
        _searchResult = searchResult;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        _context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(_context);

        View stockView = inflater.inflate(R.layout.list_item, parent, false);

        ViewHolder holder = new ViewHolder(stockView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IStock currentStock = _searchResult.get(position);
        holder._stockName.setText(currentStock.getCompName());
        holder._stockSymbol.setText(currentStock.getSymbol());
        //cut price to two decimal places
        double percentChange = currentStock.getHistoricalPrice().getLast24HourChange();
        holder._stockPrice.setText("$" + String.format("%.2f", currentStock.getPrice()) + " +" + String.format("%.2f", percentChange) + "%");

        if (percentChange < 0) {
            holder._stockPrice.setText("$" + String.format("%.2f", currentStock.getPrice()) + " " + String.format("%.2f", percentChange) + "%");//
            holder._stockPrice.setTextColor(Color.RED);
        } else {
            holder._stockPrice.setTextColor(_context.getResources().getColor(R.color.colorPrimaryBlue));
        }

    }

    @Override
    public int getItemCount() {
        return _searchResult.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                _searchResult = (List<IStock>) results.values; //TODO : What is this?
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<IStock> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = _searchResult;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }
        };
    }

    protected List<IStock> getFilteredResults(String constraint) {
        List<IStock> results = new ArrayList<>();
        for (IStock item : _searchContext) {
            if (item.getCompName().toLowerCase().contains(constraint) ||
                    item.getSymbol().toLowerCase().contains(constraint) ||
                    item.getSubindustry().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }
}