package com.larkspur.stockly.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.larkspur.stockly.activities.DetailsActivity;
import com.larkspur.stockly.data.DataFetcher;
import com.larkspur.stockly.models.IStock;
import com.larkspur.stockly.R;

import java.util.List;

/**
 * This adaptor loads the cardViews in the listView in the "Most Viewed" Recycler View
 * inside the main screen.
 * Author: Alan Lin
 */
public class MostViewAdapter extends RecyclerView.Adapter<MostViewAdapter.ViewHolder>{

    /**
     * Represents every item in the screen and displays each one.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _stockSymbol, _stockPrice;
        CardView _statusView;
        ImageView _stockImage;
        private Class _parent;

        public ViewHolder(@NonNull View view) {
            super(view);
            _parent = view.getContext().getClass();
            view.setOnClickListener(this);
            _stockSymbol = (TextView) view.findViewById(R.id.stock_name_view);
            _stockPrice = (TextView) view.findViewById(R.id.stock_price);
            _stockImage = (ImageView) view.findViewById(R.id.stock_image_view);
            _statusView = (CardView) view.findViewById(R.id.status_view);
        }

        @Override
        public void onClick(View view) {
            IStock stock = _stockList.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), DetailsActivity.class);
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
        }
    }

    private List<IStock> _stockList;
    private Context _context;

    /**
     * Default constructor
     * @param stockList list of stock objects
     */
    public MostViewAdapter(List<IStock> stockList){
        _stockList = stockList;
    }

    /**
     * Creates a ViewHolder for the CardViews inside recyclerView once the main screen
     * is launched
     * @param parent layout in which recyclerView is held
     * @param viewType view type
     * @return ViewHolder containing the cardView inside the RecyclerView
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        _context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(_context);
        View stockView = inflater.inflate(R.layout.stock_most_viewed_recycler_view, parent, false);
        ViewHolder holder = new ViewHolder(stockView);
        return holder;
    }

    /**
     * Updates the ViewHolder contents for recyclerview
     * @param holder ViewHolder for recyclerView
     * @param position position in stock list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IStock stock = _stockList.get(position);
        holder._stockSymbol.setText(stock.getSymbol());
        holder._stockPrice.setText("$" + String.format("%.2f", stock.getPrice()) + " "
                + String.format("%.2f", stock.getHistoricalPrice().getLast24HourChange()) + "%");
        DataFetcher.downloadImage(stock.getImageLink().get(0),holder._stockImage);
        if (stock.getHistoricalPrice().getLast24HourChange() > 0 ){
            holder._stockPrice.setTextColor(Color.rgb(17, 168, 253));
            holder._statusView.setCardBackgroundColor(Color.rgb(17, 168, 253));
        }else{
            holder._stockPrice.setTextColor(Color.rgb(255, 24, 24));
            holder._statusView.setCardBackgroundColor(Color.rgb(255, 24, 24));
        }
    }

    /**
     * Returns the stock list size
     * @return number of stocks in stocklist
     */
    @Override
    public int getItemCount() {
        return _stockList.size();
    }

}

