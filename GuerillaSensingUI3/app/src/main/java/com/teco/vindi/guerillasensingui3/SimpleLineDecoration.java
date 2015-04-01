package com.teco.vindi.guerillasensingui3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Simple line decorator for recycler view by samukaak.
 * Source: http://qiita.com/Sam/items/d5b82f6c2830fa4d14a2
 */
public class SimpleLineDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private Drawable mDivider;

    public SimpleLineDecoration(final Context context) {
        final TypedArray array = context.obtainStyledAttributes(ATTRS);
        mDivider = array.getDrawable(0);
        array.recycle();
    }

    @Override
    public void getItemOffsets(final Rect outRect, final int itemPosition, final RecyclerView parent) {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }

    @Override
    public void onDraw(final Canvas c, final RecyclerView parent) {
        drawVertical(c, parent);
    }

    public void drawVertical(final Canvas c, final RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}