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
     * 登录及用户信息相关接口
     * 获取验证码；验证码登录；微信登录；绑定手机号；绑定微信；获取/更新用户信息；登出
     */
    interface LoginApi {
        String GET_VERIFY_CODE = "api/verify_code/get";
        String LOGIN_VERIFY_CODE = "api/verify_code/login";
        String LOGIN_TB = "api/tb/login";
        String MERGE_ACCOUNT = "api/auth/bind";
        String BIND_TEL = "api/auth/bind/tel";
        String BIND_TB = "api/auth/bind/tb";
        String BIND_WX = "api/auth/bind/wx";
        String BIND_ALIPAY = "api/auth/bind/alipay";
        String USER_API = "api/auth/user";
        String LOGOUT = "api/auth/logout";
        String LOGIN_WX = "api/wx/login";
        String USER_INFO_API = "api/auth/user/info";
    }

}

