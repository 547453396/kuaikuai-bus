package cn.kuaikuai.common.image;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import cn.weili.common.R;
import cn.kuaikuai.common.DensityUtil;
import cn.kuaikuai.common.LogUtils;

public class ETNetImageView extends ETImageView {

    public static int[] WIDTH_COLLECTION = {110, 160, 210, 240, 320, 480, 640, 720, 1200};

    public static final String IMAGE_URL_WLCC = "http://wlcc-img.weli010.cn";
    public static final String IMAGE_URL_SSY = "static.suishenyun.net";

    /**
     * 宽度：110,160,210,240,320,480,640,720,1200;
     *
     * @param width
     * @return 返回width和上述给定的数值相近的
     */
    public static int getImageWidth(int width) {
        int result = width;
        if (width <= 135) {
            // 110+(160-110)/2
            result = WIDTH_COLLECTION[0];
        } else if (width <= 185) {
            // 160+(210-160)/2
            result = WIDTH_COLLECTION[1];
        } else if (width <= 225) {
            // 210+(240-210)/2
            result = WIDTH_COLLECTION[2];
        } else if (width <= 280) {
            // 240+(320-240)/2
            result = WIDTH_COLLECTION[3];
        } else if (width <= 400) {
            // 320+(480-320)/2
            result = WIDTH_COLLECTION[4];
        } else if (width <= 560) {
            // 480+(640-480)/2
            result = WIDTH_COLLECTION[5];
        } else if (width <= 680) {
            // 640+(720-640)/2
            result = WIDTH_COLLECTION[6];
        } else if (width <= 960) {
            // 720+(1200-720)/2
            result = WIDTH_COLLECTION[7];
        } else {
            result = WIDTH_COLLECTION[8];
        }
        return result;
    }

    public ETNetImageView(Context context) {
        super(context);
    }

    public ETNetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ETNetImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 加载图片
     * 根据布局中的控件大小来加载图片
     *
     * @param url 网络URL
     */
    public void loadUrl(String url) {
        loadUrl(url, R.drawable.blank);
    }

    private int getLoadSize(int size) {
        if (size == ViewGroup.LayoutParams.MATCH_PARENT || size == ViewGroup.LayoutParams.WRAP_CONTENT || size <= 0) {
            size = Target.SIZE_ORIGINAL;
        }
        return size;
    }

    /**
     * 加载图片
     *
     * @param place 占位
     */
    public void loadUrl(String url, @DrawableRes int place) {
        int viewWidth = getViewWidth(this);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        RequestOptions requestOptions = RequestOptions.placeholderOf(place)
                .error(place)
                .centerCrop()
                .override(getLoadSize(layoutParams == null ? -1 : layoutParams.width),
                        getLoadSize(layoutParams == null ? -1 : layoutParams.height));
        Glide.with(this).load(getNewUrl(url, viewWidth)).apply(requestOptions).into(this);
    }

