package cn.kuaikuai.common.net.callback;

import cn.kuaikuai.common.net.exception.ApiException;

public abstract class ApiCallback<T> {
    public abstract void onStart();

    public abstract void onError(ApiException e);

    public abstract void onCompleted();

    public abstract void onNext(T t);
}
