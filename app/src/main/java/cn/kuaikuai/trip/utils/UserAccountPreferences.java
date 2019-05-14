package cn.kuaikuai.trip.utils;

import android.content.Context;
import android.text.TextUtils;

import cn.kuaikuai.trip.model.entity.DBHelper;

public class UserAccountPreferences {
    private static UserAccountPreferences instance;

    private UserAccountPreferences(Context context) {
        DBHelper dbHelper = new DBHelper(context);
    }

    public static UserAccountPreferences getInstance(Context ctx) {
        if (instance == null) {
            instance = new UserAccountPreferences(ctx.getApplicationContext());
        }
        return instance;
    }

    public String getToken() {
        return DBHelper.getUserInfoByKey("token");
    }

    public void setToken(String token) {
        if (TextUtils.isEmpty(token)) {
            token = "";
        }
        DBHelper.insertUserInfo("token", token + "");
    }

    public int getSex() {
        String result = DBHelper.getUserInfoByKey("sex");
        if (!TextUtils.isEmpty(result)) {
            try {
                return Integer.parseInt(result);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public void setSex(int sex) {
        DBHelper.insertUserInfo("sex", sex + "");
    }

    public String getBirth() {
        return DBHelper.getUserInfoByKey("birth");
    }

    public void setBirth(String birth) {
        if (TextUtils.isEmpty(birth)) {
            birth = "";
        }
        DBHelper.insertUserInfo("birth", birth + "");
    }

    public String getBirthTime() {
        return DBHelper.getUserInfoByKey("birth_time");
    }

    public void setBirthTime(String birth_time) {
        if (TextUtils.isEmpty(birth_time)) {
            birth_time = "";
        }
        DBHelper.insertUserInfo("birth_time", birth_time + "");
    }

    public String getAvatar() {
        return DBHelper.getUserInfoByKey("avatar");
    }

    public void setAvatar(String avatar) {
        if (TextUtils.isEmpty(avatar)) {
            avatar = "";
        }
        DBHelper.insertUserInfo("avatar", avatar + "");
    }

    public String getMobilePhone() {
        return DBHelper.getUserInfoByKey("mobile_phone");
    }

    public void setMobilePhone(String mobile_phone) {
        if (TextUtils.isEmpty(mobile_phone)) {
            mobile_phone = "";
        }
        DBHelper.insertUserInfo("mobile_phone", mobile_phone + "");
    }

    public String getAcctk() {
        return DBHelper.getUserInfoByKey("acctk");
    }

    public void setAcctk(String acctk) {
        if (TextUtils.isEmpty(acctk)) {
            acctk = "";
        }
        DBHelper.insertUserInfo("acctk", acctk + "");
    }

    public String getNkName() {
        return DBHelper.getUserInfoByKey("nk_name");
    }

    public void setNkName(String nk_name) {
        if (TextUtils.isEmpty(nk_name)) {
            nk_name = "";
        }
        DBHelper.insertUserInfo("nk_name", nk_name + "");
    }

    public String getRealName() {
        return DBHelper.getUserInfoByKey("real_name");
    }

    public void setRealName(String real_name) {
        if (TextUtils.isEmpty(real_name)) {
            real_name = "";
        }
        DBHelper.insertUserInfo("real_name", real_name + "");
    }

    public int getBirthIsNormal() {
        String result = DBHelper.getUserInfoByKey("birth_is_normal");
        if (!TextUtils.isEmpty(result)) {
            try {
                return Integer.parseInt(result);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public void setBirthIsNormal(int birth_is_normal) {
        DBHelper.insertUserInfo("birth_is_normal", birth_is_normal + "");
    }

    //设置微信昵称
    public void setWXName(String wx_name) {
        if (TextUtils.isEmpty(wx_name)) {
            wx_name = "";
        }
        DBHelper.insertUserInfo("wx_name", wx_name + "");
    }

    //获取微信昵称
    public String getWXName() {
        return DBHelper.getUserInfoByKey("wx_name");
    }

    //微信登陆
    public void setWxLogin(int wx_login) {
        DBHelper.insertUserInfo("wx_login", wx_login + "");
    }

    //微信登陆
    public int getWxLogin() {
        String result = DBHelper.getUserInfoByKey("wx_login");
        if (!TextUtils.isEmpty(result)) {
            try {
                return Integer.parseInt(result);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public void setAlias(String alias) {
        if (TextUtils.isEmpty(alias)) {
            alias = "";
        }
        DBHelper.insertUserInfo("alias", alias + "");
    }

    public String getAlias() {
        return DBHelper.getUserInfoByKey("alias");
    }

    public void setImAccid(String accid) {
        if (TextUtils.isEmpty(accid)) {
            accid = "";
        }
        DBHelper.insertUserInfo("im_accid", accid + "");
    }

    public String getImAccid() {
        return DBHelper.getUserInfoByKey("im_accid");
    }

    public void setImToken(String token) {
        if (TextUtils.isEmpty(token)) {
            token = "";
        }
        DBHelper.insertUserInfo("im_token", token + "");
    }

    public String getImToken() {
        return DBHelper.getUserInfoByKey("im_token");
    }

    public void setUserType(int type) {
        DBHelper.insertUserInfo("user_type", type + "");
    }

    public int getUserType() {
        String result = DBHelper.getUserInfoByKey("user_type");
        if (!TextUtils.isEmpty(result)) {
            try {
                return Integer.parseInt(result);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    /**
     * 待支付
     * @param payment
     */
    public void setOrderPayment(int payment){
        DBHelper.insertUserInfo("order_payment", payment + "");
    }

    public int getOrderPayment(){
        String result = DBHelper.getUserInfoByKey("order_payment");
        if (!TextUtils.isEmpty(result)) {
            try {
                return Integer.parseInt(result);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public void setOrderDoing(int doing){
        DBHelper.insertUserInfo("order_doing", doing + "");
    }

    public int getOrderDoing(){
        String result = DBHelper.getUserInfoByKey("order_doing");
        if (!TextUtils.isEmpty(result)) {
            try {
                return Integer.parseInt(result);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public void setOrderComment(int comment){
        DBHelper.insertUserInfo("order_comment", comment + "");
    }

    public int getOrderComment(){
        String result = DBHelper.getUserInfoByKey("order_comment");
        if (!TextUtils.isEmpty(result)) {
            try {
                return Integer.parseInt(result);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public void setWxNickName(String wxNickName) {
        DBHelper.insertUserInfo("wx_nick_name", wxNickName);
    }

    public String getWxNickName() {
        return DBHelper.getUserInfoByKey("wx_nick_name");
    }


    public void setWxAvatar(String wxAvatar) {
        DBHelper.insertUserInfo("wx_avatar", wxAvatar);
    }

    public String getWxAvatar() {
        return DBHelper.getUserInfoByKey("wx_avatar");
    }

    public void setWxOpenid(String wxOpenid) {
        DBHelper.insertUserInfo("wx_open_id", wxOpenid);
    }

    public String getWxOpenid() {
        return DBHelper.getUserInfoByKey("wx_open_id");
    }

    public void setWxUnionId(String wxUnionid) {
        DBHelper.insertUserInfo("wx_union_id", wxUnionid);
    }

    public String getWxUnionId() {
        return DBHelper.getUserInfoByKey("wx_union_id");
    }

    public void setWxAccount(String wxUnionid) {
        DBHelper.insertUserInfo("wx_account", wxUnionid);
    }

    public String getWxAccount() {
        return DBHelper.getUserInfoByKey("wx_account");
    }

    public void cleanUserInfo(){
        DBHelper.cleanUserInfo();
    }
}
