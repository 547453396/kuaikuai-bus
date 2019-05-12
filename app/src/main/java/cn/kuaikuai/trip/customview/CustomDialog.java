package cn.kuaikuai.trip.customview;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.kuaikuai.trip.R;

public class CustomDialog extends Dialog implements View.OnClickListener {

    private View rootView;
    private Context ctx;
    private ImageView iv_close;
    private TextView tv_title, tv_content, tv_cancel, tv_ok;
    private View.OnClickListener positiveListener = null, negativeListener = null;

    public CustomDialog(final Context context) {
        super(context, R.style.no_background_dialog);
        this.ctx = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.layout_custom_dialog, null);
        setContentView(rootView);
        setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        iv_close = rootView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        tv_title = rootView.findViewById(R.id.tv_title);
        tv_content = rootView.findViewById(R.id.tv_content);
        tv_cancel = rootView.findViewById(R.id.tv_cancel);
        tv_ok = rootView.findViewById(R.id.tv_ok);
        tv_ok.setVisibility(View.GONE);
        tv_cancel.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
    }

    public void setMessage(String content) {
        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
        }
    }

    /**
     * 设置按钮文字和监听
     */
    public CustomDialog setPositiveButton(String text, View.OnClickListener listener) {
        positiveListener = listener;
        tv_ok.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        tv_ok.setText(text);
        tv_ok.setOnClickListener(this);
        return this;
    }

    public CustomDialog setPositiveButton(int resId, View.OnClickListener listener) {
        setPositiveButton(ctx.getString(resId),listener);
        return this;
    }

    /**
     * 设置按钮文字和监听
     */
    public CustomDialog setNegativeButton(String text, View.OnClickListener listener) {
        negativeListener = listener;
        tv_cancel.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        tv_cancel.setText(text);
        tv_cancel.setOnClickListener(this);
        return this;
    }

    /**
     * 设置按钮文字和监听
     */
    public CustomDialog setNegativeButton(int resId, View.OnClickListener listener) {
        setNegativeButton(ctx.getString(resId),listener);
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_ok) {
            if (positiveListener != null) {
                positiveListener.onClick(v);
            }
        } else if (v == tv_cancel) {
            if (negativeListener != null) {
                negativeListener.onClick(v);
            }
        }
        dismiss();
    }

    private boolean isDismissDialogWhenTouch = true;
    /**
     * 设置是否点击对话框外任意区域关闭对话框
     */
    public void setEnableDismissDialogWhenTouch(boolean b) {
        isDismissDialogWhenTouch = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDismissDialogWhenTouch) {
            dismiss();
        }
        return super.onTouchEvent(event);
    }

}
