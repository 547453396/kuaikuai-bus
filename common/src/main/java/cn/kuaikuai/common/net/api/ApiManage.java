package cn.kuaikuai.common.net.api;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import cn.weili.common.BuildConfig;
import cn.kuaikuai.common.LogUtils;
import cn.kuaikuai.common.NetUtils;
import cn.kuaikuai.common.assist.ClassUtil;
import cn.kuaikuai.common.net.callback.ApiCallback;
import cn.kuaikuai.common.net.exception.ApiException;
import cn.kuaikuai.common.net.func.ApiDataFunc;
import cn.kuaikuai.common.net.func.ApiErrFunc;
import cn.kuaikuai.common.net.func.ApiFunc;
import cn.kuaikuai.common.net.func.ApiResultFunc;
import cn.kuaikuai.common.net.interceptor.GzipRequestInterceptor;
import cn.kuaikuai.common.net.interceptor.HeadersInterceptor;
import cn.kuaikuai.common.net.mode.ApiCode;
import cn.kuaikuai.common.net.mode.ApiResult;
import cn.kuaikuai.common.net.subscriber.ApiCallbackSubscriber;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 网络操作入口
 */
public class ApiManage {

    private static Context context;
    private static ApiManage sApimanage;
    private ApiService apiService;

    /**
     * 使用baseUrl初始化
     */
    public static void initApiManage(Context context, String baseUrl, Map<String, String> headers) {
        if (sApimanage == null) {
            synchronized (ApiManage.class) {
                if (sApimanage == null) {
                    ApiManage apiManage = new ApiManage();
                    Builder builder = new Builder(context);
                    builder.baseUrl(baseUrl);
                    builder.headers(headers);
                    Retrofit retrofit = builder.build();
                    apiManage.apiService = retrofit.create(ApiService.class);
                    sApimanage = apiManage;
                }
            }
        }
    }

    public static ApiManage getInstance() {
        return checkNotNull(sApimanage, "need call method initApiManage() first");
    }

    /**
     * 可传入自定义的接口服务
     *
     * @param service
     * @param <T>
     * @return
     */
   /* public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }*/

