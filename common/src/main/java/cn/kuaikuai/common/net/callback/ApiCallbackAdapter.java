package cn.kuaikuai.common.net.callback;

import cn.kuaikuai.common.net.exception.ApiException;

/**
 * JXH
 * 2018/7/11
 */
public class ApiCallbackAdapter<T> extends ApiCallback<T> {
    @Override
    public void onStart() {

    }

    @Override
    public void onError(ApiException e) {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onNext(T t) {

    }
}
