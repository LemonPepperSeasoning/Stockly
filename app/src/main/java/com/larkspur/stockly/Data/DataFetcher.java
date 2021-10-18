package com.larkspur.stockly.Data;

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
import com.larkspur.stockly.Data.mappers.StockMapper;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.IStock;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataFetcher {

    public static void fetchCategoryStocks(Category category, List<IStock> list, BaseAdapter adapter){
        StockHandler stockHandler = StockHandler.getInstance();
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
//                        Log.d("+++++", document.getId() + " => " + document.getData());
                        stockList.add(StockMapper.toStock(document.getData()));
                    }
                    if (stockList.size() > 0) {
                        list.addAll(stockList); //We can also change it to add one item at a time.
                        adapter.notifyDataSetChanged();
                        stockHandler.addCategoryStock(category,stockList);
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
     *  Makes a query to Firestore database for stock information on one thread while
     *  another thread executes the java functions (creating stock items using onComplete
     *  function). Stock items are created and put inside a list for use. All stock items are
     *  called in order
     */
    public static void fetchStockMostView(List<IStock> list, RecyclerView.Adapter adapter, ShimmerFrameLayout shimmerView){
        StockHandler stockHandler = StockHandler.getInstance();
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
                                    stockHandler.addMostViewStock(stock);
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

    public static void fetchTopChange(Query.Direction direction,List<IStock> list, RecyclerView.Adapter adapter) {
        final IStock[] stock = new IStock[1];

        // Getting numbers collection from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company_v1")
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
