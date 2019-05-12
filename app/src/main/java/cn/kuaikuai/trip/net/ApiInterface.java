package cn.kuaikuai.trip.net;

import cn.kuaikuai.trip.BuildConfig;

/**
 * 接口
 * JXH
 * 2018/7/11
 */
public interface ApiInterface {


    /**
     * 默认base_url
     */
    String DEFAULT_BASE_URL = BuildConfig.BASE_URL;

    /**
     * 天气定位接口
     */
    String CITY_URL = "https://weather.weilitoutiao.net/Ecalender/api/city";
    /**
     * 软件中webView加载的js,作用是是去除页面广告、插入native广告
     */
    String JSSTRINGURL = "https://static.weilitoutiao.net/apis/toolkit.js";


}

