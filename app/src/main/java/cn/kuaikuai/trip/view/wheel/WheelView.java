package cn.kuaikuai.trip.view.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.List;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.utils.UtilsManager;

public class WheelView extends View {
    /**
     * Scrolling duration
     */
    private static final int SCROLLING_DURATION = 400;

    /**
     * Minimum delta for scrolling
     */
    private static final int MIN_DELTA_FOR_SCROLLING = 1;

    /** Current value & label text color */
//	private static int VALUE_TEXT_COLOR = MidData.theme_TextColor;

    /**
     * Items text color
     */
    private static final int ITEMS_TEXT_COLOR = 0xff999999;

    /**
     * Top and bottom shadows colors
     */
    private static final int[] SHADOWS_COLORS = new int[]{0x00000000,
            0x00000000, 0x00000000};

    /**
     * Additional items height (is added to standard text item height)
     */
    private final int ADDITIONAL_ITEM_HEIGHT = UtilsManager.dp2px(
            getContext(), 28);

    /**
     * Text size
     */
    public int TEXT_SIZE = UtilsManager.dp2px(getContext(), 20);

    public int AMPM_TEXT_SIZE = UtilsManager.dp2px(getContext(), 20);

    /**
     * Top and bottom items offset (to hide that)
     */
    // private final int ITEM_OFFSET = TEXT_SIZE / 5;
    private final int ITEM_OFFSET = 0;

    /**
     * Additional width for items layout
     */
    private static final int ADDITIONAL_ITEMS_SPACE = 0;

    /**
     * Label offset
     */
    private static final int LABEL_OFFSET = 8;

    /**
     * Left and right padding value
     */
    private static final int PADDING = 0;

    /**
     * Default count of visible items
     */
    private static final int DEF_VISIBLE_ITEMS = 3;
    private Context ctx;

    // Wheel Values
    private WheelAdapter adapter = null;
    private int currentItem = 0;

    // Widths
    private int itemsWidth = 0;
    private int labelWidth = 0;
    private int leftLabelWidth = 0;

    // Count of visible items
    private int visibleItems = DEF_VISIBLE_ITEMS;

    // Item height
    private int itemHeight = 0;

    // Text paints
    private TextPaint itemsPaint;
    private TextPaint valuePaint;
    private TextPaint ampmPaint;

    // Layouts
    private StaticLayout itemsLayout;
    private StaticLayout labelLayout;
    private StaticLayout valueLayout;
    private StaticLayout leftLabelLayout;

    // Label & background
    private String label;
    private Drawable centerDrawable;
    private String leftLabel = "";

    // Shadows drawables
    private GradientDrawable topShadow;
    private GradientDrawable bottomShadow;

    // Scrolling
    private boolean isScrollingPerformed;
    private int scrollingOffset;

    // Scrolling animation
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private int lastScrollY;

    // Cyclic
    boolean isCyclic = false;

    // Listeners
    private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
    private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();

    // leftLabel
//	private Paint leftPaint;
    //
    float itemTextWidth = 0;
    boolean isItemsCenter = false;
    Typeface face;
    public boolean is24HourType = true;
    boolean isAm;
    String amLabel = "";
    String pmLabel = "";
    /**
     * 向上滑为1，向下为0
     */
    int isUp = -1;
    private boolean isSmallText;

    private boolean hasSetColor = false;
    private int selectedColor = 0;

