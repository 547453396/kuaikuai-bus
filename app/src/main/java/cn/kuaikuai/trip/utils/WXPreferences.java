package cn.kuaikuai.trip.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author lyc
 * update by xujun 2016、06、27
 * 将没有用的变量注释在最下面，格式化代码并且删除无用代码
 */

public class WXPreferences {
    public String KEY = "";
    public static String SETTING_PREF = "WXToken";
    private Context context;
    private SharedPreferences settings;
    private Editor editor;
    public static WXPreferences wxPre;
    public static WXPreferences getInstance(Context context) {
        if (wxPre == null) {
            wxPre = new WXPreferences(context.getApplicationContext());
        }
        return wxPre;
    }

    public WXPreferences(Context ctx) {
        super();
        this.context = ctx;
        settings = ctx.getSharedPreferences(SETTING_PREF, 0);
        editor = settings.edit();
    }


    //微信授权成功开始对本地设置值
    public void parseJsonTokenAndSave(String strResult) {// AccessToken的json解析器
        try {
            JSONObject object = new JSONObject(strResult);
            String openId = object.optString("openid");
            //解析新的微信
            String token = object.optString("access_token");
            editor.putString("access_token", token);
            editor.putString("expires_in", object.optString("expires_in"));
            editor.putString("openid", openId);
            editor.putString("unionid", object.optString("unionid"));
            editor.putString("scope", object.optString("scope"));
            editor.commit();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 获取微信授权的token
     * @return
     */
    public String getToken(){
        return settings.getString("access_token", "");
    }

    /**
     * 获取微信的openid
     * @return
     */
    public String getOpenId(){
        return settings.getString("openid", "");
    }

    /**
     * 获取微信的unionid
     * @return
     */
    public String getUnionId(){
        return settings.getString("unionid", "");
    }

    public String getOpenIdSecond(){
        return settings.getString("openid_second", "");
    }


    //清楚微信授权信息，只有在退出等的时候有调用
    public void clearData() {
        editor.clear();
        editor.commit();
    }



//   微信的关键地址信息
//    public final static String AUTH_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
//    public final static String ACCESS_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
//    /**
//     * 微信获取access_token地址
//     */
//    String WX_access_token_Url = "https://api.weixin.qq.com/sns/oauth2/access_token?";
//    /**
//     * 微信获取refresh_token地址
//     */
//    String WX_refresh_token_Url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?";
//    /**
//     * 微信获取用户信息地址
//     */
//    String WX_userinfo_Url = "https://api.weixin.qq.com/sns/userinfo?";

}
