package cn.kuaikuai.trip.event;

public class WXLoginEvent {
    public static final int type_login_register = 1;//登录注册界面登录
    public static final int type_user_center_bind_wx = 2;//用户中心绑定微信

    public static final String RESULT_OK = "result_ok";
    public static final String RESULT_FAIL = "result_fail";


    public WXLoginEvent(String result) {
        mResult = result;
    }

    private String mResult = RESULT_OK;

    public String getResult() {
        return mResult;
    }
}
