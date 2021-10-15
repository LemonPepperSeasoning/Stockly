package com.larkspur.stockly.Adaptors;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.larkspur.stockly.Activities.StockActivity;
import com.larkspur.stockly.Models.IPortfolio;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Portfolio;
import com.larkspur.stockly.Models.User;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class PorfolioAdapter extends ArrayAdapter {

    private class ViewHolder {
        TextView _stockName, _stockSymbol, _stockPrice, _stockTotalPrice, _quantityStock;
        LinearLayout _removeStock, _stockSide;

        public ViewHolder(View currentListViewItem) {
            _stockName = currentListViewItem.findViewById(R.id.stock_name);
            _stockSymbol = currentListViewItem.findViewById(R.id.stock_symbol);
            _stockPrice = currentListViewItem.findViewById(R.id.stock_price);
            _stockTotalPrice = currentListViewItem.findViewById(R.id.stock_total_price);
            _quantityStock = currentListViewItem.findViewById(R.id.stock_quantity);
            _removeStock = currentListViewItem.findViewById(R.id.remove_stock);
            _stockSide = currentListViewItem.findViewById(R.id.stock_view);
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

        return populatePortfolio(currentStock, currentListViewItem);
    }

    private View populatePortfolio(IStock currentStock, View currentListView) {
        System.out.println("stock list size is " + _stocks.size());

        ViewHolder vh = new ViewHolder(currentListView);
        DecimalFormat df = new DecimalFormat("#.##");

//        int i = _context.getResources().getIdentifier(currentItem.getCompName(),"drawable",_context.getPackageName());

        vh._stockName.setText(currentStock.getCompName());
        vh._stockSymbol.setText(currentStock.getSymbol());


        String formattedPrice = df.format(currentStock.getPrice());
        vh._stockPrice.setText(formattedPrice);

        IPortfolio portfolio = User.getInstance().getPortfolio();
        int quantity = portfolio.getQuantity(currentStock.getSymbol());
        Double totalPrice = currentStock.getPrice() * quantity;
        String formattedTotalPrice = df.format(totalPrice);
        vh._stockTotalPrice.setText(formattedTotalPrice);
        vh._quantityStock.setText(String.valueOf(quantity));

        vh._stockSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StockActivity.class);
                intent.putExtra("Screen", "Watchlist");
                intent.putExtra("Class", _context.getClass());
                System.out.println("serializing stock");
                System.out.println(_context.getClass());
                Bundle bundle = new Bundle();
                bundle.putSerializable("stock", currentStock);
                System.out.println(bundle.getSerializable("stock"));
                IStock test = (IStock) bundle.getSerializable("stock");
                System.out.println(test.getCompName());

                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
                Toast.makeText(_context, currentStock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        vh._removeStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.add_portfolio_popup, null);
                PopupWindow popupWindow = new PopupWindow(popupView, DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                IPortfolio portfolio = User.getInstance().getPortfolio();

                Button closePopup = (Button) popupView.findViewById(R.id.add_to_portfolio_confirmbutton);
                EditText numberOfStocks = (EditText) popupView.findViewById(R.id.add_to_portfolio_edittext);
                ImageView plus = (ImageView) popupView.findViewById(R.id.plus_view);
                ImageView minus = (ImageView) popupView.findViewById(R.id.minus_view);

                int currentNumStocks = portfolio.getQuantity(currentStock.getSymbol());
                numberOfStocks.setText(Integer.toString(currentNumStocks));

                closePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(numberOfStocks.getText().toString());
                        if (!numberOfStocks.getText().toString().equals("")) {
                            int numStocks = Integer.parseInt(numberOfStocks.getText().toString());
                            if (numStocks > currentNumStocks) {
                                portfolio.removeStock(currentStock, currentNumStocks);
                            } else {
                                portfolio.removeStock(currentStock, numStocks);
                            }

                            if (portfolio.getQuantity(currentStock.getSymbol()) == -1) {
                                _stocks.remove(currentStock);
                            }
                            setData(portfolio.getPortfolio());
                            _chart.setCenterText(generateCenterSpannableText(portfolio));
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(v.getContext(), "A number needs to be input", Toast.LENGTH_SHORT).show();
                        }

                        popupWindow.dismiss();
                    }
                });

                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(numberOfStocks.getText().toString());
                        if (numberOfStocks.getText().toString().equals("") || numberOfStocks.getText().toString().equals("0")) {
                        } else {
                            int newNumStocks = Integer.parseInt(numberOfStocks.getText().toString()) - 1;
                            numberOfStocks.setText(Integer.toString(newNumStocks));
                        }
                    }
                });

                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(numberOfStocks.getText().toString());
                        if (numberOfStocks.getText().toString().equals("")) {
                            numberOfStocks.setText("1");
                        } else if (Integer.parseInt(numberOfStocks.getText().toString()) == currentNumStocks) {
                            numberOfStocks.setText(Integer.toString(currentNumStocks));
                        } else {
                            int newNumStocks = Integer.parseInt(numberOfStocks.getText().toString()) + 1;
                            numberOfStocks.setText(Integer.toString(newNumStocks));
                        }

                    }
                });
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
            Double x = s.getPrice() * portfolio.get(s);
            entries.add(new PieEntry(x.floatValue(), s.getSymbol()));
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


    private SpannableString generateCenterSpannableText(IPortfolio portfolio) {
        Double totalValue = portfolio.getTotalValue();
        Double percentageChange = portfolio.getTotal24HrChange();

        String topLine = "Total value:\n";
        String middleLine = "$" + String.format("%.2f", totalValue) + "\n";
        String percentage = String.format("%.2f", percentageChange) + "%";
        String bottomLine = "Today:" + percentage;

        SpannableString s = new SpannableString(topLine + middleLine + bottomLine);

        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(2.2f), 0, topLine.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), topLine.length(), s.length() - bottomLine.length(), 0);
        s.setSpan(new RelativeSizeSpan(2.0f), topLine.length(), s.length() - bottomLine.length(), 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - bottomLine.length(), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.8f), s.length() - bottomLine.length(), s.length(), 0);
        if (percentageChange >= 0) {
            s.setSpan(new ForegroundColorSpan(Color.GREEN), s.length() - percentage.length(), s.length(), 0);
        } else {
            s.setSpan(new ForegroundColorSpan(Color.RED), s.length() - percentage.length(), s.length(), 0);
        }
        return s;
    }

}