    /**
     * Constructor
     */
    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readStyles(context, attrs);
        initData(context);
    }

    private void readStyles(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.wheel_selected_color);
        if (a.hasValue(R.styleable.wheel_selected_color_selectedColor)) {
            hasSetColor = true;
            selectedColor = a.getColor(R.styleable.wheel_selected_color_selectedColor, 0);
        }
        a.recycle();
    }

    /**
     * Constructor
     */
    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readStyles(context, attrs);
        initData(context);
    }

    /**
     * Constructor
     */
    public WheelView(Context context) {
        super(context);
        initData(context);
    }

    /**
     * Initializes class data
     *
     * @param context the context
     */
    private void initData(Context context) {
        ctx = context;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        AMPM_TEXT_SIZE = (int) (12 * (float) dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        face = Typeface.createFromAsset(getResources().getAssets(),
                "etouch_cg.ttf");
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);

        scroller = new Scroller(context);
        amLabel = context.getString(R.string.am);
        pmLabel = context.getString(R.string.pm);
        // leftPaint=new Paint(Paint.FAKE_BOLD_TEXT_FLAG);
        // leftPaint.setAntiAlias(true);
        // leftPaint.setTextSize(TEXT_SIZE);
        // leftPaint.setColor(VALUE_TEXT_COLOR);
    }

    /**
     * Gets wheel adapter
     *
     * @return the adapter
     */
    public WheelAdapter getAdapter() {
        return adapter;
    }

    /**
     * Sets wheel adapter
     *
     * @param adapter the new wheel adapter
     */
    public void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        invalidateLayouts();
        invalidate();
    }

    /**
     * Set the the specified scrolling interpolator
     *
     * @param interpolator the interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        scroller.forceFinished(true);
        scroller = new Scroller(getContext(), interpolator);
    }

    /**
     * Gets count of visible items
     *
     * @return the count of visible items
     */
    public int getVisibleItems() {
        return visibleItems;
    }

    /**
     * Sets count of visible items
     *
     * @param count the new count
     */
    public void setVisibleItems(int count) {
        visibleItems = count;
        invalidate();
    }

    /**
     * Gets label
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label
     *
     * @param newLabel the label to set
     */
    public void setLabel(String newLabel) {
        if (label == null || !label.equals(newLabel)) {
            label = newLabel;
            labelLayout = null;
            invalidateLayouts();
            calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
        }
    }

    public String getLeftLabel() {
        return leftLabel;
    }

    public void setLeftLabel(String leftLabel) {
        this.leftLabel = leftLabel;
        invalidateLayouts();
        calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
    }

    /**
     * Adds wheel changing listener
     *
     * @param listener the listener
     */
    public void addChangingListener(OnWheelChangedListener listener) {
        changingListeners.add(listener);
    }

    /**
     * Removes wheel changing listener
     *
     * @param listener the listener
     */
    public void removeChangingListener(OnWheelChangedListener listener) {
        changingListeners.remove(listener);
    }

    public void clearChangingListener() {
        changingListeners.clear();
    }

    /**
     * Notifies changing listeners
     *
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnWheelChangedListener listener : changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * Adds wheel scrolling listener
     *
     * @param listener the listener
     */
    public void addScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.add(listener);
    }

    /**
     * Removes wheel scrolling listener
     *
     * @param listener the listener
     */
    public void removeScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.remove(listener);
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * Gets current value
     *
     * @return the current value
     */
    public int getCurrentItem() {
        return currentItem;
    }

    public boolean isUp() {
        return isUp == 1;
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index    the item index
     * @param animated the animation flag
     */
    public void setCurrentItem(int index, boolean animated) {
        if (adapter == null || adapter.getItemsCount() == 0) {
            return; // throw?
        }
        if (index < 0 || index >= adapter.getItemsCount()) {
            if (isCyclic) {
                while (index < 0) {
                    index += adapter.getItemsCount();
                }
                index %= adapter.getItemsCount();
            } else {
                return; // throw?
            }
        }
        if (index != currentItem) {
            if (animated) {
                scroll(index - currentItem, SCROLLING_DURATION);
            } else {
                invalidateLayouts();

                int old = currentItem;
                currentItem = index;

                if (!is24HourType && isUp >= 0) {
                    if (isUp == 1 && currentItem == adapter.getItemsCount() - 1) {
                        isAm = !isAm;
                        if (isAm) {
                            setLeftLabel(amLabel);
                        } else {
                            setLeftLabel(pmLabel);
                        }
                    } else if (isUp == 0
                            && currentItem == adapter.getItemsCount() - 2) {
                        isAm = !isAm;
                        if (isAm) {
                            setLeftLabel(amLabel);
                        } else {
                            setLeftLabel(pmLabel);
                        }
                    }
                }
                notifyChangingListeners(old, currentItem);
                invalidate();
            }
        }
    }

    /**
     * Sets the current item w/o animation. Does nothing when index is wrong.
     *
     * @param index the item index
     */
    public void setCurrentItem(int index) {
        if (!is24HourType) {
            isAm = amLabel.equals(getLeftLabel());
        }
        setCurrentItem(index, false);
    }

    /**
     * Tests if wheel is cyclic. That means before the 1st item there is shown
     * the last one
     *
     * @return true if wheel is cyclic
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * Set wheel cyclic flag
     *
     * @param isCyclic the flag to set
     */
    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;

        invalidateLayouts();
        invalidate();
    }

    public void setItemsCenter(boolean flag) {
        this.isItemsCenter = flag;
        invalidateLayouts();
        invalidate();
    }

    /**
     * Invalidates layouts
     */
    private void invalidateLayouts() {
        itemsLayout = null;
        valueLayout = null;
        scrollingOffset = 0;
    }

    /**
     * Initializes resources
     */
    private void initResourcesIfNecessary() {
        if (itemsPaint == null) {
            itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            // itemsPaint.density = getResources().getDisplayMetrics().density;
            itemsPaint.setTextSize(TEXT_SIZE);
            itemsPaint.setTypeface(face);
        }
        if (valuePaint == null) {
            // valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
            // | Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
            valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                    | Paint.DITHER_FLAG);
            // valuePaint.density = getResources().getDisplayMetrics().density;
            valuePaint.setTextSize(UtilsManager.dp2px(getContext(), 22));
            valuePaint.setTypeface(face);
        }
        if (ampmPaint == null) {
            // valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
            // | Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
            ampmPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                    | Paint.DITHER_FLAG);
            // valuePaint.density = getResources().getDisplayMetrics().density;
            ampmPaint.setTextSize(AMPM_TEXT_SIZE);
            ampmPaint.setTypeface(face);
        }

        if (centerDrawable == null) {
            centerDrawable = getContext().getResources().getDrawable(
                    R.drawable.wheel_val);
        }

        if (topShadow == null) {
            topShadow = new GradientDrawable(Orientation.TOP_BOTTOM,
                    SHADOWS_COLORS);
        }

        if (bottomShadow == null) {
            bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP,
                    SHADOWS_COLORS);
        }

