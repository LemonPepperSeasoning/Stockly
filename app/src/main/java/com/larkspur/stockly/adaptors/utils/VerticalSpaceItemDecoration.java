package com.larkspur.stockly.adaptors.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * This class is used in recycler views to add a vertical division between items inside the view
 * adapted from: https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int _verticalSpaceHeight;

    /**
     * Takes in the amount of space set to be between items
     * @param verticalSpaceHeight - int, the space to be set between items
     */
    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        _verticalSpaceHeight = verticalSpaceHeight;
    }

    /**
     * sets the offets between items in the recycler view
     * @param outRect
     * @param view - the view item that is currently shown in the recycler view
     * @param parent - the parent view that the recycler view is in
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //if item is the last one in the list - do not add a space below
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = _verticalSpaceHeight;
        }
    }
}