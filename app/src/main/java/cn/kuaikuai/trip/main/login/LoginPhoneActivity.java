package cn.kuaikuai.trip.main.login;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.kuaikuai.base.activity.BaseActivity;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.customview.KeyboardListenRelativeLayout;
import cn.kuaikuai.trip.customview.LoadingView;
import cn.kuaikuai.trip.event.WXLoginEvent;
import cn.kuaikuai.trip.main.login.presenter.LoginPresenter;
import cn.kuaikuai.trip.main.login.view.ILoginView;
import cn.kuaikuai.trip.push.AllPushManager;
import cn.kuaikuai.trip.utils.UserAccountPreferences;
import cn.kuaikuai.trip.utils.UtilsManager;

public class LoginPhoneActivity extends BaseActivity implements ILoginView {
    @BindView(R.id.et_phone)
    protected EditText et_phone;
    @BindView(R.id.et_identify_code)
    protected EditText et_identify_code;
    @BindView(R.id.btn_identify)
    protected TextView btn_identify;
    @BindView(R.id.tv_sec)
    protected TextView tv_sec;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.loading)
    LoadingView loading;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.root)
    KeyboardListenRelativeLayout root;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.login_other)
    LinearLayout mLoginOther;
    @BindView(R.id.ll_login_line)
    LinearLayout mLoginLine;
    private boolean from_task;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImmersionBar
                .fitsSystemWindows(true)//使用该属性,必须指定状态栏颜色
                .statusBarColor(android.R.color.white)
                .statusBarDarkFont(true)
                .init();
        mImmersionBar.keyboardEnable(true).statusBarDarkFont(true, 0.2f).init();
        setContentView(R.layout.activity_login);
        from_task = getIntent().getBooleanExtra("from_task", false);
        loginPresenter = new LoginPresenter(mActivity, this);
        initView();
    }

    private void initView() {
        root.setOnKeyboardStateChangedListener(new KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener() {
            @Override
            public void onKeyboardStateChanged(int state) {
                switch (state) {
                    case KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE://软键盘隐藏
                        llBottom.setVisibility(View.VISIBLE);
                        mLoginOther.setVisibility(View.VISIBLE);
                        mLoginLine.setVisibility(View.VISIBLE);
                        break;
                    case KeyboardListenRelativeLayout.KEYBOARD_STATE_SHOW://软键盘显示
                        llBottom.setVisibility(View.INVISIBLE);
                        mLoginOther.setVisibility(View.INVISIBLE);
                        mLoginLine.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        llBottom.setVisibility(View.VISIBLE);
                        mLoginOther.setVisibility(View.VISIBLE);
                        mLoginLine.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        btn_login.setClickable(false);
        btn_login.setAlpha(0.3f);
        et_phone.addTextChangedListener(textWatcher);
        et_identify_code.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onDestroy() {
        if (loading != null) {
            loading.dismiss();
        }
        super.onDestroy();
    }

    @OnClick(R.id.btn_identify)
    protected void clickIdentify() {
        String phone = et_phone.getText().toString().trim().replaceAll(" ", "").replaceAll("\\+86", "");
        if (!TextUtils.isEmpty(phone)) {
            if (UtilsManager.isPhoneNumberValid(phone)) {
                //请求验证码需要加密
                btn_identify.setClickable(false);
                btn_identify.setText(R.string.identify_ing);
                loginPresenter.getVerifyCode(phone);
            } else {
                UtilsManager.Toast(mActivity, R.string.errorPhoneNum);
                et_phone.requestFocus();
            }
        } else {
            UtilsManager.Toast(mActivity, R.string.phoneCanNotNull);
            et_phone.requestFocus();
        }
    }

    @OnClick(R.id.btn_login)
    protected void clickLoginPhone() {
        String phone = et_phone.getText().toString().trim().replaceAll(" ", "").replaceAll("\\+86", "");
        String identity = et_identify_code.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            UtilsManager.Toast(mActivity, R.string.phoneCanNotNull);
            et_phone.requestFocus();
        } else if (TextUtils.isEmpty(identity)) {
            UtilsManager.Toast(mActivity, R.string.identityCanNotNull);
            et_identify_code.requestFocus();
        } else {
            if (UtilsManager.isPhoneNumberValid(phone)) {
                loading.showLoading();
                loginPresenter.loginWithPhone(phone, identity, from_task);
            } else {
                UtilsManager.Toast(mActivity, R.string.errorPhoneNum);
                et_phone.requestFocus();
            }
        }
    }

    @OnClick(R.id.login_weixin)
    protected void wxLogin() {
        String WECHAT_APP_ID = WlConfig.wx_appId;
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        IWXAPI api = WXAPIFactory.createWXAPI(mActivity, WECHAT_APP_ID, true);
        if (api.isWXAppInstalled()) {//客户端安装了微信
            api.registerApp(WECHAT_APP_ID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "qweqweqwe";
            api.sendReq(req);
        } else {
            UtilsManager.Toast(mActivity, R.string.WXNotInstalled);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onWXLoginEvent(WXLoginEvent event) {
        if (TextUtils.equals(WXLoginEvent.RESULT_OK, event.getResult())) {
            loading.showLoading();
            loginPresenter.loginWithWx();
        } else {
            loading.dismiss();
            UtilsManager.Toast(mActivity, "登陆失败");
        }
    }

    @OnClick(R.id.iv_close)
    void close() {
        finish();
        overridePendingTransition(R.anim.alpha_show, R.anim.alpha_gone);
    }

    @OnClick(R.id.tv_agreement)
    void agreement() {
    }

    @Override
    public void onGetVerifyCodeSucceed() {
        TimeCount mCount = new TimeCount(60000, 1000);
        mCount.start();
        et_identify_code.requestFocus();
    }

    @Override
    public void onGetVerifyCodeFailed(String msg) {
        tv_sec.setVisibility(View.GONE);
        btn_identify.setVisibility(View.VISIBLE);
        btn_identify.setClickable(true);
        btn_identify.setText(R.string.identify_again);
        if (!TextUtils.isEmpty(msg)) {
            UtilsManager.Toast(mActivity, msg);
        }
    }

    @Override
    public void onLoginSucceed() {
        loading.dismiss();
        close();
    }

    @Override
    public void onLoginFailed(String msg) {
        loading.dismiss();
        if (!TextUtils.isEmpty(msg)) {
            UtilsManager.Toast(mActivity, msg);
        }
    }

    /**
     * 获取验证码按钮的倒计时计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            btn_identify.setText(R.string.identify_again);
            btn_identify.setClickable(true);
            btn_identify.setEnabled(true);
            btn_identify.setVisibility(View.VISIBLE);
            tv_sec.setVisibility(View.GONE);
        }

        public void onTick(long millisUntilFinished) {
            btn_identify.setVisibility(View.GONE);
            tv_sec.setVisibility(View.VISIBLE);
            tv_sec.setText(millisUntilFinished / 1000 + getString(R.string.sec));
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = et_phone.getText().toString().trim();
            if (!TextUtils.isEmpty(text) &&
                    !TextUtils.isEmpty(et_identify_code.getText().toString().trim())) {
                btn_login.setClickable(true);
                btn_login.setAlpha(1f);
            } else {
                btn_login.setClickable(false);
                btn_login.setAlpha(0.3f);
            }
            if (UtilsManager.isPhoneNumberValid(text)) {
                btn_identify.setTextColor(getResources().getColor(R.color.color_333333));
            } else {
                btn_identify.setTextColor(getResources().getColor(R.color.color_999999));
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            close();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UtilsManager.hideKeyBord(this, getWindow());
    }

    @Override
    public boolean fitsSystemWindows() {
        return false;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    // 被挤出，登录过期，返回1004时，重新登录时调用；
    public static void reLogin(Context context) {
        UserAccountPreferences.getInstance(context).cleanUserInfo();
        AllPushManager.getInstance(context).unBindPush();
    }

}
