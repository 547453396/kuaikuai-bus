package cn.kuaikuai.trip.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;

import cn.kuaikuai.base.activity.BaseActivity;
import cn.kuaikuai.common.net.api.ApiManage;
import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.event.ShareEvent;
import cn.kuaikuai.trip.event.WXLoginEvent;
import cn.kuaikuai.trip.utils.WXPreferences;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 14-3-14.
 * 微信授权使用的回调的类，必须在与包名同名的底下
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private IWXAPI api;
    public String WECHAT_APP_ID;
    public String APPSECRET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //放normal渠道的微信的key
        WECHAT_APP_ID = WlConfig.wx_appId;
        APPSECRET = WlConfig.wx_appSecret;
        api = WXAPIFactory.createWXAPI(this, WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq baseReq) {
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                finish();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            SendAuth.Resp resp = (SendAuth.Resp) baseResp;
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    getWeiXinToken(WECHAT_APP_ID, APPSECRET, resp.code, "authorization_code");
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    //"取消微信登录"
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    finish();
                    break;
                default:
                    finish();
                    break;
            }
        } else {
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    EventBus.getDefault().post(new ShareEvent(0));
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    EventBus.getDefault().post(new ShareEvent(1));
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    EventBus.getDefault().post(new ShareEvent(2));
                    finish();
                    break;
                default:
                    EventBus.getDefault().post(new ShareEvent(2));
                    finish();
                    break;
            }
        }
    }

    /**
     * 通过code获取access_token的接口。grant_type=authorization_code
     */
    private void getWeiXinToken(final String appid, final String secret, final String code, final String grant_type) {
        HashMap<String, Object> table = new HashMap<>();
        table.put("appid", appid);
        table.put("secret", secret);
        table.put("code", code);
        table.put("grant_type", grant_type);
        ApiManage.getInstance().get(WlConfig.WX_access_token_Url, table, String.class).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    String result;
                    @Override
                    public void onCompleted() {
                        try {
                        WXPreferences wxpre = WXPreferences.getInstance(WXEntryActivity.this);
                            wxpre.parseJsonTokenAndSave(result);
                            JSONObject object = new JSONObject(result);
                            if (!TextUtils.isEmpty(object.optString("access_token"))
                                    && !TextUtils.isEmpty(object.optString("openid"))) {
                                EventBus.getDefault().post(new WXLoginEvent(WXLoginEvent.RESULT_OK));
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        result = s;
                    }
                });
    }

    @Override
    public boolean isUseImmersionBar() {
        return false;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }
}