    /**
     * 加载gif图片
     *
     * @param url
     * @param place
     */
    public void loadGif(int url, int place) {
        RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)
                .error(place)
                .placeholder(place);
        Glide.with(this)
                .asGif()
                .apply(options)
                .load(url)
                .into(this);
    }

    public void loadGif(String url, int place) {
        RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)
                .error(place)
                .placeholder(place);
        Glide.with(this)
                .asGif()
                .apply(options)
                .load(url)
                .into(this);
    }

    /**
     * 加载圆形
     */
    public void loadCircleImage(String url) {
        loadCircleImage(url, 0);
    }

    public void loadCircleImage(String url, @DrawableRes int place) {
        loadCircleImage(url, place, place);
    }


    public void loadCircleImage(String url, @DrawableRes int loadHolder, @DrawableRes int errorHolder) {
        int viewWidth = getViewWidth(this);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        RequestOptions options = RequestOptions.placeholderOf(loadHolder)
                .error(errorHolder)
                .transform(new CircleCrop())
                .dontAnimate()
                .override(getLoadSize(layoutParams == null ? -1 : layoutParams.width),
                        getLoadSize(layoutParams == null ? -1 : layoutParams.height));
        Glide.with(this).asDrawable().apply(options).load(getNewUrl(url, viewWidth)).into(this);
    }

    public void loadRoundImage(String url, @DrawableRes int place) {
        loadRoundImage(url, DensityUtil.dip2px(getContext(), 4), place);
    }


    public void loadRoundImage(String url, int radius, @DrawableRes int place) {
        int viewWidth = getViewWidth(this);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        RequestOptions options = RequestOptions.placeholderOf(place)
                .error(place)
                .transforms(new CenterCrop(), new RoundedCorners(radius))
                .dontAnimate()
                .override(getLoadSize(layoutParams == null ? -1 : layoutParams.width),
                        getLoadSize(layoutParams == null ? -1 : layoutParams.height));
        Glide.with(this).asDrawable().load(getNewUrl(url, viewWidth)).apply(options).into(this);
    }

    /**
     * 加载模糊图片
     *
     * @param url
     * @param place
     */
    public void loadBlurImage(String url, @DrawableRes int place) {
        int viewWidth = getViewWidth(this);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        RequestOptions options = RequestOptions.placeholderOf(place)
                .error(place)
                .transform(new BlurTransformation())
                .dontAnimate()
                .override(getLoadSize(layoutParams == null ? -1 : layoutParams.width),
                        getLoadSize(layoutParams == null ? -1 : layoutParams.height));
        Glide.with(this).asDrawable().load(getNewUrl(url, viewWidth)).apply(options).into(this);
    }


    /**
     * 用于加载运营投放的图片
     */
    public void loadImgAD(String url, @DrawableRes int place) {
        RequestOptions options = RequestOptions.placeholderOf(place)
                .error(place)
                .dontAnimate();
        Glide.with(this).asDrawable().load(url).apply(options).into(this);
    }


    /**
     * 用于加载运营投放的图片
     */
    public void loadCircleImgAD(String url, @DrawableRes int place) {
        RequestOptions options = RequestOptions.placeholderOf(place)
                .error(place)
                .transforms(new CenterCrop(), new RoundedCorners(8))
                .dontAnimate();
        Glide.with(this).asDrawable().load(url).apply(options).into(this);
    }

    public void loadTopRoundImage(String url, int place, int radius) {
        int viewWidth = getViewWidth(this);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        RequestOptions options = RequestOptions.placeholderOf(place)
                .error(place)
                .transform(new BlurTransformation())
                .dontAnimate()
                .override(getLoadSize(layoutParams == null ? -1 : layoutParams.width),
                        getLoadSize(layoutParams == null ? -1 : layoutParams.height))
                .transforms(new CenterCrop(), new RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.TOP));
        Glide.with(this).asDrawable().load(getNewUrl(url, viewWidth)).apply(options).into(this);
    }

    public void loadTopRoundImage(String url, int place) {
        loadTopRoundImage(url, place, 8);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }


    /**
     * 若图片地址为测测或随身云下，转换新的图片地址
     *
     * @param maxSize ImageView的最大一边
     */
    public static String getNewUrl(String oldUrl, int maxSize) {
        if (TextUtils.isEmpty(oldUrl)) {
            return "";
        }
//        if (ImageConfig.Instances().containUrl(oldUrl)) {
//            //阿里cdn的图片
//            //https://img.alicdn.com/bao/uploaded/i2/2166475645/O1CN01DSGVSo1rZSGfxyUcc_!!0-item_pic.jpg
//            oldUrl = ImageConfig.Instances().getUrl(oldUrl, maxSize);
//        } else
        if (oldUrl.contains(IMAGE_URL_WLCC) || oldUrl.contains(IMAGE_URL_SSY)) {
            oldUrl += "!w" + getImageWidth(maxSize) + ".jpg";
        }
        return oldUrl;
    }


    /**
     * 获取图片的实际宽度
     */
    public static int getViewWidth(View view) {
        int width = view.getMeasuredWidth();
        if (width > 0) {
            return width;
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            // Get actual image width
            width = view.getMeasuredWidth();
        }
        if (width <= 0 && params != null) {
            // Get layout width parameter
            width = params.width;
        }
        if (width <= 0)
            width = getImageViewFieldValue(view, "mMaxWidth"); // Check maxWidth parameter
        if (width <= 0) {
            DisplayMetrics dm = view.getResources().getDisplayMetrics();
            width = dm.widthPixels;
        }
        return width;
    }


    public void cancelLoad() {
        Glide.with(this).clear(this);
    }

    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void getGlideImagePath(final Context context, final String url,
                                         final int width) {
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = Glide.with(context)
                            .load(getNewUrl(url, width))
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    String path = file.getAbsolutePath();
                    LogUtils.d("mPath:" + path);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