//		setBackgroundResource(R.drawable.wheel_bg2);
    }

    /**
     * Calculates desired height for layout
     *
     * @param layout the source layout
     * @return the desired layout height
     */
    private int getDesiredHeight(Layout layout) {
        if (layout == null) {
            return 0;
        }

        int desired = getItemHeight() * visibleItems - ITEM_OFFSET * 2
                - ADDITIONAL_ITEM_HEIGHT;

        // Check against our minimum height
        desired = Math.max(desired, getSuggestedMinimumHeight());

        return desired;
    }

    /**
     * Returns text item by index
     *
     * @param index the item index
     * @return the item or null
     */
    private String getTextItem(int index) {
        if (adapter == null || adapter.getItemsCount() == 0) {
            return null;
        }
        int count = adapter.getItemsCount();
        if ((index < 0 || index >= count) && !isCyclic) {
            return null;
        } else {
            while (index < 0) {
                index = count + index;
            }
        }

        index %= count;
        return adapter.getItem(index);
    }

    /**
     * Builds text depending on current value
     *
     * @param useCurrentValue
     * @return the text
     */
    private String buildText(boolean useCurrentValue) {
        StringBuilder itemsText = new StringBuilder();
        int addItems = visibleItems / 2 + 1;

        for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {
            if (useCurrentValue || i != currentItem) {
                String text = getTextItem(i);
                if (text != null) {
                    itemsText.append(text);
                }
            }
            if (i < currentItem + addItems) {
                itemsText.append("\n");
            }
        }

        return itemsText.toString();
    }

    /**
     * Returns the max item length that can be present
     *
     * @return the max length
     */
    private int getMaxTextLength() {
        WheelAdapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }

        int adapterLength = adapter.getMaximumLength();
        if (adapterLength > 0) {
            return adapterLength;
        }

        String maxText = null;
        int addItems = visibleItems / 2;
        for (int i = Math.max(currentItem - addItems, 0); i < Math.min(
                currentItem + visibleItems, adapter.getItemsCount()); i++) {
            String text = adapter.getItem(i);
            if (text != null
                    && (maxText == null || maxText.length() < text.length())) {
                maxText = text;
            }
        }

        return maxText != null ? maxText.length() : 0;
    }

    /**
     * Returns height of wheel item
     *
     * @return the item height
     */
    private int getItemHeight() {
        if (itemHeight != 0) {
            return itemHeight;
        } else if (itemsLayout != null && itemsLayout.getLineCount() > 2) {
            itemHeight = itemsLayout.getLineTop(2) - itemsLayout.getLineTop(1);
            return itemHeight;
        }

        return getHeight() / visibleItems;
    }

    /**
     * Calculates control width and creates text layouts
     *
     * @param widthSize the input layout width
     * @param mode      the layout mode
     * @return the calculated control width
     */
    private int calculateLayoutWidth(int widthSize, int mode) {
        initResourcesIfNecessary();
        if (widthSize == 0) {
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            widthSize = wm.getDefaultDisplay().getWidth() / 2;//屏幕宽度
        }
        int width = widthSize;

        int maxLength = getMaxTextLength();
        if (maxLength > 0) {
            float textWidth = 0;
            if (adapter != null && !TextUtils.isEmpty(adapter.getItem(0)) && TextUtils.isDigitsOnly(adapter.getItem(0))) {
                textWidth = (float) Math.ceil(Layout.getDesiredWidth("0",
                        itemsPaint));
            } else {
                textWidth = (float) Math.ceil(Layout.getDesiredWidth("00",
                        itemsPaint));
            }

            itemsWidth = (int) (maxLength * textWidth);
            itemTextWidth = itemsWidth;
        } else {
            itemsWidth = 0;
            itemTextWidth = 0;
        }
        itemsWidth += ADDITIONAL_ITEMS_SPACE; // make it some more

        labelWidth = 0;
        if (label != null && label.length() > 0) {
            labelWidth = (int) Math.ceil(Layout.getDesiredWidth(label,
                    valuePaint));
        }
        //leftLabel
        leftLabelWidth = 0;
        if (leftLabel != null && leftLabel.length() > 0) {
            leftLabelWidth = (int) Math.ceil(Layout.getDesiredWidth(leftLabel,
                    valuePaint));
        }

        boolean recalculate = false;
        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
            recalculate = true;
        } else {
//			width = itemsWidth + labelWidth + 2 * PADDING;
            width = itemsWidth + labelWidth + leftLabelWidth + 2 * PADDING;
            if (labelWidth > 0) {
                width += LABEL_OFFSET;
            }

            // Check against our minimum width
            width = Math.max(width, getSuggestedMinimumWidth());

            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
                recalculate = true;
            }
        }

        if (recalculate) {
            // recalculate width
            int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
            if (pureWidth <= 0) {
                itemsWidth = labelWidth = 0;
                leftLabelWidth = 0;
            }
            double newWidthItems = 0;
            if (isItemsCenter) {
                itemsWidth += LABEL_OFFSET * 2;
                newWidthItems = (double) (pureWidth + itemsWidth) / 2;
                labelWidth = (int) (pureWidth - newWidthItems);
                leftLabelWidth = labelWidth;
            } else if (labelWidth > 0 && leftLabelWidth <= 0) {
                newWidthItems = (double) itemsWidth * pureWidth / (itemsWidth + labelWidth);
                itemsWidth = (int) newWidthItems;
                labelWidth = pureWidth - itemsWidth;

            } else if (labelWidth <= 0 && leftLabelWidth > 0) {
                newWidthItems = (double) itemsWidth * pureWidth / (itemsWidth + leftLabelWidth);
                itemsWidth = (int) newWidthItems;
                leftLabelWidth = pureWidth - itemsWidth;

            } else if (labelWidth > 0 && leftLabelWidth > 0) {
//				double newWidthItems = (double) itemsWidth * pureWidth/ (itemsWidth + labelWidth);

                newWidthItems = (double) (itemsWidth * pureWidth) / (itemsWidth + labelWidth + leftLabelWidth);
                itemsWidth = (int) newWidthItems;
                leftLabelWidth = (leftLabelWidth * pureWidth) / (itemsWidth + labelWidth + leftLabelWidth);
                labelWidth = pureWidth - itemsWidth - leftLabelWidth;

            } else {
                itemsWidth = pureWidth + LABEL_OFFSET; // no label
            }
        }

        if (itemsWidth > 0) {
            createLayouts(itemsWidth, labelWidth);
        }

        return width;
    }

    /**
     * Creates layouts
     *
     * @param widthItems width of items layout
     * @param widthLabel width of label layout
     */
    private void createLayouts(int widthItems, int widthLabel) {
        if (itemsLayout == null || itemsLayout.getWidth() > widthItems) {
            itemsLayout = new StaticLayout(buildText(isScrollingPerformed),
                    itemsPaint, widthItems,
                    widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE
                            : Layout.Alignment.ALIGN_CENTER, 1,
                    ADDITIONAL_ITEM_HEIGHT, false);
        } else {
            itemsLayout.increaseWidthTo(widthItems);
        }

        if (!isScrollingPerformed
                && (valueLayout == null || valueLayout.getWidth() > widthItems)) {
            String text = getAdapter() != null ? getAdapter().getItem(
                    currentItem) : null;
            valueLayout = new StaticLayout(text != null ? text : "",
                    valuePaint, widthItems,
                    widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE
                            : Layout.Alignment.ALIGN_CENTER, 1,
                    ADDITIONAL_ITEM_HEIGHT, false);
        } else if (isScrollingPerformed) {
            valueLayout = null;
        } else {
            valueLayout.increaseWidthTo(widthItems);
        }

        if (widthLabel > 0 && label != null) {
            if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
                labelLayout = new StaticLayout(label, valuePaint, widthLabel,
                        isItemsCenter ? Layout.Alignment.ALIGN_NORMAL
                                : Layout.Alignment.ALIGN_NORMAL, 1,
                        ADDITIONAL_ITEM_HEIGHT, false);
            } else {
                labelLayout.increaseWidthTo(widthLabel);
            }
        }

        if (leftLabelWidth > 0 && !TextUtils.isEmpty(leftLabel)) {
            // if (leftLabelLayout == null || leftLabelLayout.getWidth() >
            // leftLabelWidth) {
            // UtilsManager.println("初始化StaticLayout："+leftLabel);
//			valuePaint.setTextSize(UtilsManager.dp2px(getContext(), 15));
            if (isSmallText) {
                leftLabelLayout = new StaticLayout(leftLabel, ampmPaint,
                        leftLabelWidth,
                        Layout.Alignment.ALIGN_CENTER
                        , 1,
                        ADDITIONAL_ITEM_HEIGHT, false);
            } else {
                leftLabelLayout = new StaticLayout(leftLabel, valuePaint,
                        leftLabelWidth,
                        isItemsCenter ? Layout.Alignment.ALIGN_OPPOSITE
                                : Layout.Alignment.ALIGN_OPPOSITE, 1,
                        ADDITIONAL_ITEM_HEIGHT, false);

            }
            // } else {
            // leftLabelLayout.increaseWidthTo(leftLabelWidth);
            // }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = calculateLayoutWidth(widthSize, widthMode);

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getDesiredHeight(itemsLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (itemsLayout == null) {
            if (itemsWidth == 0) {
                calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
            } else {
                createLayouts(itemsWidth, labelWidth);
            }
        }

        if (itemsWidth > 0) {
            canvas.save();
            // Skip padding space and hide a part of top and bottom items
            canvas.translate(PADDING, -ITEM_OFFSET);
            drawItems(canvas);
            drawValue(canvas);
            canvas.restore();
        }

        drawCenterRect(canvas);
        drawShadows(canvas);
    }

    /**
     * Draws shadows on top and bottom of control
     *
     * @param canvas the canvas for drawing
     */
    private void drawShadows(Canvas canvas) {
        topShadow.setBounds(0, 0, getWidth(), getHeight() / visibleItems);
        topShadow.draw(canvas);

        bottomShadow.setBounds(0, getHeight() - getHeight() / visibleItems,
                getWidth(), getHeight());
        bottomShadow.draw(canvas);
    }

    /**
     * Draws value and label layout
     *
     * @param canvas the canvas for drawing
     */
    private void drawValue(Canvas canvas) {


        valuePaint.setColor(hasSetColor ? selectedColor : getResources().getColor(R.color.color_theme));
        valuePaint.drawableState = getDrawableState();
        ampmPaint.setColor(hasSetColor ? selectedColor : getResources().getColor(R.color.color_theme));
        ampmPaint.drawableState = getDrawableState();

        Rect bounds = new Rect();
        itemsLayout.getLineBounds(visibleItems / 2, bounds);

        // draw label
        if (labelLayout != null) {
            canvas.save();
            canvas.translate(leftLabelWidth + itemsLayout.getWidth()
                    + LABEL_OFFSET, bounds.top);
            labelLayout.draw(canvas);
            canvas.restore();
        }
        // 左边的文字

        // if(!is24HourType&&!TextUtils.isEmpty(leftLabel)){
        // canvas.drawText(leftLabel,
        // itemsLayout.getWidth()+LABEL_OFFSET-leftLabel.length()*TEXT_SIZE-itemTextWidth-LABEL_OFFSET-2,
        // bounds.top+getItemHeight()/2-8, valuePaint);
        // }
        if (leftLabelLayout != null) {
            canvas.save();
            if (isSmallText) {
                final int center = getHeight() / 2;
                canvas.translate(0, center - (AMPM_TEXT_SIZE >> 1));
            } else {
                canvas.translate(0, bounds.top);
            }
            leftLabelLayout.draw(canvas);
            canvas.restore();
        }

        // draw current value
        if (valueLayout != null) {
            canvas.save();
            canvas.translate(leftLabelWidth, bounds.top + scrollingOffset);
            valueLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 设置LeftLabel为小字体
     */
    public void setSmallTextLeftLabel() {
        this.isSmallText = true;
    }

    /**
     * Draws items
     *
     * @param canvas the canvas for drawing
     */
    private void drawItems(Canvas canvas) {
        canvas.save();

        int top = itemsLayout.getLineTop(1);
        canvas.translate(leftLabelWidth, -top + scrollingOffset);

        itemsPaint.setColor(ITEMS_TEXT_COLOR);
        itemsPaint.drawableState = getDrawableState();
        itemsLayout.draw(canvas);

        canvas.restore();
    }

    /**
     * Draws rect for current value
     *
     * @param canvas the canvas for drawing
     */
    private void drawCenterRect(Canvas canvas) {
        int center = getHeight() / 2;
        int offset = getItemHeight() / 2;
        centerDrawable.setBounds(0, center - offset, getWidth(), center
                + offset);
        centerDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        WheelAdapter adapter = getAdapter();
        if (adapter == null) {
            return true;
        }
        if (!gestureDetector.onTouchEvent(event)
                && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }
        return true;
    }

    /**
     * Scrolls the wheel
     *
     * @param delta the scrolling value
     */
    private void doScroll(int delta) {
        if (delta > 0) {
            isUp = 0;
        } else {
            isUp = 1;
        }
        scrollingOffset += delta;

        int count = scrollingOffset / getItemHeight();
        int pos = currentItem - count;
        if (isCyclic && adapter.getItemsCount() > 0) {
            // fix position by rotating
            while (pos < 0) {
                pos += adapter.getItemsCount();
            }
            pos %= adapter.getItemsCount();
        } else if (isScrollingPerformed) {
            //
            if (pos < 0) {
                count = currentItem;
                pos = 0;
            } else if (pos >= adapter.getItemsCount()) {
                count = currentItem - adapter.getItemsCount() + 1;
                pos = adapter.getItemsCount() - 1;
            }
        } else {
            // fix position
            pos = Math.max(pos, 0);
            pos = Math.min(pos, adapter.getItemsCount() - 1);
        }

        int offset = scrollingOffset;
        if (pos != currentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }

        // update offset
        scrollingOffset = offset - count * getItemHeight();
        if (scrollingOffset > getHeight()) {
            scrollingOffset = scrollingOffset % getHeight() + getHeight();
        }
    }

    // gesture listener
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        public boolean onDown(MotionEvent e) {
            if (isScrollingPerformed) {
                scroller.forceFinished(true);
                clearMessages();
                return true;
            }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            startScrolling();
            doScroll((int) -distanceY);
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            lastScrollY = currentItem * getItemHeight() + scrollingOffset;
            int maxY = isCyclic ? 0x7FFFFFFF : adapter.getItemsCount()
                    * getItemHeight();
            int minY = isCyclic ? -maxY : 0;
            scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY,
                    maxY);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };

    // Messages
    private final int MESSAGE_SCROLL = 0;
    private final int MESSAGE_JUSTIFY = 1;

    /**
     * Set next message to queue. Clears queue before.
     *
     * @param message the message to set
     */
    private void setNextMessage(int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * Clears messages from queue
     */
    private void clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.removeMessages(MESSAGE_JUSTIFY);
    }

    // animation handler
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currY = scroller.getCurrY();
            int delta = lastScrollY - currY;
            lastScrollY = currY;
            if (delta != 0) {
                doScroll(delta);
            }

            // scrolling is not finished when it comes to final Y
            // so, finish it manually
            if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
                currY = scroller.getFinalY();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == MESSAGE_SCROLL) {
                justify();
            } else {
                finishScrolling();
            }
        }
    };

    /**
     * Justifies wheel
     */
    private void justify() {
        if (adapter == null) {
            return;
        }

        lastScrollY = 0;
        int offset = scrollingOffset;
        int itemHeight = getItemHeight();
        boolean needToIncrease = offset > 0 ? currentItem < adapter
                .getItemsCount() : currentItem > 0;
        if ((isCyclic || needToIncrease)
                && Math.abs((float) offset) > (float) itemHeight / 2) {
            if (offset < 0)
                offset += itemHeight + MIN_DELTA_FOR_SCROLLING;
            else
                offset -= itemHeight + MIN_DELTA_FOR_SCROLLING;
        }
        if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
            scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
            setNextMessage(MESSAGE_JUSTIFY);
        } else {
            finishScrolling();
        }
    }

    /**
     * Starts scrolling
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }
    }

    /**
     * Finishes scrolling
     */
    void finishScrolling() {
        if (isScrollingPerformed) {
            notifyScrollingListenersAboutEnd();
            isScrollingPerformed = false;
        }
        invalidateLayouts();
        invalidate();
    }

    /**
     * Scroll the wheel
     * <p>
     * items to scroll
     *
     * @param time scrolling duration
     */
    public void scroll(int itemsToScroll, int time) {
        scroller.forceFinished(true);

        lastScrollY = scrollingOffset;
        int offset = itemsToScroll * getItemHeight();

        scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
        setNextMessage(MESSAGE_SCROLL);

        startScrolling();
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setItemColor(int color) {
        hasSetColor = true;
        selectedColor = color;
        invalidateLayouts();
        invalidate();
    }

}
