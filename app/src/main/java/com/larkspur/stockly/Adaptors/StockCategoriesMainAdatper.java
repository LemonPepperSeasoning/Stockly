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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.larkspur.stockly.Activities.StockActivity;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * This adaptor was used for an old layout for the main screen. (Maybe delete this.)
 * Author: Alan
 */
public class StockCategoriesMainAdatper extends RecyclerView.Adapter<StockCategoriesMainAdatper.ViewHolder> {

    /**
     * Represents every item in the screen and displays each one.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _stockSymbol, _stockPrice, _stockPercent;
        private Class _parent;

        public ViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);
            _parent = view.getContext().getClass();
            _stockSymbol = (TextView) view.findViewById(R.id.stock_symbol);
            _stockPrice = (TextView) view.findViewById(R.id.stock_price);
            _stockPercent = (TextView) view.findViewById(R.id.stock_percent);
        }

        @Override
        public void onClick(View view) {
            IStock stock = _stockList.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), StockActivity.class);
            intent.putExtra("Screen", "Home");
            intent.putExtra("Class", _parent);
            Bundle bundle = new Bundle();
            bundle.putSerializable("stock", stock);
            IStock test = (IStock) bundle.getSerializable("stock");

            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
            Activity activity =(Activity) view.getContext();
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Toast.makeText(_context, stock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    private List<IStock> _stockList;
    private Context _context;

    /**
     * Default constructor
     * @param stockList List of stocks
     */
    public StockCategoriesMainAdatper(List<IStock> stockList){
        _stockList = stockList;
    }

    /**
     * Creates a ViewHolder for the TextViews for the categories titles once the main screen
     * is launched
     * @param parent layout in which the TextViews are held
     * @param viewType view type
     * @return VIewHolder holding the TextViews
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        _context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(_context);
        View stockView = inflater.inflate(R.layout.main_category_card, parent, false);
        ViewHolder holder = new ViewHolder(stockView);
        return holder;
    }

    /**
     * Updates the ViewHolder's contents for the TextViews
     * @param holder ViewHolder for the TextViews
     * @param position position in stock list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IStock stock = _stockList.get(position);
        holder._stockSymbol.setText(stock.getSymbol());
        holder._stockPrice.setText("$" + String.format("%.2f", stock.getPrice()));
        holder._stockPercent.setText(String.format("%.2f", stock.getHistoricalPrice().getLast24HourChange())+"%");
        if (stock.getHistoricalPrice().getLast24HourChange() > 0 ){
            holder._stockPrice.setTextColor(Color.GREEN);
            holder._stockPercent.setTextColor(Color.GREEN);
        }else{
            holder._stockPrice.setTextColor(Color.RED);
            holder._stockPercent.setTextColor(Color.RED);
        }
    }

    /**
     * Get the number of categories
     * @return number of categories
     */
    @Override
    public int getItemCount() {
        return _stockList.size();
    }

}
