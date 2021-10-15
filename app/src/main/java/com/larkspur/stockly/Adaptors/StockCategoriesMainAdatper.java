package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.content.Intent;
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

public class StockCategoriesMainAdatper extends RecyclerView.Adapter<StockCategoriesMainAdatper.ViewHolder> {


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
            Toast.makeText(_context, stock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    private List<IStock> _stockList;
    private Context _context;

    public StockCategoriesMainAdatper(List<IStock> stockList){
        _stockList = stockList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        _context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(_context);
        View stockView = inflater.inflate(R.layout.main_category_card, parent, false);
        ViewHolder holder = new ViewHolder(stockView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IStock stock = _stockList.get(position);
        holder._stockSymbol.setText(stock.getSymbol());
        holder._stockPercent.setText("RANDOM PERCENT");

        //cut price to two decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedPrice = df.format(stock.getPrice());
        holder._stockPrice.setText(formattedPrice);
    }

    @Override
    public int getItemCount() {
        return _stockList.size();
    }

}
