package cn.kuaikuai.common.net.func;

import cn.kuaikuai.common.net.exception.ApiException;
import cn.kuaikuai.common.net.mode.ApiResult;
import rx.Observable;
import rx.functions.Func1;

/**
 * ApiResult<T>转T
 */
public class ApiDataFunc<T> implements Func1<ApiResult<T>, Observable<T>> {
    public ApiDataFunc() {
    }

    @Override
    public Observable<T> call(ApiResult<T> response) {
        if (ApiException.isSuccess(response)) {
            return Observable.just(response.getData());
        } else {
//            String errorMessage = "code:" + response.getStatus() + ";message:" + response.getDesc();
            return Observable.error(new ApiException(new Throwable(response.getDesc()), response.getStatus()));
        }
    }
}
