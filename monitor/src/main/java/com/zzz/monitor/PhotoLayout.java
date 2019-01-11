package com.zzz.monitor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class PhotoLayout extends ViewGroup {
    public PhotoLayout(Context context) {
        this(context, null);
    }

    public PhotoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int mTotalHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            int height = childView.getMeasuredHeight();
            int width = childView.getMeasuredWidth();
            childView.layout(left, mTotalHeight, left + width, mTotalHeight + height);
            mTotalHeight += height;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 计算自定义的ViewGroup中所有子控件的大小
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight);
    }
}
