package cn.kuaikuai.trip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cn.kuaikuai.trip.R;

public class AutoWrapView extends ViewGroup {
    private int mHorizontalSpacing;
    private int mVerticalSpacing;


    public AutoWrapView(Context context) {
        this(context, null);
    }

    public AutoWrapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoWrapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AutoWrapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    public void setSpacing(int horizontalSpacing, int verticalSpacing) {
        mHorizontalSpacing = horizontalSpacing;
        mVerticalSpacing = verticalSpacing;
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.AutoWrapView);
        int horizotal = (int) typedArray.getDimension(R.styleable.AutoWrapView_horizontal_space, 0);
        int vertical = (int) typedArray.getDimension(R.styleable.AutoWrapView_vertical_space, 0);
        setSpacing(horizotal, vertical);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int mTotalHeight = 0;
        int mTotalWidth = 0;
        int mTempHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int measureHeight = childView.getMeasuredHeight();
            int measuredWidth = childView.getMeasuredWidth();
            mTempHeight = (measureHeight > mTempHeight) ? measureHeight : mTempHeight;
            if (mTotalHeight == 0) {
                mTotalHeight = mTempHeight;
            }
            if ((measuredWidth + mTotalWidth + mHorizontalSpacing) > widthSize) {
                mTotalWidth = 0;
                mTotalHeight += (mTempHeight + mVerticalSpacing);
                mTempHeight = 0;
            }
            int startX = mTotalWidth;
            if (startX != 0) {
                startX += mHorizontalSpacing;
            }
            mTotalWidth = startX + measuredWidth;
        }
        setMeasuredDimension(widthSize, mTotalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int mTotalHeight = 0;
        int mTotalWidth = 0;
        int mTempHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int measureHeight = childView.getMeasuredHeight();
            int measuredWidth = childView.getMeasuredWidth();
            mTempHeight = (measureHeight > mTempHeight) ? measureHeight : mTempHeight;
            if ((measuredWidth + mTotalWidth + mHorizontalSpacing) > getMeasuredWidth()) {
                mTotalWidth = 0;
                mTotalHeight += (mTempHeight + mVerticalSpacing);
                mTempHeight = 0;
            }
            int startX = mTotalWidth;
            if (startX != 0) {
                startX += mHorizontalSpacing;
            }
            mTotalWidth = startX + measuredWidth;
            childView.layout(startX, mTotalHeight, mTotalWidth, mTotalHeight + measureHeight);
        }
    }
}