package cn.kuaikuai.trip.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by yll on 16/11/17.
 * 将ScrollView的 onScrollChanged方法暴露出来
 */

public class ETScrollView extends ScrollView {
    public ETScrollView(Context context) {
        super(context);
    }

    public ETScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ETScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface ScrollViewListener {

        void onScrollChanged(int x, int y, int oldx, int oldy);

    }

    private ScrollViewListener scrollViewListener = null;

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(x, y, oldx, oldy);
        }
    }
}
