package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.larkspur.stockly.Activities.MainActivity;
import com.larkspur.stockly.Activities.DetailsActivity;
import com.larkspur.stockly.Activities.utils.LineChartHandler;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.List;

public class TopChangeAdapter extends RecyclerView.Adapter<TopChangeAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView _name, _symbol, _price;
        LineChart _chart;
        CardView _status;
        private Class _parent;

        public ViewHolder(@NonNull View view) {
            super(view);
            _parent = view.getContext().getClass();
            view.setOnClickListener(this);

//            _topGainer = findViewById(R.id.top_gainer);
            _name = (TextView) view.findViewById(R.id.stock_name);
            _symbol = (TextView) view.findViewById(R.id.stock_symbol);
            _price = (TextView) view.findViewById(R.id.stock_price);
            _chart = (LineChart) view.findViewById(R.id.chart1);
            _status = (CardView) view.findViewById(R.id.stock_status_card);
        }

        @Override
        public void onClick(View view) {
            IStock stock = _stockList.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), DetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("stock", stock);
            Log.e("stock",stock.getCompName());
            intent.putExtra("Screen", "Home");
            intent.putExtra("Class", MainActivity.class);
            IStock test = (IStock) bundle.getSerializable("stock");
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        }
    }


    private List<IStock> _stockList;
    private Context _context;

    public TopChangeAdapter(List<IStock> stocks){
        _stockList = stocks;
    }

    @NonNull
    @Override
    public TopChangeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        _context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(_context);
        View stockView = inflater.inflate(R.layout.main_view_top_change_stock, parent, false);

        TopChangeAdapter.ViewHolder holder = new TopChangeAdapter.ViewHolder(stockView);
        LineChartHandler.setupGraph(holder._chart,false, Color.BLACK);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TopChangeAdapter.ViewHolder holder, int position) {
        IStock stock = _stockList.get(position);
        holder._name.setText(stock.getCompName());
        holder._symbol.setText(stock.getSymbol());
        LineChartHandler.setData(stock.getHistoricalPrice(), holder._chart);
        Double changeSpan24hours = stock.getHistoricalPrice().getLastWeekChange();
        String princeChange = "$" + String.format("%.2f", stock.getPrice()) + " "
                + String.format("%.2f", changeSpan24hours) + "%";
        holder._price.setText(princeChange);
        if (changeSpan24hours > 0){
            holder._price.setTextColor(Color.rgb(17, 168, 253));
            holder._status.setCardBackgroundColor(Color.rgb(17, 168, 253));
        }else{
            holder._price.setTextColor(Color.rgb(255, 24, 24));
            holder._status.setCardBackgroundColor(Color.rgb(255, 24, 24));
        }
    }

    @Override
    public int getItemCount() {
        return _stockList.size();
    }
}
