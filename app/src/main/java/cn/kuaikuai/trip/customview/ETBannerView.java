package cn.kuaikuai.trip.customview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.ArrayList;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.common.image.ETNetImageView;

/**
 * 横向滚动的banner广告图View
 */
public class ETBannerView extends LinearLayout {

    private Context ctx;
    private int mCurrentScreen = 0;
    private float down_x = 0, downY = 0;
    private int viewWidth = 0;
    private int lastScrollByPix = 0;
    private Scroller mScroller;
    private static final int MAX_SETTLE_DURATION = 350; // ms
    private int totalPage = 0;
    /**
     * 广告自动滚动的时间间隔
     */
    private long AdLongTime = 1000 * 3;
    /**
     * 是否暂停广告图片滚动
     */
    private boolean isPauseAdScroll = false;
    private IndicatorListener listener;
    /**
     * 标识是否可以继续向左滑动
     */
    private boolean adCanGoOnScroll2Left = false;
    /**
     * 标识是否可以继续向右滑动
     */
    private boolean adCanGoOnScroll2Right = true;
    private int mTouchSlop;
    private ViewGroup mParentView;
    private ListView mListView;
    private ScrollView mScrollView;//两者只有一个会存在
    private RecyclerView mRecyclerView;
    // 图片是否是圆角
    private boolean isRoundImage = false;

    public ETBannerView(Context context) {
        super(context);
        this.ctx = context;
        mScroller = new Scroller(getContext());
        ViewConfiguration configuration = ViewConfiguration.get(ctx);
        // 获得可以认为是滚动的距离
        mTouchSlop = configuration.getScaledTouchSlop();
        this.setOrientation(HORIZONTAL);
    }

