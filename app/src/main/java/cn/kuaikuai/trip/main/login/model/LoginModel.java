package cn.kuaikuai.trip.main.login.model;

import android.content.Context;

import java.util.Map;

import cn.kuaikuai.base.model.BaseModel;
import cn.kuaikuai.common.net.api.ApiManage;
import cn.kuaikuai.trip.model.bean.login.LoginBean;
import cn.kuaikuai.trip.model.bean.login.UserAccountBean;
import cn.kuaikuai.trip.model.bean.login.VerifyCodeBean;
import cn.kuaikuai.trip.net.ApiInterface;
import rx.Observer;

public class LoginModel extends BaseModel {
    public LoginModel(Context context) {
        super(context);
    }

    public void getVerifyCode(Map<String, Object> parameters, Observer<VerifyCodeBean> observer) {
        subscribe(ApiManage.getInstance().post(ApiInterface.LoginApi.GET_VERIFY_CODE, parameters, VerifyCodeBean.class), observer);
    }

    public void loginWithPhone(Map<String, Object> parameters, Observer<LoginBean> observer) {
        subscribe(ApiManage.getInstance().post(ApiInterface.LoginApi.LOGIN_VERIFY_CODE, parameters, LoginBean.class), observer);
    }

    public void loginWithTb(Map<String, Object> parameters, Observer<LoginBean> observer) {
        subscribe(ApiManage.getInstance().post(ApiInterface.LoginApi.LOGIN_TB, parameters, LoginBean.class), observer);
    }

    public void loginWithWx(Map<String, Object> parameters, Observer<LoginBean> observer) {
        subscribe(ApiManage.getInstance().post(ApiInterface.LoginApi.LOGIN_WX, parameters, LoginBean.class), observer);
    }

    public void getUserInfo(Map<String, Object> parameters, Observer<UserAccountBean> observer) {
        subscribe(ApiManage.getInstance().apiGet(ApiInterface.LoginApi.USER_API, parameters, UserAccountBean.class), observer);
    }
}
