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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.larkspur.stockly.Activities.StockActivity;
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
            IStock stock = _stockList.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), StockActivity.class);
            intent.putExtra("Screen", "Home");
            intent.putExtra("Class", _parent);
            System.out.println("serializing stock");
            Bundle bundle = new Bundle();
            bundle.putSerializable("stock", stock);
            System.out.println(bundle.getSerializable("stock"));
            IStock test = (IStock) bundle.getSerializable("stock");
//            System.out.println(
//
//
//            .getCompName());
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
            Toast.makeText(_context, stock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    private List<IStock> _stockListAll;
    private List<IStock> _stockList;
    private Context _context;
    private ViewGroup _parent;

    public SearchAdapter(List<IStock> stockList){
        _stockListAll = stockList;
        _stockList = stockList;
    }

    public void addData(List<IStock> stockList){
        _stockListAll = stockList;
        _stockList = stockList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        _parent = parent;
        _context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(_context);

        View stockView = inflater.inflate(R.layout.search_result_component, parent, false);

        ViewHolder holder = new ViewHolder(stockView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IStock stock = _stockList.get(position);

        holder._stockSymbol.setText(stock.getSymbol());
        holder._stockPrice.setText((stock.getPrice().toString()));
        holder._stockName.setText(stock.getCompName());
    }

    @Override
    public int getItemCount() {
        return _stockList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                _stockList = (List<IStock>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<IStock> filteredResults = null;
                Log.d("+++++++++++++++++++++++++++",constraint.toString());
                if (constraint.length() == 0) {
                    filteredResults = _stockListAll;
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

        for (IStock item : _stockListAll) {
            if (item.getCompName().toLowerCase().contains(constraint) ||
                    item.getSymbol().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        _stockList.clear();
        if (charText.length() == 0) {
            _stockList.addAll(_stockListAll);
        } else {
            for (IStock stock : _stockListAll) {
                if (stock.getCompName().toLowerCase(Locale.getDefault()).contains(charText)
                || stock.getSymbol().toLowerCase(Locale.getDefault()).contains(charText) ) {
                    _stockList.add(stock);
                }
            }
        }
        notifyDataSetChanged();
    }
}
