package com.larkspur.stockly.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.larkspur.stockly.activities.DetailsActivity;
import com.larkspur.stockly.models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter holds all the views for the SearchView. This is mainly for the recycler view
 * for displaying the search suggestions.
 * Author: Takahiro, Jonathon
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

    /**
     * Represents every item in the screen and displays each one.
     */
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

            Bundle bundle = new Bundle();
            bundle.putSerializable("stock", stock);

            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        }
    }

    List<IStock> _searchContext;
    List<IStock> _searchResult;
    private Context _context;
    private TextView _noResultsView;

    /**
     * Default constructor
     * @param searchContext the list of all stocks
     * @param searchResult the list of stocks shown to the user in the search suggestions
     * @param context this information required to access the xml files
     */
    public SearchAdapter(List<IStock> searchContext, List<IStock> searchResult, Context context) {
        _searchContext = searchContext;
        _searchResult = searchResult;
        _context = context;
        _noResultsView = ((Activity)_context).findViewById(R.id.no_results);
    }

    /**
     * Holds the views which are presented upon creation of the searchActivity screen.
     * This includes the listView and SearchView.
     * @param parent the layout in which the listView item is created
     * @param viewType the listview
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(_context);

        View stockView = inflater.inflate(R.layout.list_item, parent, false);

        ViewHolder holder = new ViewHolder(stockView);
        return holder;
    }

    /**
     * Holds the views which are created dynamically during the filtering process
     * @param holder ViewHolder
     * @param position position of stock in the list
     */
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

    /**
     * returns the size of the filtered search result List at the moment.
     * @return the size of the filtered search result list.
     */
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
                // reset the list
                _searchResult.clear();
                //add all the new results into referenced list
                _searchResult.addAll((List<IStock>) results.values);
                notifyDataSetChanged();

                if (_searchResult.size() <= 0) {

                    // Set visibility for text view to true.
                    _noResultsView.setVisibility(View.VISIBLE);
                }
                else {

                    // Hide the "no search results found" text.
                    _noResultsView.setVisibility(View.GONE);
                }
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