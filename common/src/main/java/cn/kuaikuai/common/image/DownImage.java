package cn.kuaikuai.common.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.ExecutionException;

/**
 * 下载图片
 * JXH
 * 2018/8/7
 */
public class DownImage {

    public static void imageBitmap(Context context, String imgUrl, final BitmapCallback callback) {
        Glide.with(context).asBitmap().load(imgUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (callback != null) {
                    callback.onSuccessBitmap(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (callback != null) {
                    callback.onFail(null);
                }
            }
        });
    }


    public static void imageDrawable(Context context, String imgUrl, final DrawableCallback callback) {
        Glide.with(context).asDrawable().load(imgUrl).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (callback != null) {
                    callback.onSuccessDrawable(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (callback != null) {
                    callback.onFail(null);
                }
            }
        });
    }

    public static Bitmap getBitmap(Context context, String url, int width, int height) {
        try {
            Bitmap bitmap = Glide.with(context).asBitmap().load(url).into(width, height).get();
            return bitmap;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public interface BitmapCallback {
        void onSuccessBitmap(Bitmap bitmap);

        void onFail(Throwable throwable);
    }

    interface DrawableCallback {

        void onSuccessDrawable(Drawable drawable);

        void onFail(@Nullable Throwable throwable);
    }

}
