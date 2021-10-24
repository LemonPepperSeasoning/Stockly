package com.larkspur.stockly.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.larkspur.stockly.data.mappers.StockMapper;
import com.larkspur.stockly.models.Category;
import com.larkspur.stockly.models.IStock;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *  Makes a query to Firestore database for stock information on one thread while
 *  another thread executes the java functions (creating stock items using onComplete
 *  function). Stock items are created and put inside a list for use. All stock items are
 *  called in order
 */
public class DataFetcher {

    /**
     *  Fetch category with the matching cateogry.
     * @param category : category of the stock we want to get.
     * @param list : List object that is currently connected to adapter.
     * @param adapter : adapter to notify that the data has been changed.
     */
    public static void fetchCategoryStocks(Category category, List<IStock> list, BaseAdapter adapter){
        DataCache dataCache = DataCache.getInstance();
        List<IStock> stockList = new LinkedList<>();
        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .whereEqualTo("Category", category.toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        stockList.add(StockMapper.toStock(document.getData()));
                    }
                    if (stockList.size() > 0) {
                        list.addAll(stockList); //We can also change it to add one item at a time.
                        adapter.notifyDataSetChanged();
                        dataCache.addCategoryStock(category,stockList);
                    } else {
                        Log.d("Fetch Failed", "return value was empty");
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch stocks by category");
                }
            }
        });
    }

    /**
     *  Fetch stock with the highest view count.
     * @param shimmerView : shimmerView to stop and hide, once the data is fetch.
     * @param list : List object that is currently connected to adapter.
     * @param adapter : adapter to notify that the data has been changed.
     */
    public static void fetchStockMostView(List<IStock> list, RecyclerView.Adapter adapter, ShimmerFrameLayout shimmerView){
        DataCache dataCache = DataCache.getInstance();
        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("viewcount")
                .orderBy("viewcount", Query.Direction.DESCENDING)
                .limit(10)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();

                        DocumentReference ref = (DocumentReference)data.get("company");
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    IStock stock = StockMapper.toStock(task.getResult().getData());
                                    list.add(stock);
                                    adapter.notifyDataSetChanged();
                                    shimmerView.stopShimmer();
                                    shimmerView.setVisibility(View.GONE);
                                    dataCache.addMostViewStock(stock);
                                } else {
                                    Log.e("Fetch Error", "failed to fetch stocks by mostView's reference");
                                }
                            }
                        });
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch most viewed");
                }
            }
        });
    }

    /**
     * Fetches image from Firestore database loads it into the imageView in the CardView
     * inside the recyclerView "Most Viewed"
     * @param referenceLink the URL for the image in Firestore Storage
     * @param imageView the imageView in the CardView inside the RecyclerView
     */
    public static void downloadImage(String referenceLink, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        try {
            StorageReference httpsReference = storage.getReferenceFromUrl(referenceLink);
            File localFile = File.createTempFile("images", "jpg");
            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e("NO IMAGE=", referenceLink);
                }
            });
        } catch (IOException e) {
            //TODO : When image download fails, maybe we will just set it to default image or something.
            Log.e("NO IMAGE:", referenceLink);
        }
    }

    /**
     *  Fetch stock with the biggest percentage change in a span of a week.
     * @param direction : direction to search for. (ASCENDING for top loser, DESCENDING for top gainer)
     * @param shimmerView : shimmerView to stop and hide, once the data is fetch.
     * @param list : List object that is currently connected to adapter.
     * @param adapter : adapter to notify that the data has been changed.
     */
    public static void fetchTopChange(Query.Direction direction,List<IStock> list, RecyclerView.Adapter adapter,ShimmerFrameLayout shimmerView) {
        final IStock[] stock = new IStock[1];
        DataCache dataCache = DataCache.getInstance();

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .orderBy("WeekChange", direction)
                .limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        stock[0] = StockMapper.toStock(document.getData());
                    }
                    if (stock[0] != null) {
                        list.add(stock[0]);
                        adapter.notifyDataSetChanged();
                        shimmerView.stopShimmer();
                        shimmerView.setVisibility(View.GONE);
                        if (direction.equals(Query.Direction.ASCENDING)){
                            dataCache.addTopLoser(stock[0]);
                        }else{
                            dataCache.addTopGainer(stock[0]);
                        }
                    } else {
                        Log.d("Fetch Failed", "return value was empty");
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch stocks by category");
                }
            }
        });
    }

    /**
     *  Fetch all stocks that is in company colleciton.
     * @param list : list representing the context.
     * @param list2 : list representing the filtered result.
     * @param adapter : adapter to notify that the data has been changed.
     */
    public static void fetchAllStocks(List<IStock> list, List<IStock> list2, RecyclerView.Adapter adapter){
        DataCache stockHandler = DataCache.getInstance();
        List<IStock> stockList = new LinkedList<>();
        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        stockList.add(StockMapper.toStock(document.getData()));
                    }
                    if (stockList.size() > 0) {
                        list.addAll(stockList); //We can also change it to add one item at a time.
                        list2.addAll(stockList);
                        adapter.notifyDataSetChanged();
                        stockHandler.addAllStock(stockList);
                    } else {
                        Log.d("Fetch Failed", "return value was empty");
                    }
                } else {
                    Log.e("Fetch Error", "failed to fetch stocks by category");
                }
            }
        });
    }
}
