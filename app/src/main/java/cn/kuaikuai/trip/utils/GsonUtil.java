package cn.kuaikuai.trip.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * create by guangqi at 18/7/19
 */
public class GsonUtil {

    public static <T> T JsonToObject(String json, Class<T> tClass) {

        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            T t1 = new Gson().fromJson(json, tClass);
            return t1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> JsonToList(String json) {

        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            Type type = new TypeToken<List<T>>() {
            }.getType();
            List<T> list = new Gson().fromJson(json, type);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String objToJsonString(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return new Gson().toJson(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
