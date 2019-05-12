package cn.kuaikuai.trip.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONArray;

import cn.kuaikuai.common.DeviceUtils;

import static android.content.Context.MODE_PRIVATE;

public class SpUtils {

    private Context ctx;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static SpUtils spUtils = null;

    public static SpUtils getInstance(Context context) {
        if (spUtils == null) {
            spUtils = new SpUtils(context.getApplicationContext());
        }
        return spUtils;
    }

    private SpUtils(Context context) {
        ctx = context;
        sp = context.getSharedPreferences("WlkkSharedPreferences", MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 存储
     */
    public void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public Object getSharedPreference(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        } else {
            return sp.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 获取imei，先从缓存获取，未获取到从sdcard中获取，再未获取到则原始获取
     */
    public String getImei() {
        String imei = sp.getString("device_imei", "");
        if (TextUtils.isEmpty(imei)) {
            imei = DeviceHelper.getDeviceValue(DeviceHelper.IMEI);
            if (TextUtils.isEmpty(imei)) {
                TelephonyManager tm = (TelephonyManager) ctx
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return "";
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        imei = tm.getImei();
                    } else {
                        imei = tm.getDeviceId();
                    }
                    if (!TextUtils.isEmpty(imei)) {
                        setImei(imei);
                        DeviceHelper.writeValue(DeviceHelper.IMEI, imei);
                    }
                }
            }
        }
        return imei;
    }

    public void setImei(String imei) {
        editor.putString("device_imei", imei);
        editor.commit();
    }

    public String getImsi() {
        String imsi = sp.getString("device_imsi", "");
        if (TextUtils.isEmpty(imsi)) {
            imsi = DeviceHelper.getDeviceValue(DeviceHelper.IMSI);
            if (TextUtils.isEmpty(imsi)) {
                TelephonyManager tm = (TelephonyManager) ctx
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return "";
                    }
                    imsi = tm.getSubscriberId();
                    if (!TextUtils.isEmpty(imsi)) {
                        setImsi(imsi);
                        DeviceHelper.writeValue(DeviceHelper.IMSI, imsi);
                    }
                }
            }
        }
        return imsi;
    }

    public void setImsi(String imsi) {
        editor.putString("device_imsi", imsi);
        editor.commit();
    }

    public String getMac() {
        String mac = sp.getString("device_mac", "");
        if (TextUtils.isEmpty(mac)) {
            mac = DeviceHelper.getDeviceValue(DeviceHelper.MAC);
            if (TextUtils.isEmpty(mac)) {
                mac = DeviceUtils.getMacAddress(ctx);
                if (!TextUtils.isEmpty(mac)) {
                    setMac(mac);
                    DeviceHelper.writeValue(DeviceHelper.MAC, mac);
                }
            }
        }
        return mac;
    }

    public void setMac(String mac) {
        editor.putString("device_mac", mac);
        editor.commit();
    }

    /**
     * @param json 是一个JSONArray
     */
    public void setMasterQuickReply(String json) {
        editor.putString("MasterQuickReply", json);
        editor.commit();
    }

    public String getMasterQuickReply() {
        String result = sp.getString("MasterQuickReply", "");
        try {
            JSONArray array;
            if (TextUtils.isEmpty(result)) {
                array = new JSONArray();
                array.put("新增快捷回复");
                array.put("您好，很高兴为您服务，请完善您的生辰八字资料");
                array.put("已收到，请稍等片刻，我为您起卦");
                array.put("不好意思，现在正在给其他客户预测，请您稍等一下");
                array.put("开始正式咨询前，请您认真阅读：\n" +
                        "1）、本次预测服务时间为60-90分钟；\n" +
                        "2）、服务流程：\n" +
                        "核对排盘、校对准确度、针对您询问的事情详细讲解、提问交流；\n" +
                        "3）、预测服务尽量一气呵成，请安排好您的时间；\n" +
                        "4）、过程中请保持心平气静，避免杂念；\n" +
                        "5）、预测需有事求测，不宜抱着试探、戏弄、挑衅的心态，杂念占据上风则预测容易不准；\n" +
                        "6）、预测中如果有问题，可以提出，我会集中为您解答；");
                array.put("如果您对以上所测内容无疑问的话，我们就进入正式预测未来环节。没有特殊情况，您将不能退订此单。请问您是否同意继续预测？");
                array.put("请问是否还有其他问题？");
                array.put("如果没有其他问题，我们这次服务就结束了可以嘛？");
                array.put("谢谢您的信任，期待再次为您预测");
            } else {
                array = new JSONArray(result);
                array.put(0, "新增快捷回复");
            }
            result = array.toString();
        } catch (Exception e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }
}
