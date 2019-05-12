package cn.kuaikuai.trip.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 投放数据跳转
 * JXH
 * 2018/7/31
 */
public class IntentScheme {

    public static boolean checkIfSpecialUrl(final Context ctx, String url) {
        return checkIfSpecialUrl(ctx, url, null);
    }

    /**
     * 判断url中的链接是否是要拦截的链接，如果是则返回true并执行对应的操作，比如打开对应页面或弹框等
     */
    public static boolean checkIfSpecialUrl(final Context ctx, String url, Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        boolean result = checkIfSpecialUrlAndInitIntent(ctx, url, intent);
        try {
            if (intent.getComponent() != null
                    && !TextUtils.isEmpty(intent.getComponent().getClassName())) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断url中的链接是否是要拦截的链接,如果是则返回true，并将intent初始化为对应页面的intent，如果是弹框则直接弹出否则只初始化intent
     */
    public static boolean checkIfSpecialUrlAndInitIntent(Context ctx, String url, Intent intent) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return false;
    }

}
