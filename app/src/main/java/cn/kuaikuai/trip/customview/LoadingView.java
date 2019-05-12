package cn.kuaikuai.trip.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.kuaikuai.trip.R;

/**
 * Loading View
 * 默认是显示loading
 * 还可以用来标志另外两种状态（请求网络失败 和 数据为空）
 */
public class LoadingView extends FrameLayout {

    private TextView mTvHint;
    private OnClick2Refresh clicklistener;
    private String emptyText = "";
    private String errorText = "";
    private ImageView mIvEmpty;
    /**
     * 0 empty 1 error 2 loading
     */
    private int mType = 0;


    /**
     * 初始化时是否默认启动动画
     */
    private boolean mAutoAnim = true;

    public LoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 场景当请求失败 或者 请求结果为空，界面会有空/请求失败的提示，点击它们的时候需要重新请求
     * eg：
     * loading.setClicklistener(new LoadingView.OnClick2Refresh() {
     * public void startLoading() {
     * loading.showLoading();
     * netUnit.getPost(ctx, 1, false);
     * }
     * });
     *
     * @param clicklistener 点击重新请求的回调
     */
    public void setClicklistener(OnClick2Refresh clicklistener) {
        this.clicklistener = clicklistener;
    }

    /**
     * 用于设置背景
     */
//    public void setBackground(int resourceId) {
//        dialog_circle.setBackgroundResource(resourceId);
//    }
//    public void setText(String str) {
//        tipTextView.setText(str);
//    }
//
//    public void setText(int resid) {
//        tipTextView.setText(resid);
//    }
//
//    public void setTextColor(int color) {
//        tipTextView.setTextColor(color);
//    }
//

    /**
     * 设置是否自动加载动画
     */
    public void setAutoAnim(boolean autoAnim) {
        mAutoAnim = autoAnim;
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.simple_progress_dialog, this);// 得到加载view

        ConstraintLayout rl_empty = findViewById(R.id.rl_empty);
        rl_empty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicklistener != null) {
                    clicklistener.startLoading();
                }
            }
        });
        mTvHint = findViewById(R.id.tv_hint);
        mIvEmpty = findViewById(R.id.iv_empty);
        if (attrs != null) {
            TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.LoadingView);
            mTvHint.setText(t.getString(R.styleable.LoadingView_text));// 设置加载信息
            mAutoAnim = t.getBoolean(R.styleable.LoadingView_autoAnim, false);
            t.recycle();
        }
    }

    void loadAni() {
        Drawable drawable = mIvEmpty.getDrawable();
        if (drawable != null && drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).start();
        }
    }

    private void cancelAnim() {
        Drawable drawable = mIvEmpty.getDrawable();
        if (drawable != null && drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).stop();
        }
    }


    public void showError() {
        mType = 1;
        String error = TextUtils.isEmpty(errorText) ? getContext().getResources().getString(R.string.load_failed) : errorText;
        mTvHint.setText(error);
        mTvHint.setVisibility(VISIBLE);
        mIvEmpty.setImageResource(R.drawable.net_error);
        cancelAnim();
        setVisibility(VISIBLE);
    }

    public void showEmpty() {
        mType = 0;
        String empty = TextUtils.isEmpty(emptyText) ? getContext().getResources().getString(R.string.noData) : emptyText;
        mTvHint.setText(empty);
        mTvHint.setVisibility(VISIBLE);
        mIvEmpty.setImageResource(R.drawable.empty_content);
        cancelAnim();
        setVisibility(VISIBLE);
    }

    public void showLoading() {
        mType = 2;
        mIvEmpty.setImageResource(R.drawable.anim_loading);
        mTvHint.setVisibility(GONE);
        loadAni();
        setVisibility(VISIBLE);
    }


    public boolean isShowLoading() {
        return getVisibility() == View.VISIBLE;
    }

    public void dismiss() {
        mIvEmpty.setImageResource(0);
        cancelAnim();
        setVisibility(GONE);
    }

    /**
     * 设置为空时的提示语
     */
    public void setEmptyText(String tips) {
        this.emptyText = tips;
        if (mTvHint != null && mType == 0) {
            mTvHint.setText(emptyText);
        }
    }

    /**
     * 设置错误提示语
     */
    public void setErrorText(String error) {
        this.errorText = error;
        if (mTvHint != null && mType == 1) {
            mTvHint.setText(errorText);
        }

    }

    /**
     * 使用setClicklistener 替换
     */
    @Deprecated
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    /**
     * 使用showLoading showEmpty showLoading  dismiss 替换
     */
    @Deprecated
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            if (mAutoAnim) {
                loadAni();
            }
        } else {
            cancelAnim();
        }
    }

    public interface OnClick2Refresh {
        void startLoading();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoAnim && getVisibility() == VISIBLE) {
            loadAni();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnim();
//        if (mIvloading != null) {
//            Drawable drawable = mIvloading.getDrawable();
//            if (drawable != null && drawable instanceof AnimationDrawable) {
//                AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
//                animationDrawable.stop();
//                for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
//                    Drawable frame = animationDrawable.getFrame(i);
//                    recycleDrawable(frame);
//                }
//            }
//        }

//        if (mIvEmpty != null) {
//            Drawable drawable = mIvEmpty.getDrawable();
//            recycleDrawable(drawable);
//        }
    }


    void recycleDrawable(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }
}
