package com.larkspur.stockly.adaptors;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.larkspur.stockly.activities.DetailsActivity;
import com.larkspur.stockly.activities.utils.PieChartHandler;
import com.larkspur.stockly.models.IPortfolio;
import com.larkspur.stockly.models.IStock;
import com.larkspur.stockly.models.User;
import com.larkspur.stockly.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * This adaptor holds the views in the Portfolio screen.
 * Author: Alan, Jonathon
 */
public class PorfolioAdapter extends ArrayAdapter {

    /**
     * Represents every item in the screen and displays each one.
     */
    private class ViewHolder {
        TextView _stockName, _stockSymbol, _stockPrice, _stockTotalPrice, _quantityStock;
        LinearLayout _removeStock, _stockSide;
        CardView _stockColor;
        public ViewHolder(View currentListViewItem) {
            _stockName = currentListViewItem.findViewById(R.id.stock_name);
            _stockSymbol = currentListViewItem.findViewById(R.id.stock_symbol);
            _stockPrice = currentListViewItem.findViewById(R.id.stock_price);
            _stockTotalPrice = currentListViewItem.findViewById(R.id.stock_total_price);
            _quantityStock = currentListViewItem.findViewById(R.id.stock_quantity);
            _removeStock = currentListViewItem.findViewById(R.id.remove_stock);
            _stockSide = currentListViewItem.findViewById(R.id.stock_view);
            _stockColor = currentListViewItem.findViewById(R.id.status_view);
        }
    }

    int _layoutID;
    Context _context;
    private List<IStock> _stocks;
    private PieChart _chart;
    private IPortfolio _portfolio;

    /**
     * Default constructor
     * @param context this information required to access the xml files
     * @param resource the resource id for a layout file containing the relevant ListView
     * @param objects stock data objects list
     * @param chart PieChart
     */
    public PorfolioAdapter(@NonNull Context context, int resource, @NonNull List<IStock> objects, PieChart chart) {
        super(context, resource, objects);
        _layoutID = resource;
        _context = context;
        _stocks = objects;
        _chart = chart;
        _portfolio = User.getInstance().getPortfolio();
    }

    /**
     * Uses layoutInflater to initialise the cardView in listview and populates the card fields
     * with stock information using populateList
     * @param position position of stock in list
     * @param convertView the listView item you wish to create dynamically
     * @param parent the layout in which the listView item is created
     * @return CardViews inside the ListView
     */
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

    /**
     * Populates the stock items with data and implements click functionality for the CardViews
     * inside the listView
     * @param currentStock current stock being populated in the CardView
     * @param currentListView current cardView inside listView
     * @return CardView inside the ListView with information inside them
     */
    private View populatePortfolio(IStock currentStock, View currentListView) {

        ViewHolder vh = new ViewHolder(currentListView);
        DecimalFormat df = new DecimalFormat("#.##");

        vh._stockName.setText(currentStock.getCompName());
        vh._stockSymbol.setText(currentStock.getSymbol());

        String formattedPrice = df.format(currentStock.getPrice());
        vh._stockPrice.setText("$"+formattedPrice);
        double percentChange = currentStock.getHistoricalPrice().getLast24HourChange();

        vh._stockPrice.setText("$" + String.format("%.2f", currentStock.getPrice()) + " +" + String.format("%.2f", percentChange) + "%");

        if(percentChange < 0){
            vh._stockPrice.setText("$" + String.format("%.2f", currentStock.getPrice()) + " " + String.format("%.2f", percentChange) + "%");//
            vh._stockPrice.setTextColor(Color.RED);
            vh._stockColor.setCardBackgroundColor(Color.RED);
        }else{
            vh._stockPrice.setTextColor(_context.getResources().getColor(R.color.colorPrimaryBlue));
            vh._stockColor.setCardBackgroundColor(_context.getResources().getColor(R.color.colorPrimaryBlue));

        }

        int quantity = _portfolio.getQuantity(currentStock.getSymbol());
        Double totalPrice = currentStock.getPrice() * quantity;
        String formattedTotalPrice = df.format(totalPrice);
        vh._stockTotalPrice.setText(formattedTotalPrice);
        vh._quantityStock.setText(String.valueOf(quantity));

        vh._stockSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);

                intent.putExtra("Screen", "Portfolio");
                intent.putExtra("Class", _context.getClass());
                Bundle bundle = new Bundle();
                bundle.putSerializable("stock", currentStock);


                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
                Activity activity =(Activity) v.getContext();
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

                TextView popupMessage = (TextView) popupView.findViewById(R.id.popup_text_view);
                popupMessage.setText("Enter amount to remove from portfolio");

                Button closePopup = (Button) popupView.findViewById(R.id.add_to_portfolio_confirmbutton);
                EditText numberOfStocks = (EditText) popupView.findViewById(R.id.add_to_portfolio_edittext);
                ImageView plus = (ImageView) popupView.findViewById(R.id.plus_view);
                ImageView minus = (ImageView) popupView.findViewById(R.id.minus_view);

                int currentNumStocks = portfolio.getQuantity(currentStock.getSymbol());
                numberOfStocks.setText(Integer.toString(currentNumStocks));

                closePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                            PieChartHandler.setData(portfolio.getPortfolio(),_chart);
                            _chart.setCenterText(PieChartHandler.generateCenterSpannableText(portfolio));
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
}