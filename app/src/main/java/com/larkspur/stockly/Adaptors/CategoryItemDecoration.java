package com.larkspur.stockly.Adaptors;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * This class handles the class
 */
public class CategoryItemDecoration extends RecyclerView.ItemDecoration {

    int spaceBetween;
    public CategoryItemDecoration(int space) {
        spaceBetween = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
//        int parentWidth = parent.getMeasuredWidth();
//        Log.e("widths","Parent : " + String.valueOf(parentWidth));
//        int childWidth = view.getMeasuredWidth();
//        Log.e("widths", "Child : "+ String.valueOf(childWidth));
//        int spaceBetween = parentWidth - childWidth*2;

        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = 0;
//        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) %2 == 1 ) {
            outRect.left = spaceBetween;
        } else {
            outRect.right = 0;
        }
    }

}