    public ETBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        ViewConfiguration configuration = ViewConfiguration.get(ctx);
        // 获得可以认为是滚动的距离
        mTouchSlop = configuration.getScaledTouchSlop();
        this.setOrientation(HORIZONTAL);
    }

    /**
     * 设置他的父可滑动View和所在ListView
     */
    public void setParentView(ViewGroup parentView, ListView list) {
        mParentView = parentView;
        mListView = list;
    }

    /**
     * 设置他的父可滑动View和所在ScrollView
     */
    public void setParentViewScrollView(ViewGroup parentView, ScrollView scrollView) {
        mParentView = parentView;
        mScrollView = scrollView;
    }

    /**
     * 设置他的父可滑动View和所在RecyclerView
     */
    public void setParentViewScrollView(ViewGroup parentView, RecyclerView scrollView) {
        mParentView = parentView;
        mRecyclerView = scrollView;
    }


    /**
     * 设置是否可以向左、向右滑动
     */
    private void setAdCanGoOnScrollLeftAndRight(boolean left, boolean right) {
        adCanGoOnScroll2Left = left;
        adCanGoOnScroll2Right = right;
    }

    /**
     * 设置广告自动滚动的时间间隔，单位毫秒
     */
    public void setADLongTime(long longTime) {
        this.AdLongTime = longTime;
    }

    public void removeBannerRunable() {
        handler.removeCallbacks(run);
    }

    public void postBannerRunable() {
        handler.removeCallbacks(run);
        handler.postDelayed(run, AdLongTime);
    }

    // 是否是圆角的imageView
    public void setRoundImage(boolean roundImage) {
        isRoundImage = roundImage;
    }

    /**
     * 给广告设置数据
     */
    public void setADContent(String[] urls, int place) {
        if (urls == null) {
            return;
        }
        this.removeAllViews();
        handler.removeCallbacks(run);
        this.totalPage = urls.length;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        for (int i = 0; i < totalPage; i++) {
            ETNetImageView imageView = new ETNetImageView(ctx);
            this.addView(imageView, params);
            loadImage(imageView, urls[i], place);
        }
        ScrollToScreen(0);
        handler.postDelayed(run, AdLongTime);
    }

    public void setADContent(String[] urls) {
        setADContent(urls, -1);
    }

    public void setADContentRes(ArrayList<String> res) {
        this.removeAllViews();
        handler.removeCallbacks(run);
        this.totalPage = res != null ? res.size() : 0;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        for (int i = 0; i < totalPage; i++) {
            ETNetImageView imageView = new ETNetImageView(ctx);
            this.addView(imageView, params);
            loadImage(imageView, res.get(i), -1);
        }
        ScrollToScreen(0);
        handler.postDelayed(run, AdLongTime);
    }

    /**
     * 图片+底部文字banner
     *
     * @param urls
     * @param titles
     */
    public void setBannerContent(String[] urls, String[] titles) {
        this.removeAllViews();
        handler.removeCallbacks(run);
        this.totalPage = urls != null ? urls.length : 0;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        for (int i = 0; i < totalPage; i++) {
            View view = inflate(ctx, R.layout.yydb_banner_image_view, null);
            ETNetImageView imageView = view.findViewById(R.id.iv_image);
            LinearLayout ll_title = view.findViewById(R.id.ll_title);
            TextView tv_title = view.findViewById(R.id.tv_title);
            if (TextUtils.isEmpty(titles[i])) {
                ll_title.setVisibility(GONE);
            } else {
                ll_title.setVisibility(VISIBLE);
                tv_title.setText(titles[i]);
            }
            this.addView(view, params);
            loadImage(imageView, urls[i], -1);
        }
        ScrollToScreen(0);
        handler.postDelayed(run, AdLongTime);
    }


    /**
     * 设置一个自定义的view
     *
     * @param view 要显示view的数组
     */
    public void setADCustomView(View[] view) {
        if (view == null) {
            return;
        }
        this.removeAllViews();
        handler.removeCallbacks(run);
        this.totalPage = view.length;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        for (int i = 0; i < totalPage; i++) {
            if (view[i] != null) {
                this.addView(view[i], params);
            }
        }
        ScrollToScreen(0);
        handler.postDelayed(run, AdLongTime);
    }

    /**
     * 设置一个自定义的view
     *
     * @param views 要显示view的数组
     */
    public void setADCustomView(ArrayList<View> views) {
        if (views == null) {
            return;
        }
        this.removeAllViews();
        handler.removeCallbacks(run);
        this.totalPage = views.size();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        for (int i = 0; i < totalPage; i++) {
            if (views.get(i) != null) {
                this.addView(views.get(i), params);
            }
        }
        ScrollToScreen(0);
        handler.postDelayed(run, AdLongTime);
    }

    /**
     * 设置一个自定义view，并且插入到相应的位置
     *
     * @param child
     * @param view
     */
    public void setADView(int child, View view) {
        if (view == null || child < 0) {
            return;
        }
        this.totalPage++;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        this.addView(view, child, params);
        ScrollToScreen(child);
        handler.postDelayed(run, AdLongTime);
    }


    Runnable run = new Runnable() {
        @Override
        public void run() {
            if (!isPauseAdScroll) {
                handler.sendEmptyMessage(2);
                handler.postDelayed(this, AdLongTime);
            }
        }
    };

    public void stopScroll() {
        try {
            handler.removeCallbacks(run);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPlay() {
        try {
            handler.removeCallbacks(run);
            handler.postDelayed(run, AdLongTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopScroll();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startPlay();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != VISIBLE) {
            stopScroll();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        viewWidth = this.getMeasuredWidth();
        super.onLayout(changed, l, t, r, b);
        int childLeft = 0;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            if (listener != null) {
                listener.indicatorChanged(mCurrentScreen);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0); // mCurrentScreen*getWidth()
            postInvalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float now_x = ev.getX();
        boolean result = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                down_x = now_x;
                downY = ev.getY();
                handler.removeCallbacks(run);
                if (mParentView != null) {//mParentView对手势时间直接放行
                    mParentView.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                result = onMove(ev);
                break;
        }
        return result;
    }

    /**
     * 如果已经能滑动了事件拦截了
     *
     * @param ev
     * @return
     */
    private boolean onMove(MotionEvent ev) {
        final float x = ev.getX();
        final int xDiff = (int) Math.abs(down_x - x);
        return xDiff > mTouchSlop; //左右滑动
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float now_x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                handler.removeCallbacks(run);
                break;
            case MotionEvent.ACTION_MOVE:
                if (totalPage == 1) {
                    setAdCanGoOnScrollLeftAndRight(false, false);
                    requestDisallowInterceptTouchEvent(false);
                    if (mParentView != null) {
                        mParentView.requestDisallowInterceptTouchEvent(false);
                    }
                    return false;
                }
                if (mListView != null && Math.abs(event.getX() - down_x) > 2 * mTouchSlop) {
                    mListView.requestDisallowInterceptTouchEvent(true);
                } else if (mScrollView != null && Math.abs(event.getX() - down_x) > 2 * mTouchSlop) {
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                }
                if (mRecyclerView != null && Math.abs(event.getX() - down_x) > 2 * mTouchSlop) {
                    mRecyclerView.requestDisallowInterceptTouchEvent(true);
                }
                int nowScrollByPix = (int) (down_x - now_x);
                int totalScreen = totalPage - 1;
                if (mParentView != null) {
                    // 1. X 方向滑动距离大于y方向滑动距离时，表示左右滑动，由BannerView具体处理事件
                    if (Math.abs(now_x - down_x) > Math.abs(event.getY() - downY)) {
                        // 当滑到最左侧或者最右侧时，将事件交给父view，解决外层嵌套横向滚动view的事件冲突;
                        if ((mCurrentScreen == totalScreen && nowScrollByPix > 0)
                                || (mCurrentScreen == 0 && nowScrollByPix < 0)) {
                            mParentView.requestDisallowInterceptTouchEvent(false);
                        } else {
                            mParentView.requestDisallowInterceptTouchEvent(true);
                        }
                        //2 . 否则认为 Y方向在滑动，事件交由父布局处理;
                    } else {
                        mParentView.requestDisallowInterceptTouchEvent(false);
                    }
                }
                if ((adCanGoOnScroll2Left && nowScrollByPix <= 0) || (adCanGoOnScroll2Right && nowScrollByPix >= 0)) {
                    scrollBy(nowScrollByPix - lastScrollByPix, 0);
                    lastScrollByPix = nowScrollByPix;
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int nextScreen = 0;
                if (getScrollX() > (mCurrentScreen * getWidth() + getWidth() / 5)) {
                    nextScreen = mCurrentScreen + 1;
                } else if (getScrollX() < (mCurrentScreen * getWidth() - getWidth() / 5)) {
                    nextScreen = mCurrentScreen - 1;
                } else {
                    nextScreen = mCurrentScreen;
                }
                ScrollToScreen(nextScreen);
                if (Math.abs(now_x - down_x) < 5 && action == MotionEvent.ACTION_UP) {
                    Message msg = new Message();
                    msg.what = 3;
                    msg.arg2 = mCurrentScreen;
                    handler.sendMessage(msg);
                }
                handler.postDelayed(run, AdLongTime);
                break;
        }
        return true;
    }

    /**
     * 滑动到指定的一屏
     *
     * @param nextScreen
     */
    public void ScrollToScreen(int nextScreen) {
        nextScreen = Math.max(0, Math.min(nextScreen, getChildCount() - 1));
        final int newX = nextScreen * getWidth();
        final int delta = newX - (mCurrentScreen * getWidth() + lastScrollByPix);
        float duration = (Math.abs(delta) / (float) viewWidth) * MAX_SETTLE_DURATION;
        mScroller.startScroll(mCurrentScreen * getWidth() + lastScrollByPix, 0,
                delta, 0, (int) duration);
        if (mCurrentScreen != nextScreen) {
            mCurrentScreen = nextScreen;
            Message msg = new Message();
            msg.what = 4;
            msg.arg2 = mCurrentScreen;
            handler.sendMessage(msg);
        }
        lastScrollByPix = 0;
        adCanGoOnScroll2Left = !(mCurrentScreen == 0);
        adCanGoOnScroll2Right = !(mCurrentScreen == totalPage - 1);
        invalidate();
    }

    public int getmCurrentScreen() {
        return mCurrentScreen;
    }

    /**
     * 控制广告图片的滚动
     *
     * @param isPauseAdScroll 是否暂停
     */
    public void controlAdScroll(boolean isPauseAdScroll) {
        this.isPauseAdScroll = isPauseAdScroll;
    }

    Handler handler = new Handler() {
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 2:// 滑动到下一屏
                    int nextScreen = mCurrentScreen + 1;
                    if (nextScreen > totalPage - 1) {
                        nextScreen = 0;
                    }
                    ScrollToScreen(nextScreen);
                    break;
                case 3:// 单击某一屏的事件
                    if (listener != null) {
                        listener.clickBanner(msg.arg2);
                    }
                    break;
                case 4:// 当前显示的屏幕改变
                    if (listener != null) {
                        listener.indicatorChanged(msg.arg2);
                    }
                    break;
            }
        }
    };

    private void loadImage(ETNetImageView etNetImageView, String url, int place) {
        etNetImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (isRoundImage) {
            etNetImageView.setDisplayMode(ETNetImageView.ROUNDED);
            etNetImageView.loadRoundImage(url, place);
        } else {
            etNetImageView.setDisplayMode(ETNetImageView.NORMAL);
            etNetImageView.loadUrl(url, place);
        }
    }


    /**
     * 索引监听
     */
    public interface IndicatorListener {
        void indicatorChanged(int position);

        void clickBanner(int position);
    }

    /**
     * 索引监听
     */
    public void setIndicatorListener(IndicatorListener listener) {
        this.listener = listener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            startPlay();
        } else {
            stopScroll();
        }
    }
}
