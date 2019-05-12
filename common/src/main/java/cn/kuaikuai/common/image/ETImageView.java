package cn.kuaikuai.common.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.util.AttributeSet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.weili.common.R;

/**
 * Created by etouch on 14-10-8.
 * 扩展ImageView,支持以下功能：
 * 1、正圆形图片(可设置边框及颜色)
 * 2、圆角图片
 */
public class ETImageView extends android.support.v7.widget.AppCompatImageView {

    /**
     * 图片显示默认模式、正圆形模式、圆角矩形模式
     */

    public static final int NORMAL = 1;
    public static final int CIRCLE = 2;
    public static final int ROUNDED = 3;
    public static final int ROUNDED_TOP_LEFT = 4;
    public static final int ROUNDED_TOP_RIGHT = 5;
    public static final int ROUNDED_BOTTOM_LEFT = 6;
    public static final int ROUNDED_BOTTOM_RIGHT = 7;

    public static final int ROUND_SIZE = 16;

    @IntDef({NORMAL, CIRCLE, ROUNDED, ROUNDED_TOP_LEFT, ROUNDED_TOP_RIGHT, ROUNDED_BOTTOM_LEFT, ROUNDED_BOTTOM_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayMode {
    }

    private int mImageMode = NORMAL;
    private RectF mDrawableRectF = new RectF();
    private Matrix mShaderMatrix = new Matrix();
    private Paint mBitmapPaint = new Paint();
    private Paint mBorderPaint = new Paint();
    /**
     * 单独画圆角使用
     */
    private Paint mRoundPaint = new Paint();
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);

    /**
     * 图片源
     */
    private Bitmap mBitmap;
    /**
     * 位图渲染器
     */
    private BitmapShader mBitmapShader;
    private int mBitmapResId = 0;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mDrawableRadius;
    private float mBorderRadius;
    private boolean isHasInitPaint = false;

    /**
     * 圆形图片的边框颜色 默认白色
     */
    private int mBorderColor = Color.WHITE;
    /**
     * 圆形图片的边框宽度 默认为0
     */
    private int mBorderWidth = 0;
    /**
     * 圆角图片的弧度
     */
    private int mRoundedPixel = ROUND_SIZE;
    /**
     * 四个边是否为圆角
     */
    private boolean mRoundLeftTop, mRoundRightTop, mRoundLeftBottom, mRoundRightBottom;
    private float mRatio = -1;

    public ETImageView(Context context) {
        super(context);
        Init(null);
    }

    public ETImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(attrs);
    }

