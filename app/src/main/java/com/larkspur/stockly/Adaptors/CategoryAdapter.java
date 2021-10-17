package com.larkspur.stockly.Adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.larkspur.stockly.Activities.ListActivity;
import com.larkspur.stockly.Activities.StockActivity;
import com.larkspur.stockly.Data.mappers.StockMapper;
import com.larkspur.stockly.Models.Category;
import com.larkspur.stockly.Models.IStock;
import com.larkspur.stockly.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class handles the textView for the categories in the main screen.
 * Author: Alan
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    /**
     * Represents every item in the screen and displays each one.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _categoryName;
        public ImageView _categoryImage;
        private Class _parent;

        public ViewHolder(@NonNull View view) {
            super(view);
            _parent = view.getContext().getClass();
            view.setOnClickListener(this);

            _categoryName = (TextView) view.findViewById(R.id.category_name_view);
            _categoryImage = (ImageView) view.findViewById(R.id.category_image_view);
        }

        @Override
        public void onClick(View view) {
            Category category = _categoryList.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), ListActivity.class);
            intent.putExtra("Screen", "Home");
            intent.putExtra("Class", _parent);
            intent.putExtra("Category", category.getCategoryName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
            Toast.makeText(_context, category.toString() + " was clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Category> _categoryList;
    private Context _context;

    /**
     * Default constructor
     */
    public CategoryAdapter(){
        _categoryList = Arrays.asList(Category.values());
    }

    /**
     * Creates a ViewHolder for the TextViews for the categories titles once the main screen
     * is launched
     * @param parent layout in which the TextViews are held
     * @param viewType view type
     * @return VIewHolder holding the TextViews
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        _context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(_context);
        View stockView = inflater.inflate(R.layout.main_view_category, parent, false);

        ViewHolder holder = new ViewHolder(stockView);
        return holder;
    }

    /**
     * Updates the ViewHolder's contents for the TextViews
     * @param holder ViewHolder for the TextViews
     * @param position position in stock list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Category category = _categoryList.get(position);
        holder._categoryName.setText(category.getCategoryName().replace(" ","\n"));
        holder._categoryImage.setImageResource(category.getDrawableId());
        holder._categoryImage.setColorFilter(category.getColor());
    }

    /**
     * Get the number of categories
     * @return number of categories
     */
    @Override
    public int getItemCount() {
        return _categoryList.size();
    }

}
