package cn.kuaikuai.trip.main.login;

import android.content.Context;
import android.text.TextUtils;

import cn.kuaikuai.trip.utils.UserAccountPreferences;

public class LoginUtils {

    /**
     * 是否登录成功
     * @param context
     * @return
     */
    public static boolean isLoginSuccess(Context context){
        return !TextUtils.isEmpty(UserAccountPreferences.getInstance(context).getToken());
    }

}
