package com.larkspur.stockly.Adaptors;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * This class handles the orientation of the categories layouts by dynamically creating
 * space between each category layout for a better UI experience.
 * Author: Takahiro
 */
public class CategoryItemDecoration extends RecyclerView.ItemDecoration {

    int spaceBetween;
    public CategoryItemDecoration(int space) {
        spaceBetween = space;
    }

    /**
     * Calculate the categories layout's spacing from each other
     * @param outRect Parent layout shape
     * @param view Categories layout
     * @param parent Parent layout
     * @param state state of RecyclerView
     */
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
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