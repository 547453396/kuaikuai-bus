package cn.kuaikuai.trip.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import cn.kuaikuai.trip.R;

/**
 * JXH
 * 2018/8/14
 */
public class AutoStartImageView extends android.support.v7.widget.AppCompatImageView {
    public AutoStartImageView(Context context) {
        super(context);
    }

    public AutoStartImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoStartImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setImageResource(R.drawable.anim_loading);
        Drawable drawable = getDrawable();
        if (drawable != null && drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Drawable drawable = getDrawable();
        if (drawable != null && drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).stop();
        }
        setImageResource(0);
    }
}
