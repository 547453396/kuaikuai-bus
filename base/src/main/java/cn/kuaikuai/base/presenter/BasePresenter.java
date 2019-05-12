package cn.kuaikuai.base.presenter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.LifecycleProvider;


abstract public class BasePresenter {

    protected Context mContext;

    public BasePresenter(Context context) {
        mContext = context;
    }

    public void doDestroy() {
        mContext = null;
    }

    @Nullable
    protected LifecycleProvider getActivityLifecycleProvider() {
        LifecycleProvider provider = null;
        if (null != mContext && mContext instanceof LifecycleProvider) {
            provider = (LifecycleProvider) mContext;
        }
        return provider;
    }

    /**
     * 业务异常处理
     *
     * @param e
     */
    abstract public void doError(Throwable e);
}
