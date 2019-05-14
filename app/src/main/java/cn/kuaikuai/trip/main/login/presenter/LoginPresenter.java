package cn.kuaikuai.trip.main.login.presenter;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.kuaikuai.common.net.api.ApiManage;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.main.login.model.LoginModel;
import cn.kuaikuai.trip.main.login.view.ILoginView;
import cn.kuaikuai.trip.model.bean.login.LoginBean;
import cn.kuaikuai.trip.model.bean.login.UserAccountBean;
import cn.kuaikuai.trip.model.bean.login.VerifyCodeBean;
import cn.kuaikuai.trip.net.ApiInterface;
import cn.kuaikuai.trip.net.ApiUtils;
import cn.kuaikuai.trip.utils.UserAccountPreferences;
import cn.kuaikuai.trip.utils.WXPreferences;
import rx.Observer;

public class LoginPresenter {
    private Context ctx;
    private ILoginView loginView;
    private LoginModel loginModel;

    public LoginPresenter(Context context, ILoginView loginView) {
        ctx = context;
        this.loginView = loginView;
        loginModel = new LoginModel(context);
    }

    public void getVerifyCode(String phone) {
        HashMap<String, Object> table = new HashMap<>();
        table.put("tel", phone);
        table.put("area_code", "86");
        table.put("type", "sms");
        ApiUtils.addCommonParams(ctx, table);
        loginModel.getVerifyCode(table, new Observer<VerifyCodeBean>() {
            VerifyCodeBean bean;

            @Override
            public void onCompleted() {
                if (loginView != null) {
                    if (bean != null) {
                        if (bean.getStatus() == 1000) {
                            loginView.onGetVerifyCodeSucceed();
                        } else {
                            if (TextUtils.isEmpty(bean.getDesc())) {
                                loginView.onGetVerifyCodeFailed(ctx.getString(R.string.server_error));
                            } else {
                                loginView.onGetVerifyCodeFailed(bean.getDesc());
                            }
                        }
                    } else {
                        loginView.onGetVerifyCodeFailed(ctx.getString(R.string.server_error));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (loginView != null) {
                    loginView.onGetVerifyCodeFailed(ctx.getString(R.string.server_error));
                }
            }

            @Override
            public void onNext(VerifyCodeBean bean) {
                this.bean = bean;
            }
        });
    }

    public void loginWithPhone(String phone, String identity, boolean from_task) {
        HashMap<String, Object> table = new HashMap<>();
        JSONObject body = new JSONObject();
        try {
            body.put("mobilePhone",phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loginModel.loginWithPhone(table, body.toString(), new LoginObserver(LoginObserver.LOGIN_TYPE_PHONE, from_task));
    }

    public void loginWithTb(String nick_name, String avatar, String openid, String open_sid, boolean from_task) {
        HashMap<String, Object> table = new HashMap<>();
        table.put("nick_name", nick_name);
        table.put("avatar", avatar);
        table.put("openid", openid);
        table.put("open_sid", open_sid);
        ApiUtils.addCommonParams(ctx, table);
        loginModel.loginWithTb(table, new LoginObserver(LoginObserver.LOGIN_TYPE_TAOBAO, from_task));
    }

    public void loginWithWx() {
        WXPreferences wxPreferences = WXPreferences.getInstance(ctx);
        HashMap<String, Object> table = new HashMap<>();
        table.put("access_token", wxPreferences.getToken());
        table.put("open_id", wxPreferences.getOpenId());
        ApiUtils.addCommonParams(ctx, table);
        loginModel.loginWithWx(table, new LoginObserver(LoginObserver.LOGIN_TYPE_WEIXIN, false));
    }

    private class LoginObserver implements Observer<LoginBean> {
        public static final int LOGIN_TYPE_PHONE = 0;
        public static final int LOGIN_TYPE_TAOBAO = 1;
        public static final int LOGIN_TYPE_WEIXIN = 2;

        LoginBean bean;
        int mLoginType; //1 taobao 2 weixin  0 phone
        boolean from_task;

        LoginObserver(int loginType, boolean from_task) {
            this.from_task = from_task;
            mLoginType = loginType;
        }

        @Override
        public void onCompleted() {
            if (bean != null) {
                if ("200".equals(bean.getCode())) {
                    UserAccountPreferences accountPreferences = UserAccountPreferences.getInstance(ctx);
                    if (bean.getData() != null) {
                        accountPreferences.setToken(bean.getData().getToken());
                        accountPreferences.setMobilePhone(bean.getData().getMobilePhone());
                        Map<String, String> headers = new HashMap<>();
                        if (!TextUtils.isEmpty(UserAccountPreferences.getInstance(ctx).getToken())){
                            headers.put("bus-token", UserAccountPreferences.getInstance(ctx).getToken());
                            ApiManage.initApiManage(ctx, ApiInterface.DEFAULT_BASE_URL, null);
                        }
                        getUserInfo();
                    }
                } else {
                    if (loginView != null) {
                        if (TextUtils.isEmpty(bean.getMsg())) {
                            loginView.onLoginFailed(ctx.getString(R.string.server_error));
                        } else {
                            loginView.onLoginFailed(bean.getMsg());
                        }
                    }
                }
            } else {
                if (loginView != null) {
                    loginView.onLoginFailed(ctx.getString(R.string.server_error));
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            if (loginView != null) {
                loginView.onLoginFailed(ctx.getString(R.string.server_error));
            }
        }

        @Override
        public void onNext(LoginBean bean) {
            this.bean = bean;
        }
    }

    private void getUserInfo() {
        HashMap<String, Object> table = new HashMap<>();
        loginModel.getUserInfo(table, new Observer<LoginBean>() {
            LoginBean loginBean;

            @Override
            public void onCompleted() {
                UserAccountPreferences accountPreferences = UserAccountPreferences.getInstance(ctx);
                if (loginBean != null) {

                } else {
                    accountPreferences.cleanUserInfo();
                    if (loginView != null) {
                        loginView.onLoginFailed(ctx.getString(R.string.server_error));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                UserAccountPreferences accountPreferences = UserAccountPreferences.getInstance(ctx);
                accountPreferences.cleanUserInfo();
                if (loginView != null) {
                    loginView.onLoginFailed(ctx.getString(R.string.server_error));
                }
            }

            @Override
            public void onNext(LoginBean userAccountBean) {
                this.loginBean = userAccountBean;
            }
        });
    }

    private void saveUserInfo(final UserAccountBean userAccountBean, final UserAccountPreferences accountPreferences) {
        if (TextUtils.isEmpty(userAccountBean.getNickName())) {
            if (!TextUtils.isEmpty(userAccountBean.getWxNickName())) {
                userAccountBean.setNickName(userAccountBean.getWxNickName());
            } else if (!TextUtils.isEmpty(userAccountBean.getTbNickName())) {
                userAccountBean.setNickName(userAccountBean.getTbNickName());
            }
        }
        if (TextUtils.isEmpty(userAccountBean.getAvatar())) {
            if (!TextUtils.isEmpty(userAccountBean.getWxAvatar())) {
                userAccountBean.setAvatar(userAccountBean.getWxAvatar());
            } else if (!TextUtils.isEmpty(userAccountBean.getTbAvatar())) {
                userAccountBean.setAvatar(userAccountBean.getTbAvatar());
            }
        }
        accountPreferences.setAvatar(userAccountBean.getAvatar());
        accountPreferences.setNkName(userAccountBean.getNickName());
        accountPreferences.setRealName(userAccountBean.getReal_name());
        accountPreferences.setSex(userAccountBean.getSex());

        accountPreferences.setWxNickName(userAccountBean.getWxNickName());
        accountPreferences.setWxAvatar(userAccountBean.getWxAvatar());
        accountPreferences.setWxOpenid(userAccountBean.getWxOpenid());
        accountPreferences.setWxUnionId(userAccountBean.getWxUnionid());
        accountPreferences.setWxAccount(userAccountBean.getWx_account());

        if (userAccountBean.getTel() != 0) {
            accountPreferences.setMobilePhone(userAccountBean.getTel() + "");
        } else {
            accountPreferences.setMobilePhone("");
        }
    }
}
