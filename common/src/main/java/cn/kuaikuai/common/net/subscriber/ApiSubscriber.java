package cn.kuaikuai.common.net.subscriber;

import android.accounts.NetworkErrorException;
import android.content.Context;

import java.lang.ref.WeakReference;

import cn.kuaikuai.common.assist.Network;
import cn.kuaikuai.common.net.exception.ApiException;
import cn.kuaikuai.common.net.mode.ApiCode;
import rx.Subscriber;

/**
 * @Description: API统一订阅者，采用弱引用管理上下文，防止内存泄漏
 */
public abstract class ApiSubscriber<T> extends Subscriber<T> {
    public WeakReference<Context> contextWeakReference;

    public ApiSubscriber(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, ApiCode.Request.UNKNOWN));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!Network.isConnected(contextWeakReference.get())) {
            onError(new ApiException(new NetworkErrorException(), ApiCode.Request.NETWORK_ERROR));
        }
    }

    public abstract void onError(ApiException e);
}
