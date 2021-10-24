package com.larkspur.stockly.Adaptors.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * This class is used in recycler views to add a vertical division between items inside the view
 * adapted from: https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int _verticalSpaceHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        _verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = _verticalSpaceHeight;
        }
    }
}