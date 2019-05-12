/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.kuaikuai.trip.view.viewpagerindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.kuaikuai.trip.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements PageIndicator {
    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final CharSequence EMPTY_TITLE = "";

    /**
     * Interface for a callback when the selected tab has been reselected.
     */
    public interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        void onTabReselected(int position);

        void onTabSelected(int position);
    }

    private Runnable mTabSelector;
    private int pading2dp = getResources().getDimensionPixelSize(R.dimen.dimen_2);
    private int pading10dp = getResources().getDimensionPixelSize(R.dimen.dimen_10);
    private float mIndicatorHeight;

    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            final int oldSelected = mViewPager.getCurrentItem();
            final int newSelected = tabView.getIndex();
            if (mTabReselectedListener != null) {
                if (oldSelected == newSelected) {
                    mTabReselectedListener.onTabReselected(newSelected);
                } else {
                    mTabReselectedListener.onTabSelected(newSelected);
                }
            }
            mViewPager.setCurrentItem(newSelected, true);
        }
    };


    private final IcsLinearLayout mTabLayout;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mMaxTabWidth;
    private int mSelectedTabIndex;

    private OnTabReselectedListener mTabReselectedListener;

    private ArrayList pointViews = new ArrayList<>();
    private ArrayList<TabView> tabViews = new ArrayList<>();
    private ArrayList<ImageView> iv_indicators = new ArrayList<>();

    /**
     * 设置indicator的宽度自适应
     */
    private boolean indicatorWidthSelfAdaption = false;

    public void setIndicatorWidthSelfAdaption(boolean indicatorWidthSelfAdaption) {
        this.indicatorWidthSelfAdaption = indicatorWidthSelfAdaption;
    }

    private float mIndicatorWidth = -1;
    /**
     * 文字大小
     */
    private float textSize = 16;

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    /**
     * 选中的文字大小
     */
    private float selectTextSize = 18;

    public void setSelectTextSize(float selectTextSize) {
        this.selectTextSize = selectTextSize;
    }

    private int textUnSelectColor;  //文字未选中时的颜色
    @DrawableRes
    private int indicatorUnSelectColor;   //indicator未选中时的颜色

    /**
     * 设置文字和indicator未选中时的颜色
     */
    public void setUnSelectColor(int textUnSelectColor, @DrawableRes int indicatorUnSelectColor) {
        this.textUnSelectColor = textUnSelectColor;
        this.indicatorUnSelectColor = indicatorUnSelectColor;
    }

    private int textSelectedColor;        //文字选中时的颜色
    @DrawableRes
    private int indicatorSelectedColor;   //indicator选中时的颜色

    /**
     * 设置文字和indicator选中时的颜色
     */
    public void setSelectedColor(int textSelectedColor, @DrawableRes int indicatorSelectedColor) {
        this.textSelectedColor = textSelectedColor;
        this.indicatorSelectedColor = indicatorSelectedColor;
    }

    private boolean isAverageView = true;//是否平均分头部

    public void setIsAverageView(boolean isAverageView) {
        this.isAverageView = isAverageView;
    }

    public TabPageIndicator(Context context) {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        mTabLayout = new IcsLinearLayout(context, R.attr.vpiTabPageIndicatorStyle);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabPageIndicator);

        mIndicatorWidth = typedArray.getDimension(R.styleable.TabPageIndicator_indicator_width, -1);
        int normalTextColor = typedArray.getColor(R.styleable.TabPageIndicator_text_normal_color, Color.WHITE);
        int selectedTextColor = typedArray.getColor(R.styleable.TabPageIndicator_text_selected_color, Color.BLACK);
        int lineSelectedColor = typedArray.getResourceId(R.styleable.TabPageIndicator_line_selected_color, 0);
        int lineNormalColor = typedArray.getResourceId(R.styleable.TabPageIndicator_line_normal_color, 0);
        float selectedTextSize = typedArray.getDimension(R.styleable.TabPageIndicator_text_selected_size, 16);
        float normalTextSize = typedArray.getDimension(R.styleable.TabPageIndicator_text_normal_size, 16);
        mIndicatorHeight = typedArray.getDimension(R.styleable.TabPageIndicator_indicator_height, pading2dp);
        setSelectedColor(selectedTextColor, lineSelectedColor);
        setUnSelectColor(normalTextColor, lineNormalColor);
        setTextSize(normalTextSize);
        setSelectTextSize(selectedTextSize);
        typedArray.recycle();
    }

    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        mTabReselectedListener = listener;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            if (childCount > 2) {
                mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            } else {
                mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
            }
        } else {
            mMaxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }


    private void addTab(int index, CharSequence text, int iconResId) {
        FrameLayout frameLayout = new FrameLayout(getContext());

        final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setSingleLine(true);
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setGravity(Gravity.CENTER);
        tabView.setText(text);
        if (textSize != -1) {
            tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        if (iconResId != 0) {
            tabView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        }
        if (isAverageView) {//要平均分
            tabView.setPadding(pading2dp, 0, pading2dp, 0);
            frameLayout.addView(tabView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        } else {
            tabView.setPadding(pading10dp, 0, pading10dp, 0);
            frameLayout.addView(tabView, new LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        }
        ImageView iv = new ImageView(getContext());
        LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, (int) mIndicatorHeight);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        if (indicatorWidthSelfAdaption) {//indicator 宽度根据文字宽度变化
            layoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dimen_4);
//            Paint paint = tabView.getPaint();
//            layoutParams.width = (int) paint.measureText(text.toString()) + UtilsManager.dp2px(getContext(), 7);
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.dimen_15);
        } else {//indicator 宽度充满一个tabView
            if (mIndicatorWidth != -1) {
                layoutParams.width = (int) mIndicatorWidth;
            }
        }
        frameLayout.addView(iv, layoutParams);

        //在文字的右上角添加小红点
//        CustomCircleView redPoint = new CustomCircleView(getContext());
//        redPoint.setRoundColor(MidData.theme_DotColor);
//        LayoutParams pointLps = new LayoutParams(UtilsManager.dp2px(getContext(), 6), UtilsManager.dp2px(getContext(), 6));
//        pointLps.setMargins((int) tabView.getPaint().measureText(text.toString()) / 2 + UtilsManager.dp2px(getContext(), 7) / 2, UtilsManager.dp2px(ctx, 8), 0, 0);
//        pointLps.gravity = Gravity.CENTER_HORIZONTAL;
//        redPoint.setVisibility(GONE);
//
//        frameLayout.addView(redPoint, pointLps);
//        pointViews.add(redPoint);

        if (isAverageView) {
            mTabLayout.addView(frameLayout, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
        } else {
            mTabLayout.addView(frameLayout, new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        }
        tabViews.add(tabView);
        iv_indicators.add(iv);

    }

//    public CustomCircleView getPointPosition(int index) {
//        if (index < pointViews.size() && index >= 0) {
//            return pointViews.get(index);
//        } else {
//            return null;
//        }
//    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
        }
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            Log.e(getClass().getSimpleName(), "ViewPager does not have adapter instance.");
            return;
        }
        mViewPager = view;
        view.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mTabLayout.removeAllViews();
        tabViews.clear();
        pointViews.clear();
        iv_indicators.clear();
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter == null) {
            return;
        }
        IconPagerAdapter iconAdapter = null;
        if (adapter instanceof IconPagerAdapter) {
            iconAdapter = (IconPagerAdapter) adapter;
        }
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            int iconResId = 0;
            if (iconAdapter != null) {
                iconResId = iconAdapter.getIconResId(i);
            }
            addTab(i, title, iconResId);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        if (mViewPager == null) {
            Log.e(getClass().getSimpleName(), "ViewPager has not been bound.");
            return;
        }
        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item, smoothScroll);

        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
                tabViews.get(i).setTextColor(textSelectedColor);
                iv_indicators.get(i).setBackgroundResource(indicatorSelectedColor);
                tabViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_PX, selectTextSize);
            } else {
                tabViews.get(i).setTextColor(textUnSelectColor);
                iv_indicators.get(i).setBackgroundResource(indicatorUnSelectColor);
                tabViews.get(i).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        }
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }


    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    private class TabView extends android.support.v7.widget.AppCompatTextView {
        private int mIndex;

        public TabView(Context context) {
            super(context, null, R.attr.vpiTabPageIndicatorStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Re-measure if we went beyond our maximum size.
            if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            }
        }

        public int getIndex() {
            return mIndex;
        }
    }

}
