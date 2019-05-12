package cn.kuaikuai.trip.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import cn.kuaikuai.trip.R;

public class ETIconButtonTextView extends android.support.v7.widget.AppCompatImageView {
    /**各个icon*/
    /**下箭头*/
    public static final int TYPE_NARROW = 4;
    /**返回*/
    public static final int TYPE_BACK = 2;
    /**分享*/
    public static final int TYPE_SHARE = 9;
    /**删除*/
    public static final int TYPE_DELETE = 12;
    private int buttonType;

    public ETIconButtonTextView(Context context) {
        super(context);
        init();
    }

    public ETIconButtonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ETIconButtonTextView);
        buttonType = typedArray.getInt(R.styleable.ETIconButtonTextView_buttonType, 0);
        typedArray.recycle();
        init();
    }

    private void init(){
        setButtonType(buttonType);
    }

    /**设置按钮类型*/
    public void setButtonType(int type){
        switch (type){
            case TYPE_NARROW:
                setImageResource(R.drawable.icon_home_xiangxiajiantou);
                break;
            case TYPE_BACK:
                setImageResource(R.drawable.back_black);
                break;
            case TYPE_SHARE:
                setImageResource(R.drawable.icon_share);
                break;
            case TYPE_DELETE:
                setImageResource(R.drawable.icon_close);
                break;
        }
    }
}
