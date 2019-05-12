package cn.kuaikuai.base.model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import cn.kuaikuai.common.net.callback.ApiCallback;
import cn.kuaikuai.common.net.subscriber.ApiCallbackSubscriber;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BaseModel {
    private Context mContext;
    LifecycleProvider mLifecycleProvider;

    public BaseModel(@NonNull Context context) {
        this.mContext = context;
        mLifecycleProvider = getActivityLifecycleProvider();
    }

    public BaseModel(@NonNull Context context, @NonNull LifecycleProvider lifecycleProvider) {
        this.mContext = context;
        mLifecycleProvider = lifecycleProvider;
    }

    @Nullable
    private LifecycleProvider getActivityLifecycleProvider() {
        LifecycleProvider provider = null;
        if (null != mContext && mContext instanceof LifecycleProvider) {
            provider = (LifecycleProvider) mContext;
        }
        return provider;
    }


    protected Observable subscribe(Observable observable, Observer observer) {
        if (mLifecycleProvider != null) {
            if (mLifecycleProvider instanceof Fragment) {
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mLifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                        .subscribe(observer);
            } else if (mLifecycleProvider instanceof Activity) {
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mLifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(observer);
            }
        }
        return observable;
    }

    protected void subscribe(Observable observable, ApiCallback callback) {
        if (mLifecycleProvider != null) {
            if (mLifecycleProvider instanceof Fragment) {
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mLifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                        .subscribe(new ApiCallbackSubscriber(mContext, callback));
            } else if (mLifecycleProvider instanceof Activity) {
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mLifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(new ApiCallbackSubscriber(mContext, callback));
            }
        }
    }
}
