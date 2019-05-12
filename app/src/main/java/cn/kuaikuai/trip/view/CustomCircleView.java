package cn.kuaikuai.trip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.common.DensityUtil;


/**
 * 画一个圆形的view, isChecked为true时会在中间绘制一个勾
 * circleColor 圆的颜色
 * textColor 未读数的颜色
 * num 未读数
 */
public class CustomCircleView extends View {

    private Context ctx;
    /**
     * 圆的颜色
     */
    private int roundColor, textColor;
    private int textSize;
    /**
     * 未读消息数
     */
    private int num;
    private Paint paint;
    private boolean isChecked = false;

    public CustomCircleView(Context context) {
        this(context, null);
        ctx = context;
    }

    public CustomCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        ctx = context;
    }

    public CustomCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL); //设置实心
        paint.setAntiAlias(true);  //消除锯齿
        ctx = context;

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.CustomCircleView);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.CustomCircleView_circleColor, getResources().getColor(R.color.circle_color));
        isChecked = mTypedArray.getBoolean(R.styleable.CustomCircleView_isChecked, false);
        textColor = mTypedArray.getColor(R.styleable.CustomCircleView_textColor, Color.WHITE);

        textSize = DensityUtil.dip2px(ctx, 10);

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centreX = getWidth() / 2;
        int centreY = getHeight() / 2;
        int radius = Math.min(centreX, centreY); //圆的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStrokeWidth(radius); //设置圆环的宽度
        canvas.drawCircle(centreX, centreY, radius, paint); //画出圆
        float startX = (float) (centreX - radius / Math.sqrt(2));
        float startY = (float) (centreY - radius / Math.sqrt(2));
        if (isChecked) {
            drawCheckBitmap(canvas, startX, startY);//画勾
        } else if (num > 0) {
            paint.setColor(textColor);
            int width;
            String numStr;
            if (num <= 99) {
                numStr = num + "";
                textSize = DensityUtil.dip2px(ctx, 10);
                paint.setTextSize(textSize);
            } else {
                textSize = DensityUtil.dip2px(ctx, 7);
                paint.setTextSize(textSize);
                numStr = "99+";
            }
            width = getTextWidth(paint, numStr);
            canvas.drawText(numStr, centreX - width / 2, centreY + textSize / 2 - DensityUtil.dip2px(ctx, 1), paint);
        }
    }

    private void drawCheckBitmap(Canvas canvas, float startx, float starty) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_ic_ok_w);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, startx, starty, paint);
            bitmap.recycle();
        }
    }

    private int getTextWidth(Paint paint, String text) {
        float[] widths = new float[text.length()];
        paint.getTextWidths(text, widths);
        int length = widths.length, nowLength = 0, a;
        for (a = 0; a < length; a++) {
            nowLength += widths[a];
        }
        return nowLength;
    }

    public void setNum(int num) {
        this.num = num;
        invalidate();
    }

    public int getRoundColor() {
        return roundColor;
    }

    public void setRoundColor(int color) {
        this.roundColor = color;
        postInvalidate();
    }

    private boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
        postInvalidate();
    }
}
