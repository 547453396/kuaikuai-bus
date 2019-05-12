package cn.kuaikuai.common.image;

import android.content.Context;

import com.bumptech.glide.Glide;

/**
 * 内存管理
 * 图片等的缓存，在可用内存较低的时候可以清理掉
 * JXH
 * 2018/7/12
 */
public class MemoryControl {

    public static void onLowMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    public static void onTrimMemory(Context context, int level) {
        Glide.get(context).trimMemory(level);
    }
}
