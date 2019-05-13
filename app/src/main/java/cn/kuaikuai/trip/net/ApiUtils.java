package cn.kuaikuai.trip.net;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.kuaikuai.trip.constant.KeyConstant;
import cn.kuaikuai.trip.constant.NetParamsConstant;
import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.model.entity.DBHelper;
import cn.kuaikuai.trip.utils.Base64;
import cn.kuaikuai.trip.utils.PackageHelper;
import cn.kuaikuai.trip.utils.SpUtils;
import cn.kuaikuai.trip.utils.UserAccountPreferences;
import cn.kuaikuai.trip.utils.UtilsManager;
import cn.kuaikuai.common.ChannelUtil;
import cn.kuaikuai.common.libs.WeliLib;

/**
 * http://dev.etouch.cn/ssy_api_doc_key_info.html
 * 公共参数规则，微鲤看看公共参数为
 * app_key、app_ts、uid、auth_token、ver_code、ver_name、channel、city_key、os_version、device_id、app_sign
 */
public class ApiUtils {

    /**
     * 接口公共参数
     * 以后接口调用统一需要加参数然后再添加app_sign
     *
     * @param ctx
     * @param table
     */
    public static void addCommonParams(Context ctx, Map<String, Object> table) {
        if (table != null) {
//            UserAccountPreferences userAccountPreferences = UserAccountPreferences.getInstance(ctx);
//            if (!table.containsKey(NetParamsConstant.APP_KEY)) {
//                table.put(NetParamsConstant.APP_KEY, WlConfig.appkey);
//            }
//            if (!table.containsKey(NetParamsConstant.APP_TS)) {
//                table.put(NetParamsConstant.APP_TS, System.currentTimeMillis());
//            }
//            if (!table.containsKey(NetParamsConstant.UID)) {
//                table.put(NetParamsConstant.UID, userAccountPreferences.getUid());
//            }
//            if (!table.containsKey(NetParamsConstant.ACCTK)) {
//                table.put(NetParamsConstant.ACCTK, UserAccountPreferences.getInstance(ctx).getAcctk());
//            }
//            PackageHelper packageHelper = new PackageHelper(ctx);
//            if (!table.containsKey(NetParamsConstant.VER_CODE)) {
//                table.put(NetParamsConstant.VER_CODE, packageHelper.getLocalVersionCode() + "");
//            }
//            if (!table.containsKey(NetParamsConstant.VER_NAME)) {
//                table.put(NetParamsConstant.VER_NAME, packageHelper.getLocalVersionName());
//            }
//            if (!table.containsKey(NetParamsConstant.CHANNEL)) {
//                table.put(NetParamsConstant.CHANNEL, ChannelUtil.getChannel(ctx));
//            }
//            if (!table.containsKey(NetParamsConstant.CITY_KEY)) {
//                String location = DBHelper.getCacheDataByKey(KeyConstant.LOCATION);
//                JSONObject jsonObject = null;
//                String cityKey = "";
//                try {
//                    jsonObject = new JSONObject(location);
//                    cityKey = jsonObject.optString("cityKey1", "");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                table.put(NetParamsConstant.CITY_KEY, cityKey);
//            }
//            if (!table.containsKey(NetParamsConstant.OS_VERSION)) {
//                table.put(NetParamsConstant.OS_VERSION, packageHelper.getOSVersion() + "");
//            }
//            if (!table.containsKey(NetParamsConstant.DEVICE_ID)) {
//                String device_id = SpUtils.getInstance(ctx).getImei() + SpUtils.getInstance(ctx).getMac();
//                device_id = UtilsManager.getMD5(device_id.getBytes());
//                table.put(NetParamsConstant.DEVICE_ID, device_id);
//            }
//            if (!table.containsKey(NetParamsConstant.APP_SIGN)) {
//                table.put(NetParamsConstant.APP_SIGN, getTheAppSign(table));
//            }
        }
    }

    /**
     * 获取auth_token
     *
     * @return
     */
    public static String getAuthToken(Context context) {
        JSONObject auth_token = new JSONObject();
        String authToken = "";
        try {
            auth_token.put("acctk", UserAccountPreferences.getInstance(context).getAcctk());
            auth_token.put("up", "ANDROID");
            auth_token.put("device", getDeviceValue(context));
            authToken = Base64.encode(auth_token.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authToken;
    }

    /**
     * 生成device字段，并存储
     */
    public static String generateDeviceValue(Context ctx) {
        String model = android.os.Build.MODEL;
        String imei = SpUtils.getInstance(ctx).getImei();
        String device = model.replace(" ", "") + imei;
        DBHelper.insertCacheData(KeyConstant.DEVICE_VALUE, device);
        return device;
    }

    public static String getDeviceValue(Context ctx) {
        String device = DBHelper.getCacheDataByKey(KeyConstant.DEVICE_VALUE);
        if (TextUtils.isEmpty(device)) {
            generateDeviceValue(ctx);
        }
        return device;
    }

    /**
     * 将HashMap中的参数生成签名（参数排序后连接起来md5）
     */
    public static String getTheAppSign(Map<String, Object> table) {
        TreeMap<String, Object> tree = new TreeMap<>();
        if (table != null) {
            Set<Map.Entry<String, Object>> enu = table.entrySet();
            for (Map.Entry<String, Object> item : enu) {
                tree.put(item.getKey(), item.getValue());
            }
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keys = tree.keySet();
        for (String key : keys) {
            sb.append(key).append(tree.get(key));
        }
        sb.append(WlConfig.appSecret);
        return WeliLib.getInstance().doTheEncrypt(UtilsManager.getMD5(sb.toString().getBytes()), 1);
    }
}
