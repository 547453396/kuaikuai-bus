package cn.kuaikuai.trip.customview;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

import cn.kuaikuai.trip.R;

public class CustomRefundDialog extends Dialog implements View.OnClickListener {
    private View rootView;
    private Context ctx;
    private ImageView iv_close;
    private ArrayList<TextView> textViews;
    private EditText editText;
    private TextView tv_title, tv_cancel, tv_ok;
    private View.OnClickListener positiveListener = null, negativeListener = null;
    private ArrayList<ItemBean> datas;
    private JSONArray jsonArray;

    public CustomRefundDialog(final Context context) {
        super(context, R.style.no_background_dialog);
        this.ctx = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.layout_refund_dialog, null);
        setContentView(rootView);
        setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        iv_close = rootView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        tv_title = rootView.findViewById(R.id.tv_title);
        editText = rootView.findViewById(R.id.editText);
        tv_cancel = rootView.findViewById(R.id.tv_cancel);
        tv_ok = rootView.findViewById(R.id.tv_ok);
        textViews = new ArrayList<>();
        textViews.add((TextView) rootView.findViewById(R.id.text0));
        textViews.add((TextView) rootView.findViewById(R.id.text1));
        textViews.add((TextView) rootView.findViewById(R.id.text2));
        textViews.add((TextView) rootView.findViewById(R.id.text3));
        textViews.add((TextView) rootView.findViewById(R.id.text4));
        textViews.add((TextView) rootView.findViewById(R.id.text5));
        initData();
    }

    private void initData() {
        datas = new ArrayList<>();
        datas.add(new ItemBean("对方信息不全", false));
        datas.add(new ItemBean("用户态度恶劣", false));
        datas.add(new ItemBean("用户对预测不满意", false));
        datas.add(new ItemBean("用户回复慢", false));
        datas.add(new ItemBean("无理由退款", false));
        datas.add(new ItemBean("用户下错单", false));
        refreshContent();
    }

    private void refreshContent() {
        jsonArray = new JSONArray();
        for (int i = 0; i < textViews.size(); i++) {
            TextView textView = textViews.get(i);
            textView.setTag(i);
            textView.setOnClickListener(this);
            if (i < datas.size()) {
                ItemBean itemBean = datas.get(i);
                textView.setText(itemBean.content);
                if (itemBean.selected) {
                    textView.setBackgroundResource(R.drawable.bg_refund_item_selected);
                    textView.setTextColor(ctx.getResources().getColor(R.color.color_FFAD0B));
                    jsonArray.put(itemBean.content);
                } else {
                    textView.setBackgroundResource(R.drawable.bg_refund_item_normal);
                    textView.setTextColor(ctx.getResources().getColor(R.color.color_91999f));
                }
            } else {
                textView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String getRefundString() {
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        String edit = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(edit)) {
            jsonArray.put(edit);
        }
        return jsonArray.toString();
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
    }

    /**
     * 设置按钮文字和监听
     */
    public CustomRefundDialog setPositiveButton(String text, View.OnClickListener listener) {
        positiveListener = listener;
        tv_ok.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        tv_ok.setText(text);
        tv_ok.setOnClickListener(this);
        return this;
    }

    public CustomRefundDialog setPositiveButton(int resId, View.OnClickListener listener) {
        setPositiveButton(ctx.getString(resId), listener);
        return this;
    }

    /**
     * 设置按钮文字和监听
     */
    public CustomRefundDialog setNegativeButton(String text, View.OnClickListener listener) {
        negativeListener = listener;
        tv_cancel.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        tv_cancel.setText(text);
        tv_cancel.setOnClickListener(this);
        return this;
    }

    /**
     * 设置按钮文字和监听
     */
    public CustomRefundDialog setNegativeButton(int resId, View.OnClickListener listener) {
        setNegativeButton(ctx.getString(resId), listener);
        return this;
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag();
        if (object != null) {
            int index = (int) object;
            datas.get(index).selected = !datas.get(index).selected;
            refreshContent();
            return;
        }

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

    private class ItemBean {
        public String content;
        public boolean selected;

        public ItemBean(String content, boolean selected) {
            this.content = content;
            this.selected = selected;
        }
    }

    @Override
    public void dismiss() {
        hideKeyBord();
        super.dismiss();
    }

    private void hideKeyBord() {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            Window window = getWindow();
            if (window != null) {
                imm.hideSoftInputFromWindow(window.getDecorView().getWindowToken(), 0);
            }
        }
    }
}
