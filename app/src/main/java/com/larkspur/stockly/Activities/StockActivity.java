package com.larkspur.stockly.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;


import android.view.Gravity;
import android.view.LayoutInflater;


import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;


import android.widget.ListView;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;


import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Adaptors.SearchListViewAdaptor;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.HistoricalPrice;

import com.larkspur.stockly.Models.IHistoricalPrice;
import com.larkspur.stockly.Models.IPortfolio;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.Models.IWatchlist;
import com.larkspur.stockly.Models.Portfolio;
import com.larkspur.stockly.Models.Stock;
import com.larkspur.stockly.Models.UserInfo;
import com.larkspur.stockly.Models.Watchlist;
import com.larkspur.stockly.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StockActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
 SearchView.OnQueryTextListener{

    private LineChart chart;
    //    private SeekBar seekBarX, seekBarY;
//    private TextView tvX, tvY;
    private Typeface tfLight;
    private DrawerLayout _drawerLayout;


    private class ViewHolder {
        TextView _stockName, _stockNameAndSymbol, _stockPrice, _stockPercent, _previousScreen;
        LinearLayout _return;
        ImageView _stockImage;
        public ViewHolder() {
            _stockName = findViewById(R.id.stock_name);
            _stockNameAndSymbol = findViewById(R.id.stock_name_and_symbol);
            _stockPrice = findViewById(R.id.stock_price);
            _stockPercent = findViewById(R.id.stock_percent);
            _return = findViewById(R.id.return_view);
            _previousScreen = findViewById(R.id.previous_screen_text_view);
            _stockImage = findViewById(R.id.stock_image_view);
        }
    }

    private ViewHolder _vh;
    private IStock _stock;
    private boolean _watchlisted;
    private IWatchlist _watchlist;
    private int _currentImageIndex;


    //        =======================Search functionality=============================

    ListView list;
    SearchListViewAdaptor _adaptor;
    SearchView editsearch;
    String[] stockNameList;
    private UserInfo _userInfo;

    //        =======================--------------------=============================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        _drawerLayout = findViewById(R.id.drawer_layout);
        _vh = new ViewHolder();

        if (getIntent().getExtras() != null) {
            System.out.println("STOCK DATA HERE\n");
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            System.out.println(bundle.getSerializable("stock"));
            _stock = (IStock) bundle.getSerializable("stock");

            _currentImageIndex = 0;
            downloadImage(_stock.getImageLink().get(_currentImageIndex));

            _watchlist = Watchlist.getInstance();
            _watchlisted = _watchlist.hasStock(_stock);
            System.out.println("STOCK STARTS HERE");
            System.out.println(intent.getStringExtra("Screen"));
            System.out.println("previous class: " + intent.getExtras().getSerializable("Class"));
            String previousScreen = intent.getStringExtra("Screen");
            _vh._previousScreen.setText("Return to " + previousScreen);

            setupStockView();
            setupGraph();

            Toast.makeText(this, _stock.getSymbol() + " was Launched!", Toast.LENGTH_SHORT).show();
        } else {
            throw new RuntimeException("Stock not found!");
        }

        //        =======================Search functionality=============================

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.searchList);

        _adaptor = new SearchListViewAdaptor(this, R.layout.search_list_item, new ArrayList<>());

        // Binds the Adapter to the ListView
        list.setAdapter(_adaptor);

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);

        // Set up the searchbar settings
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

        //        =======================--------------------=============================
    }


    private void downloadImage(String referenceLink){

        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference httpsReference = storage.getReferenceFromUrl("https://storage.googleapis.com/stockstats-39c48.appspot.com//Users/Goyard/Downloads/FIRE-min.jpg%22);
//        StorageReference httpsReference = storage.getReferenceFromUrl("gs://stockstats-39c48.appspot.com/SOFTENG306P2_5\\AAPL\\2.jpg");

//        StorageReference x = httpsReference.child("/").child("Users").child("Goyard").child("Downloads").child("apple.png");
        try{
            StorageReference httpsReference = storage.getReferenceFromUrl(referenceLink);
            File localFile = File.createTempFile("images", "jpg");
            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    _vh._stockImage.setImageBitmap(myBitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e("NO IMAGE=",referenceLink);
                }
            });
        }catch(IOException e) {
            //TODO : When image download fails, maybe we will just set it to default image or something.
            Log.e("NO IMAGE:",referenceLink);
        }
    }

    //        =======================Search functionality=============================

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        _adaptor.filter(text);
        return false;
    }

    public void clickSearch(View view) {
        ListView listview = findViewById(R.id.searchList);
        listview.setVisibility(View.VISIBLE);

        // Show the keyboard
        editsearch.setFocusable(true);
        editsearch.setIconified(false);
        editsearch.requestFocusFromTouch();

        // Show text
        EditText searchEditText = (EditText) editsearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setCursorVisible(true);

        // Fetch the stock data for suggestions
        fetchAllStocks();
    }

    public void closeSearch(View view) {
        // Collapse searchList
        ListView listview = findViewById(R.id.searchList);
        listview.setVisibility(View.GONE);

        //Hide keyboard
        editsearch.clearFocus();
        editsearch.requestFocusFromTouch();

        //Stop blinking in searchbar
        EditText searchEditText = (EditText) editsearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setCursorVisible(false);

        //Clear text in searchbar
        searchEditText.setText("");
    }

    //        =======================--------------------=============================

    //        =======================Search functionality=============================

    private void fetchAllStocks() {
        List<IStock> stockList = new LinkedList<>();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company_v2")
                .whereEqualTo("Category", Category.InformationTechnology.toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot results = task.getResult();
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("+++++", document.getId() + " => " + document.getData());
                        Map<String, Object> data = document.getData();
                        HistoricalPrice tmpHistoricalPrice = new HistoricalPrice((List<Double>) data.get("Price"));
                        IStock tmpStock = new Stock(
                                ((String) data.get("Name")),
                                ((String) data.get("Symbol")),
                                (Category.getValue((String) data.get("Category"))),
                                ((String) data.get("Subindustry")),
                                ((String) data.get("location")),
                                tmpHistoricalPrice);
                        stockList.add(tmpStock);
                    }

                    System.out.println("============================");
                    System.out.println(stockList.size());
                    for (IStock i : stockList) {
                        Log.d("== Stock : ", i.getCompName() + " " + i.getCategory() + " " + i.getSymbol() + " == ");
                    }
                    System.out.println("============================");

                    if (stockList.size() > 0) {
                        Log.i("Getting colors", "Success");

                        _adaptor.addData(stockList);
                        // Once the task is successful and data is fetched, propagate the adaptor
                        //  propagateAdaptor(stockList);

                        // Hide the ProgressBar
//                        vh.progressBar.setVisibility(View.GONE);
                    } else {
//                        Toast.makeText(getBaseContext(), "Colors Collection was empty!", Toast.LENGTH_LONG).show();
                    }
                } else {
//                    Toast.makeText(getBaseContext(), "Loading colors collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //        =======================--------------------=============================


    private void setupStockView() {
        _vh._stockName.setText(_stock.getCompName());
        _vh._stockNameAndSymbol.setText(_stock.getCompName() + " (" + _stock.getSymbol() + ")");
        _vh._stockPrice.setText(_stock.getPrice().toString());
    }

    private void setupGraph() {
        chart = findViewById(R.id.chart1);
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setBackgroundColor(Color.rgb(46, 46, 51));

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);

        XAxis x = chart.getXAxis();
        x.setEnabled(false);

        YAxis y = chart.getAxisLeft();
        y.setTypeface(tfLight);
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);

        setData(_stock.getHistoricalPrice());

        // don't forget to refresh the drawing
        chart.invalidate();
    }

    private void setData(@NonNull IHistoricalPrice prices) {
        ArrayList<Entry> values = new ArrayList<>();

        int index = 0;
        for (Double i : prices.getHistoricalPrice()) {
            values.add(new Entry(index, i.floatValue()));
            index++;
        }
        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.rgb(159, 125, 225));
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.rgb(159, 125, 225));
            set1.setFillColor(Color.rgb(159, 125, 225));
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // create a data object with the data sets
            LineData data = new LineData(set1);
            data.setValueTypeface(tfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        return;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    public void clickMenu(View view) {
        MainActivity.openDrawer(_drawerLayout);
    }

    public void clickCloseSideMenu(View view) {
        MainActivity.closeDrawer(_drawerLayout);
    }

    public void clickHome(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        MainActivity.redirectActivity(this, MainActivity.class, bundle);
    }

    public void clickPortfolio(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        MainActivity.redirectActivity(this, PortfolioActivity.class, bundle);
    }

    public void clickWatchlist(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        MainActivity.redirectActivity(this, WatchlistActivity.class, bundle);
    }

    public void clickSettings(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        MainActivity.redirectActivity(this, SettingsActivity.class, bundle);
    }

    public void clickHelp(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        MainActivity.redirectActivity(this, HelpActivity.class, bundle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(_drawerLayout);
    }

    public void clickReturn(View view) {
        Intent intent = this.getIntent();
        Class activity = (Class) intent.getExtras().getSerializable("Class");
        Bundle bundle = new Bundle();
        bundle.putSerializable("stock", _stock);
        System.out.println(bundle.getSerializable("stock"));
        IStock test = (IStock) bundle.getSerializable("stock");
        System.out.println(test.getCompName());

        intent.putExtras(bundle);
        MainActivity.redirectActivity(this, activity, bundle);
    }


    public void clickAddWatchlist(View view) {
        if (_watchlisted == false) {
            _watchlist.addStock(_stock);
            Toast.makeText(this, "added " + _stock.getCompName() + " to watchlist", Toast.LENGTH_SHORT).show();
            _watchlisted = true;

        } else {
            _watchlist.removeStock(_stock);
            Toast.makeText(this, "removed " + _stock.getCompName() + " to watchlist", Toast.LENGTH_SHORT).show();
            _watchlisted = false;
        }
    }

    public void clickAddPortfolio(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_portfolio_popup, null);
        PopupWindow popupWindow = new PopupWindow(popupView, DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        IPortfolio portfolio = Portfolio.getInstance();
        Button closePopup = (Button) popupView.findViewById(R.id.add_to_portfolio_confirmbutton);
        EditText numberOfStocks = (EditText) popupView.findViewById(R.id.add_to_portfolio_edittext);
        ImageView plus = (ImageView) popupView.findViewById(R.id.plus_view);
        ImageView minus = (ImageView) popupView.findViewById(R.id.minus_view);
        numberOfStocks.setText("0");

        Toast.makeText(this, "added " + _stock.getCompName() + " to portfolio", Toast.LENGTH_SHORT).show();
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(numberOfStocks.getText().toString());
                if (!numberOfStocks.getText().toString().equals("")) {
                    int numStocks = Integer.parseInt(numberOfStocks.getText().toString());
                    portfolio.addStock(_stock, numStocks);
                } else {
                    Toast.makeText(view.getContext(), "A number needs to be input", Toast.LENGTH_SHORT).show();
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
                if(numberOfStocks.getText().toString().equals("")){
                    numberOfStocks.setText("1");
                }else{
                    int newNumStocks = Integer.parseInt(numberOfStocks.getText().toString()) + 1;
                    numberOfStocks.setText(Integer.toString(newNumStocks));
                }

            }
        });

    }

    public void clickNextImageLeft(View view) {
        _currentImageIndex += 2; //same as -1
        downloadImage(_stock.getImageLink().get(_currentImageIndex%3));
    }

    public void clickNextImageRight(View view) {
        _currentImageIndex += 1;
        downloadImage(_stock.getImageLink().get(_currentImageIndex%3));
    }
}
