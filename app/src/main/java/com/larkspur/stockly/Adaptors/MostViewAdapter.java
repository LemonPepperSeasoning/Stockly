package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.larkspur.stockly.Activities.StockActivity;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.List;

public class MostViewAdapter extends RecyclerView.Adapter<MostViewAdapter.ViewHolder>{


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _stockSymbol, _stockPrice;

        public ViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);
            _stockSymbol = (TextView) view.findViewById(R.id.stock_name_view);
            _stockPrice = (TextView) view.findViewById(R.id.stock_price);
        }

        @Override
        public void onClick(View view) {
            IStock stock = _stockList.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), StockActivity.class);
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

    private List<IStock> _stockList;
    private Context _context;

    public MostViewAdapter(List<IStock> stockList){
        _stockList = stockList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        _context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(_context);

        View stockView = inflater.inflate(R.layout.stock_most_viewed_recycler_view, parent, false);

        ViewHolder holder = new ViewHolder(stockView);
        System.out.println("===== MOSTVIEWADAPTER ======");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        IStock stock = _stockList.get(position);

        holder._stockSymbol.setText(stock.getSymbol());
        holder._stockPrice.setText((stock.getPrice().toString()));

    }

    @Override
    public int getItemCount() {
        return _stockList.size();
    }

}

