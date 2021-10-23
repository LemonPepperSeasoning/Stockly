//package com.larkspur.stockly.Adaptors;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.cardview.widget.CardView;
//
//import com.larkspur.stockly.Activities.DetailsActivity;
//import com.larkspur.stockly.Models.IStock;
//import com.larkspur.stockly.R;
//
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
///**
// * This class is the adaptor for dynamically loading the CardViews for the ListView which
// * represented the search list suggestion.
// * Author: Jonathon
// * Reference: https://abhiandroid.com/ui/searchview
// */
//
//public class SearchListViewAdaptor extends ArrayAdapter {
//
//    public class ViewHolder {
//        public CardView _searchListCard;
//        public TextView _stockSymbol, _stockName, _stockPrice;
//
//        public ViewHolder(@NonNull View view) {
//            _searchListCard = (CardView) view.findViewById(R.id.search_list_card);
//            _stockSymbol = (TextView) view.findViewById(R.id.stock_symbol_view);
//            _stockName = (TextView) view.findViewById(R.id.stock_name_view);
//            _stockPrice = (TextView) view.findViewById(R.id.stock_price_view);
//
//        }
//    }
//
//    // Declare Variables
//    Context _context;
//    int _layoutID;
//    LayoutInflater inflater;
//    private List<IStock> _stockNamesList = null;
//    private List<IStock> _arraylist;
//
//    /**
//     * Default constructor
//     * @param context this information required to access the xml files
//     * @param resource the resource id for a layout file containing the relevant ListView
//     * @param stockNamesList List of stocks which are suggestible to the user, based
//     *                       on their input to the search bar (different from list of all stocks)
//     */
//    public SearchListViewAdaptor(Context context, int resource, List<IStock> stockNamesList) {
//        super(context, resource, stockNamesList);
//        _context = context;
//        _layoutID = resource;
//        _stockNamesList = stockNamesList;
//        inflater = LayoutInflater.from(_context);
//        _arraylist = new ArrayList<IStock>();
//        _arraylist.addAll(stockNamesList);
//    }
//
//    /**
//     * Uses layoutInflater to initialise the cardView in listview for the search suggestions
//     * and populates the card fields with stock information using populateList
//     * @param position position of stock in list
//     * @param convertView the listView item you wish to create dynamically
//     * @param parent the layout in which the listView item is created
//     * @return CardViews inside the ListView
//     */
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View currentListViewItem = convertView;
//        if (currentListViewItem == null) {
//            currentListViewItem = LayoutInflater.from(getContext()).inflate(_layoutID, parent, false);
//        }
//
//        IStock currentStock = _stockNamesList.get(position);
//
//
//        return populateList(currentStock, currentListViewItem);
//    }
//
//    /**
//     * Populates the CardView items with data and implements click functionality for the CardViews
//     * inside the listView
//     * @param currentStock current stock being populated in the CardView
//     * @param currentListView current cardView inside listView
//     * @return CardView inside the ListView with information inside them
//     */
//    private View populateList(IStock currentStock, View currentListView) {
//        System.out.println("stock list size is " + _stockNamesList.size());
//
//        ViewHolder vh = new ViewHolder(currentListView);
//
//        vh._stockName.setText(currentStock.getCompName());
//        vh._stockSymbol.setText(currentStock.getSymbol());
//
//        DecimalFormat df = new DecimalFormat("#.##");
//        String formattedPrice = df.format(currentStock.getPrice());
//        vh._stockPrice.setText(formattedPrice);
//
//        vh._searchListCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
//                System.out.println("serializing stock");
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("stock", currentStock);
//                System.out.println(bundle.getSerializable("stock"));
//                IStock test = (IStock) bundle.getSerializable("stock");
//                System.out.println(test.getCompName());
//
//                intent.putExtras(bundle);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                v.getContext().startActivity(intent);
//                Activity activity =(Activity) v.getContext();
//                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                Toast.makeText(_context, currentStock.getSymbol() + " was clicked!", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//
//        return currentListView;
//    }
//
//    /**
//     * Initialises the list of stocks which the user has suggestions for as well as the
//     * list of all stocks in the system to the SearchListView Adaptor so all information
//     * can be loaded in the Search component
//     * @param data List of Stock objects
//     */
//    public void addData(List<IStock> data){
//        _stockNamesList = data;
//        _arraylist = new ArrayList<IStock>(); // create new object to avoid pointing to same object
//        _arraylist.addAll(data);
//    }
//
//    /**
//     * Returns stock list size of search suggestions
//     * @return stock list size of search suggestions
//     */
//    @Override
//    public int getCount() {
//        return _stockNamesList.size();
//    }
//
//    /**
//     * Returns the stock in the stock list of search suggestions based on
//     * its position
//     * @param position position of the stock in the stock list of search suggestions
//     * @return the stock in the stock list of search suggestions
//     */
//    @Override
//    public IStock getItem(int position) {
//        return _stockNamesList.get(position);
//    }
//
//    /**
//     * Returns the stock's id in its stock list based on its position
//     * @param position stock's position
//     * @return id in the form of the stock's position
//     */
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    /**
//     * Filters the list of stock's based on the user's input. If the user has not done
//     * any input action, the list of all stocks will be presented automatically.
//     * On the other hand, if the user inputs a string, the system will populate
//     * another list of suggestion based on the input and display them.
//     * @param charText User input in search bar
//     */
//    public void filter(String charText) {
//        // change to lower case
//        charText = charText.toLowerCase(Locale.getDefault());
//        // clear all stocks
//        _stockNamesList.clear();
//        if (charText.length() == 0) {
//            // _stockNamesList refers to the stocks which contain the search query
//            // _arrayList refers to the all stocks
//            _stockNamesList.addAll(_arraylist);
//        } else {
//            for (IStock wp : _arraylist) {
//                if (wp.getCompName().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    _stockNamesList.add(wp);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//}
//