    public ETImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Init(attrs);
    }

    private void Init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Module_Common_ETImageView);
            int anInt = typedArray.getInt(R.styleable.Module_Common_ETImageView_mode, NORMAL);
            setDisplayMode(anInt);

            int roundSize = typedArray.getInt(R.styleable.Module_Common_ETImageView_round_size,ROUND_SIZE);
            setImageRoundedPixel(roundSize);

            float dimension = typedArray.getDimension(R.styleable.Module_Common_ETImageView_border_width, 0);
            int color = typedArray.getColor(R.styleable.Module_Common_ETImageView_border_color, Color.WHITE);
            setImageCircleBorderColorAndWidth(color, (int) dimension);

            mRatio = typedArray.getFloat(R.styleable.Module_Common_ETImageView_ratio, -1);
            typedArray.recycle();
        }
        isHasInitPaint = true;
        mDrawableRectF = new RectF();
        mShaderMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBorderPaint = new Paint();
    }

    /**
     * 设置图片显示模式
     */
    public void setDisplayMode(int mode) {
        mImageMode = mode;
        if (!modeEqual(NORMAL)) {
            setScaleType(ScaleType.CENTER_CROP);
        }
    }

    private boolean modeEqual(@DisplayMode int mode) {
        return mImageMode == mode;
    }

    /**
     * 正圆图片模式时设置边框颜色及宽度
     */
    public void setImageCircleBorderColorAndWidth(int borderColor, int borderWidth) {
        mBorderColor = borderColor;
        mBorderWidth = borderWidth;
    }

    /**
     * 圆角矩形模式时设置圆角像素值
     */
    public void setImageRoundedPixel(int roundedPixel) {
        mRoundedPixel = roundedPixel;
    }

    /**
     * 设置哪个角为圆角
     */
    public void setRound(boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
        mRoundLeftTop = leftTop;
        mRoundRightTop = rightTop;
        mRoundLeftBottom = leftBottom;
        mRoundRightBottom = rightBottom;
        setUpForOnDraw();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setUpForOnDraw();
    }

    @Override
    public void setImageResource(int resId) {
        try {
            if (mBitmapResId == resId) {
                return;
            }
            mBitmapResId = resId;
            if (mBitmapResId != -2) {
                mBitmap = null;
                if (mBitmapResId != -1) {
                    super.setImageResource(resId);
                    mBitmap = getBitmapFromDrawable(getDrawable());
                } else {
                    super.setImageResource(R.drawable.blank);
                }
                setUpForOnDraw();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mBitmapResId = 0;
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setUpForOnDraw();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmapResId = 0;
        mBitmap = bm;
        if (mBitmap == null || mBitmap.isRecycled()) {
            return;
        }
        super.setImageBitmap(bm);
        setUpForOnDraw();
    }

    @Override
    public void setImageURI(Uri uri) {
        mBitmapResId = 0;
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setUpForOnDraw();
    }

    public Bitmap getImageBitmap() {
        return mBitmap;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        if (mRatio != -1) {
            setMeasuredDimension(measuredWidth, (int) (measuredWidth * mRatio));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if (mBitmapResId == 0 && mBitmap != null && mBitmap.isRecycled()) {
                return;
            } else if (mBitmapResId == -1) {
                canvas.drawColor(Color.rgb(0xee, 0xee, 0xee));
            }
            if (modeEqual(CIRCLE)) {
                int po_x = getWidth() / 2, po_y = getHeight() / 2;
                canvas.drawCircle(po_x, po_y, mBorderRadius, mBitmapPaint);
                if (mBorderWidth != 0) {
                    canvas.drawCircle(po_x, po_y, mBorderRadius, mBorderPaint);
                }
            } else if (modeEqual(ROUNDED) || modeEqual(ROUNDED_BOTTOM_RIGHT) ||
                    modeEqual(ROUNDED_BOTTOM_LEFT) || modeEqual(ROUNDED_TOP_RIGHT) || modeEqual(ROUNDED_TOP_LEFT)
                    || mRoundLeftTop || mRoundLeftBottom || mRoundRightTop || mRoundRightBottom) {
                Drawable mDrawable = getDrawable();
                if (mDrawable == null) {
                    return;
                }
                if (mDrawable.getIntrinsicWidth() <= 0 || mDrawable.getIntrinsicHeight() <= 0) {
                    return;
                }
                int layerId = canvas.saveLayer(mDrawableRectF, null, Canvas.ALL_SAVE_FLAG);
                canvas.drawRoundRect(mDrawableRectF, mRoundedPixel, mRoundedPixel, mBitmapPaint);
                if (modeEqual(ROUNDED)) {
                    return;
                }
                //控制单个圆角
                initRoundPaint();
                if (!mRoundLeftTop) {
                    canvas.drawRect(0, 0, mRoundedPixel, mRoundedPixel, mRoundPaint);
                }
                if (!mRoundRightTop) {
                    canvas.drawRect(mDrawableRectF.right - mRoundedPixel,
                            0, mDrawableRectF.right, mRoundedPixel, mRoundPaint);
                }
                if (!mRoundLeftBottom) {
                    canvas.drawRect(0, mDrawableRectF.bottom - mRoundedPixel, mRoundedPixel,
                            mDrawableRectF.bottom, mRoundPaint);
                }
                if (!mRoundRightBottom) {
                    canvas.drawRect(mDrawableRectF.right - mRoundedPixel, mDrawableRectF.bottom - mRoundedPixel,
                            mDrawableRectF.right, mDrawableRectF.bottom, mRoundPaint);
                }
                canvas.restoreToCount(layerId);
            } else {
                super.onDraw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来绘制圆角
     */
    public void initRoundPaint() {
        mRoundPaint.setXfermode(mXfermode);
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setShader(mBitmapShader);
    }


    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setUpForOnDraw() {
        if (mImageMode == NORMAL && !(mRoundLeftTop | mRoundRightTop | mRoundLeftBottom | mRoundRightBottom)) {
            return;
        }
        if (!isHasInitPaint || mBitmap == null) {
            return;
        }
        try {
            mBitmapWidth = mBitmap.getWidth();
            mBitmapHeight = mBitmap.getHeight();
            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            /*************************************/
            int width = getWidth(), height = getHeight();
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setShader(mBitmapShader); // 位图渲染
            if (mImageMode == CIRCLE) {
                mBorderPaint.setStyle(Paint.Style.STROKE);
                mBorderPaint.setAntiAlias(true);
                mBorderPaint.setColor(mBorderColor);
                mBorderPaint.setStrokeWidth(mBorderWidth);
                mBorderRadius = Math.min((width - mBorderWidth) / 2, (height - mBorderWidth) / 2);
            } else if (mImageMode == ROUNDED || mRoundLeftTop || mRoundRightTop || mRoundLeftBottom || mRoundRightBottom) {
                mDrawableRectF.set(0, 0, width, height);
            }
            updateShaderMatrix(width, height);
            /*************************************/

        } catch (Exception e) {
            e.printStackTrace();
        }
        invalidate();
    }

    private void updateShaderMatrix(int viewWidth, int viewHeight) {
        float scale;
        mShaderMatrix.set(null);
        if (mBitmapWidth * viewHeight > viewWidth * mBitmapHeight) {
            scale = viewHeight / (float) mBitmapHeight;
        } else {
            scale = viewWidth / (float) mBitmapWidth;
        }
        mShaderMatrix.setScale(scale, scale);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    /**
     * 将bitmap变成圆形，或圆角矩形。
     */
    private Bitmap circleCropOrRoundRect(@DisplayMode int displaymode) {
        if (displaymode == NORMAL) return null;

        if (mBitmap == null) return null;

        int dwidth = mBitmap.getWidth(), dheight = mBitmap.getHeight(), vwidth = getWidth(), vheight = getHeight();

        float scale; //bitmap相对于view的缩放比例。
        if (dwidth * vheight > vwidth * dheight) {
            scale = (float) vheight / (float) dheight;
        } else {
            scale = (float) vwidth / (float) dwidth;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        int sourceSize = Math.min(dwidth, dheight);
        int x = (dwidth - sourceSize) / 2;
        int y = (dheight - sourceSize) / 2;

        int resultSize = Math.min(getWidth(), getHeight());
        //将原bitmap缩放为view最小边为边长的正方形。
        Bitmap squared = Bitmap.createBitmap(mBitmap, x, y, sourceSize, sourceSize, matrix, true);

        Bitmap result = Bitmap.createBitmap(vwidth, vheight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(mBitmapPaint);
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        if (displaymode == CIRCLE) {
            //圆形。
            float r = resultSize / 2F;
            canvas.drawCircle(r, r, r, paint);
        } else {
            //圆角矩形。
            canvas.drawRoundRect(new RectF(0, 0, vwidth, vheight), mRoundedPixel, mRoundedPixel, paint);
        }
        return result;
    }
}
