package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.larkspur.stockly.Models.IPortfolio;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Portfolio;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class PorfolioAdapter extends ArrayAdapter {

    private class ViewHolder {
        TextView _stockName, _stockSymbol, _stockPrice, _stockTotalPrice, _quantityStock;
        LinearLayout _removeStock;

        public ViewHolder(View currentListViewItem) {
            _stockName = currentListViewItem.findViewById(R.id.stock_name);
            _stockSymbol = currentListViewItem.findViewById(R.id.stock_symbol);
            _stockPrice = currentListViewItem.findViewById(R.id.stock_price);
            _stockTotalPrice = currentListViewItem.findViewById(R.id.stock_total_price);
            _quantityStock = currentListViewItem.findViewById(R.id.stock_quantity);
            _removeStock = currentListViewItem.findViewById(R.id.remove_stock);
        }
    }

    int _layoutID;
    Context _context;
    private List<IStock> _stocks;
    private PieChart _chart;

    public PorfolioAdapter(@NonNull Context context, int resource, @NonNull List<IStock> objects, PieChart chart) {
        super(context, resource, objects);
        _layoutID = resource;
        _context = context;
        _stocks = objects;
        _chart = chart;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentListViewItem = convertView;
        if (currentListViewItem == null) {
            currentListViewItem = LayoutInflater.from(getContext()).inflate(_layoutID, parent, false);
        }

        IStock currentStock = _stocks.get(position);

        return populateWatchList(currentStock, currentListViewItem);
    }

    private View populateWatchList(IStock currentStock, View currentListView){
        System.out.println("stock list size is " + _stocks.size());

        ViewHolder vh = new ViewHolder(currentListView);

//        int i = _context.getResources().getIdentifier(currentItem.getCompName(),"drawable",_context.getPackageName());

        vh._stockName.setText(currentStock.getCompName());
        vh._stockSymbol.setText(currentStock.getSymbol());
        vh._stockPrice.setText(currentStock.getPrice().toString());

        vh._removeStock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                IPortfolio portfolio = Portfolio.getInstance();
                portfolio.removeStock(currentStock,1);
                _stocks.remove(currentStock);
                setData(portfolio.getPortfolio());
                notifyDataSetChanged();
            }
        });

        return currentListView;
    }
    private void setData(Hashtable<IStock, Integer> portfolio) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        //TODO : Sort portfolio by total price
        List<IStock> sortedPortfolio = new ArrayList<>();
        sortedPortfolio.addAll(portfolio.keySet());

        int index = 0;
        for (IStock s : sortedPortfolio) {
            Double x = s.getPrice()*portfolio.get(s);
            entries.add(new PieEntry( x.floatValue(),s.getSymbol()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Portfolio");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        _chart.setData(data);

        // undo all highlights
        _chart.highlightValues(null);

        _chart.invalidate();
    }

}