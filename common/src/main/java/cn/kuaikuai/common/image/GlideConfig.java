package cn.kuaikuai.common.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import cn.weili.common.BuildConfig;

/**
 * glide 配置接口，文档要求AppGlideModule必须定义在application module中
 * 暂时先定义在library module中，后续有问题再移入application中
 */
@GlideModule
public class GlideConfig extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setLogLevel(BuildConfig.DEBUG ? Log.DEBUG : Log.ERROR);
    }
}