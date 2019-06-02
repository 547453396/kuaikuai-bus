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
        String DRIVER_REGISTER = "user/driver/register";
        String DRIVER_GET = "user/driver/get";
        String LOGIN_TB = "api/tb/login";
        String USER_API = "api/auth/user";
        String LOGIN_WX = "api/wx/login";
    }

    interface LinePathApi {
        String ROUTE_ADD = "route/add";
        String LINE_ALL = "line/getAll";
    }

}

