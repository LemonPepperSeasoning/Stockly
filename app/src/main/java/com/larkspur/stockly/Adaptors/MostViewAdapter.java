package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.larkspur.stockly.Activities.StockActivity;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class MostViewAdapter extends RecyclerView.Adapter<MostViewAdapter.ViewHolder>{


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _stockSymbol, _stockPrice;
        ImageView _stockImage;
        private Class _parent;

        public ViewHolder(@NonNull View view) {
            super(view);
            _parent = view.getContext().getClass();

            view.setOnClickListener(this);
            _stockSymbol = (TextView) view.findViewById(R.id.stock_name_view);
            _stockPrice = (TextView) view.findViewById(R.id.stock_price);
            _stockImage = (ImageView) view.findViewById(R.id.stock_image_view);
        }

        @Override
        public void onClick(View view) {
            IStock stock = _stockList.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), StockActivity.class);
            intent.putExtra("Screen", "Home");
            intent.putExtra("Class", _parent);
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
    private ViewGroup _parent;

    public MostViewAdapter(List<IStock> stockList){
        _stockList = stockList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        _parent = parent;
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

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedPrice = df.format(stock.getPrice());
        holder._stockPrice.setText(formattedPrice);
        downloadImage(stock.getImageLink().get(0),holder._stockImage);
    }

    @Override
    public int getItemCount() {
        return _stockList.size();
    }

    private void downloadImage(String referenceLink, ImageView imageView){

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

                    imageView.setImageBitmap(myBitmap);
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
}