    /**
     * 由外部设置被观察者
     *
     * @param observable
     * @param <T>
     * @return
     */
    public <T> Observable<T> call(Observable<T> observable) {
        return observable.compose(new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(new ApiErrFunc<T>());
            }
        });
    }

    /**
     * 普通Get方式请求，需传入实体类
     *
     * @param url   请求地址，可以是相对地址，也可以是绝对地址（如果和baseUrl域名不同，需要使用绝对地址）
     * @param maps  参数集合
     * @param clazz 实体类
     */
    public <T> Observable<T> get(String url, Map<String, Object> maps, Class<T> clazz) {
        if (BuildConfig.LOG_MODE) {
            LogUtils.i(LogUtils.getParametersToString(url, maps));
        }
        return apiService.get(url, maps).compose(this.norTransformer(clazz));
    }

    /**
     * 普通Get方式请求，无需订阅，只需传入Callback回调
     */
    public <T> Subscription get(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.get(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 普通POST方式请求，需传入实体类
     *
     * @param url        请求地址，可以是相对地址，也可以是绝对地址（如果和baseUrl域名不同，需要使用绝对地址）
     * @param parameters 参数集合
     * @param clazz      实体类
     */
    public <T> Observable<T> post(final String url, final Map<String, Object> parameters, Class<T> clazz) {
        if (BuildConfig.LOG_MODE) {
            LogUtils.i(LogUtils.getParametersToString(url, parameters));
        }
        return apiService.post(url, parameters).compose(this.norTransformer(clazz));
    }

    /**
     * 普通POST方式请求，无需订阅，只需传入Callback回调
     */
    public <T> Subscription post(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.post(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 提交表单方式请求，需传入实体类
     *
     * @param url
     * @param fields
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> form(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, Class<T> clazz) {
        return apiService.postForm(url, fields).compose(this.norTransformer(clazz));
    }

    /**
     * 提交表单方式请求，无需订阅，只需传入Callback回调
     */
    public <T> Subscription form(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, ApiCallback<T> callback) {
        return this.form(url, fields, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 提交Body方式请求，需传入实体类
     *
     * @param url
     * @param body
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> body(final String url, final Object body, Class<T> clazz) {
        return apiService.postBody(url, body).compose(this.norTransformer(clazz));
    }

    public <T> Observable<T> body(final String url, String json, final Map<String, Object> maps, Class<T> clazz) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return apiService.postJson(url, requestBody, maps).compose(this.norTransformer(clazz));
    }

    public Call<okhttp3.ResponseBody> bodyCall(String url, String json, final Map<String, Object> maps) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return apiService.postJsonCall(url, requestBody, maps);
    }

    /**
     * 提交Body方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param body
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription body(final String url, final Object body, ApiCallback<T> callback) {
        return this.body(url, body, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 删除信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> delete(final String url, final Map<String, Object> maps, Class<T> clazz) {
        if (BuildConfig.LOG_MODE) {
            LogUtils.i(LogUtils.getParametersToString(url, maps));
        }
        return apiService.delete(url, maps).compose(this.norTransformer(clazz));
    }

    /**
     * 删除信息请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription delete(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.delete(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 修改信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> put(final String url, final String json, final Map<String, Object> maps, Class<T> clazz) {
        if (BuildConfig.LOG_MODE) {
            LogUtils.i(LogUtils.getParametersToString(url, maps));
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return apiService.put(url, requestBody, maps).compose(this.norTransformer(clazz));
    }

    /**
     * 修改信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> put(final String url, final Map<String, Object> maps, Class<T> clazz) {
        if (BuildConfig.LOG_MODE) {
            LogUtils.i(LogUtils.getParametersToString(url, maps));
        }
        return apiService.put(url, maps).compose(this.norTransformer(clazz));
    }

    /**
     * 修改信息请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription put(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        return this.put(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 上传图片，需传入请求body和实体类
     *
     * @param url
     * @param requestBody
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadImage(String url, RequestBody requestBody, Class<T> clazz) {
        return apiService.uploadImage(url, requestBody).compose(this.norTransformer(clazz));
    }

    /**
     * 上传图片，需传入图片文件和实体类
     *
     * @param url
     * @param file
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadImage(String url, File file, Class<T> clazz) {
        return apiService.uploadImage(url, RequestBody.create(okhttp3.MediaType.parse("image/jpg; " + "charset=utf-8"), file)).compose
                (this.norTransformer(clazz));
    }

    public <T> Observable<T> uploadImage(String url, File file, List<MultipartBody.Part> parts, Class<T> clazz) {
        return apiService.uploadImage(url, RequestBody.create(okhttp3.MediaType.parse("image/jpg; " + "charset=utf-8"), file), parts).compose
                (this.norTransformer(clazz));
    }

    public Call<ResponseBody> uploadImageCall(String url, File file, List<MultipartBody.Part> parts) {
        return apiService.uploadImageCall(url, RequestBody.create(okhttp3.MediaType.parse("image/jpg; " + "charset=utf-8"), file), parts);
    }

    public <T> Observable<T> uploadAudio(String url, File file, List<MultipartBody.Part> parts, Class<T> clazz) {
        return apiService.uploadAudio(url, RequestBody.create(okhttp3.MediaType.parse("audio/aac; " + "charset=utf-8"), file), parts).compose
                (this.norTransformer(clazz));
    }

    /**
     * 上传文件
     *
     * @param url
     * @param requestBody
     * @param file
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadFile(String url, RequestBody requestBody, MultipartBody.Part file, Class<T> clazz) {
        return apiService.uploadFile(url, requestBody, file).compose(this.norTransformer(clazz));
    }

    /**
     * 上传多文件
     *
     * @param url
     * @param files
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadFlies(String url, Map<String, RequestBody> files, Class<T> clazz) {
        return apiService.uploadFiles(url, files).compose(this.norTransformer(clazz));
    }

    /*=============================以下处理服务器返回对象为ApiResult<T>形式的请求=================================*/

    /**
     * 由外部设置被观察者
     *
     * @param observable
     * @param <T>
     * @return
     */
    public <T> Observable<T> apiCall(Observable<T> observable) {
        return observable.map(new Func1<T, T>() {
            @Override
            public T call(T result) {
                if (result instanceof ApiResult) {
                    ApiResult value = (ApiResult) result;
                    return (T) value.getData();
                } else {
                    Throwable throwable = new Throwable("Please call(Observable<T> observable) , < T > is not " + "ApiResult object");
                    new ApiException(throwable, ApiCode.Request.INVOKE_ERROR);
                    return (T) result;
                }
            }
        }).compose(new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(new ApiErrFunc<T>());
            }
        });
    }

    /**
     * 返回ApiResult<T>的Get方式请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> apiGet(final String url, final Map<String, Object> maps, Class<T> clazz) {
        if (BuildConfig.LOG_MODE) {
            LogUtils.i(LogUtils.getParametersToString(url, maps));
        }
        return apiService.get(url, maps).map(new ApiResultFunc<>(clazz)).compose(this.<T>apiTransformer());
    }

    /**
     * 返回ApiResult<T>的Get方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription apiGet(final String url, final Map<String, Object> maps, ApiCallback<T> callback) {
        return this.apiGet(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber<>(context, callback));
    }

    /**
     * 返回ApiResult<T>的POST方式请求，需传入实体类
     *
     * @param url
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> apiPost(final String url, final Map<String, Object> parameters, Class<T> clazz) {
        if (BuildConfig.LOG_MODE) {
            LogUtils.i(LogUtils.getParametersToString(url, parameters));
        }
        return apiService.post(url, parameters).map(new ApiResultFunc<>(clazz)).compose(this.<T>apiTransformer());
    }

    /**
     * 返回ApiResult<T>的POST方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param parameters
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription apiPost(final String url, final Map<String, Object> parameters, ApiCallback<T> callback) {
        return this.apiPost(url, parameters, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 创建ViseApi.Builder
     *
     * @param context
     * @return
     */
    public static ApiManage.Builder newBuilder(Context context) {
        return new ApiManage.Builder(context);
    }

    private <T> Observable.Transformer<ResponseBody, T> norTransformer(final Class<T> clazz) {
        return new Observable.Transformer<ResponseBody, T>() {
            @Override
            public Observable<T> call(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                        .mainThread()).map(new ApiFunc<>(clazz)).onErrorResumeNext(new ApiErrFunc<T>());
            }
        };
    }

    private <T> Observable.Transformer<ApiResult<T>, T> apiTransformer() {
        return new Observable.Transformer<ApiResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<ApiResult<T>> apiResultObservable) {
                return apiResultObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                        .mainThread()).flatMap(new ApiDataFunc<T>()).onErrorResumeNext(new ApiErrFunc<T>());
            }
        };
    }

    private static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    /**
     * ViseApi的所有配置都通过建造者方式创建
     */
    public static final class Builder {
        private okhttp3.Call.Factory callFactory;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        private HostnameVerifier hostnameVerifier;
        private SSLSocketFactory sslSocketFactory;
        private ConnectionPool connectionPool;
        private String baseUrl;
        private Retrofit.Builder retrofitBuilder;
        private OkHttpClient.Builder okHttpBuilder;

        Builder(Context mContext) {
            context = mContext;
            okHttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
        }

        /**
         * 设置自定义OkHttpClient
         *
         * @param client
         * @return
         */
        public ApiManage.Builder client(OkHttpClient client) {
            retrofitBuilder.client(checkNotNull(client, "client == null"));
            return this;
        }

        /**
         * 设置Call的工厂
         *
         * @param factory
         * @return
         */
        public ApiManage.Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * 设置连接超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public ApiManage.Builder connectTimeout(int timeout) {
            return connectTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 设置读取超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public ApiManage.Builder readTimeout(int timeout) {
            return readTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 设置写入超时时间（秒）
         *
         * @param timeout
         * @return
         */
        public ApiManage.Builder writeTimeout(int timeout) {
            return writeTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 设置代理
         *
         * @param proxy
         * @return
         */
        public ApiManage.Builder proxy(Proxy proxy) {
            okHttpBuilder.proxy(checkNotNull(proxy, "proxy == null"));
            return this;
        }

        /**
         * 设置连接池
         *
         * @param connectionPool
         * @return
         */
        public ApiManage.Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) throw new NullPointerException("connectionPool == null");
            this.connectionPool = connectionPool;
            return this;
        }

        /**
         * 设置连接超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public ApiManage.Builder connectTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.connectTimeout(timeout, unit);
            } else {
                okHttpBuilder.connectTimeout(NetUtils.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * 设置写入超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public ApiManage.Builder writeTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.writeTimeout(timeout, unit);
            } else {
                okHttpBuilder.writeTimeout(NetUtils.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * 设置读取超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public ApiManage.Builder readTimeout(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                okHttpBuilder.readTimeout(timeout, unit);
            } else {
                okHttpBuilder.readTimeout(NetUtils.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * 设置请求BaseURL
         *
         * @param baseUrl
         * @return
         */
        public ApiManage.Builder baseUrl(String baseUrl) {
            this.baseUrl = checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        /**
         * 设置转换工厂
         *
         * @param factory
         * @return
         */
        public ApiManage.Builder converterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }

        /**
         * 设置CallAdapter工厂
         *
         * @param factory
         * @return
         */
        public ApiManage.Builder callAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        /**
         * 设置请求头部
         *
         * @param headers
         * @return
         */
        public ApiManage.Builder headers(Map<String, String> headers) {
            okHttpBuilder.addInterceptor(new HeadersInterceptor(headers));
            return this;
        }

        /**
         * 设置请求参数
         *
         * @param parameters
         * @return
         */
        public ApiManage.Builder parameters(Map<String, String> parameters) {
            okHttpBuilder.addInterceptor(new HeadersInterceptor(parameters));
            return this;
        }

        /**
         * 设置拦截器
         *
         * @param interceptor
         * @return
         */
        public ApiManage.Builder interceptor(Interceptor interceptor) {
            okHttpBuilder.addInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置网络拦截器
         *
         * @param interceptor
         * @return
         */
        public ApiManage.Builder networkInterceptor(Interceptor interceptor) {
            okHttpBuilder.addNetworkInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置SSL工厂
         *
         * @param sslSocketFactory
         * @return
         */
        public ApiManage.Builder SSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        /**
         * 设置主机验证机制
         *
         * @param hostnameVerifier
         * @return
         */
        public ApiManage.Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        /**
         * 使用POST方式是否需要进行GZIP压缩，服务器不支持则不设置
         *
         * @return
         */
        public ApiManage.Builder postGzipInterceptor() {
            interceptor(new GzipRequestInterceptor());
            return this;
        }

        public Retrofit build() {
            if (okHttpBuilder == null) {
                throw new IllegalStateException("okHttpBuilder required.");
            }

            if (retrofitBuilder == null) {
                throw new IllegalStateException("retrofitBuilder required.");
            }

            if (!TextUtils.isEmpty(baseUrl)) {
                retrofitBuilder.baseUrl(baseUrl);
            }

            //转换器Converter(将json 转为JavaBean)
            if (converterFactory == null) {
//                converterFactory = GsonConverterFactory.create();
                converterFactory = GsonConverterFactory.create();
            }
            retrofitBuilder.addConverterFactory(converterFactory);

            //rxjava
            if (callAdapterFactory == null) {
                callAdapterFactory = RxJavaCallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            if (callFactory != null) {
                retrofitBuilder.callFactory(callFactory);
            }

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(interceptor);
            //线程池
            if (connectionPool == null) {
                connectionPool = new ConnectionPool(NetUtils.DEFAULT_MAX_IDLE_CONNECTIONS, NetUtils.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
            }
            okHttpBuilder.connectionPool(connectionPool);

            OkHttpClient okHttpClient = okHttpBuilder.build();
            retrofitBuilder.client(okHttpClient);
            return retrofitBuilder.build();
        }
    }

}
