package cn.kuaikuai.trip.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by etouch on 15/1/5.
 * 支持显示图标的textView，使用图标字体库icomoon.ttf,具体中每个图标对应的文本代码请@LJG索取
 */
public class ETIconTextView extends TextView{

    public ETIconTextView(Context context) {
        super(context);
        Init();
    }

    public ETIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public ETIconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Init();
    }

    private void Init(){
        /**加入网页链接字体*/
        this.setTypeface(Typeface.createFromAsset(getResources().getAssets(),"icomoon.ttf"));
    }

}
