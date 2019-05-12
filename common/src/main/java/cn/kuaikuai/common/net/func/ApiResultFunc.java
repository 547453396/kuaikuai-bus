package cn.kuaikuai.common.net.func;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cn.kuaikuai.common.LogUtils;
import cn.kuaikuai.common.net.gson.ParameterizedTypeImpl;
import cn.kuaikuai.common.net.mode.ApiResult;
import cn.kuaikuai.common.net.mode.ResultBean;
import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * ResponseBody转ApiResult<T>
 */
public class ApiResultFunc<T> implements Func1<ResponseBody, ApiResult<T>> {
    private Class<T> clazz;

    public ApiResultFunc(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ApiResult<T> call(ResponseBody responseBody) {
        Gson gson = new Gson();
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setStatus(-1);
        ResultBean result = null;
        try {
            String json = responseBody.string();
            result = parseApiResult(json);
            LogUtils.i("ApiResultFunc", "responseBody:" + json);
            if (result != null) {
                if (result.getData() != null) {
                    if (clazz.equals(String.class)) {
                        apiResult.setData((T) result.getData());
                    } else {
                        String resultData = result.getData();
                        T data;
                        //判断是否为集合
                        if (resultData.startsWith("[") && resultData.endsWith("]")) {
                            //这里传入泛型T，却返回一个list<T>，容易造成使用困惑
                            data = fromJsonArray(gson, result.getData(), clazz);
                        } else {
                            data = fromJsonObject(gson, result.getData(), clazz);
                        }
                        apiResult.setData(data);
                    }
                    apiResult.setDesc(result.getDesc());
                } else {
                    apiResult.setDesc(result.getDesc());
                }
                apiResult.setStatus(result.getStatus());
            } else {
                apiResult.setDesc("ResponseBody's string is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            apiResult.setDesc(e.getMessage());
        } finally {
            responseBody.close();
        }
        return apiResult;
    }

    private ResultBean parseApiResult(String json) throws JSONException {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        ResultBean resultBean = new ResultBean();
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("status")) {
            resultBean.setStatus(jsonObject.getInt("status"));
        }
        if (jsonObject.has("data")) {
            resultBean.setData(jsonObject.getString("data"));
        }
        if (jsonObject.has("desc")) {
            resultBean.setDesc(jsonObject.getString("desc"));
        }
        return resultBean;
    }

    public static <T> T fromJsonObject(Gson gson, String reader, Class<T> clazz) {
        return gson.fromJson(reader, clazz);
    }

    public static <T> T fromJsonArray(Gson gson, String reader, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        return gson.fromJson(reader, listType);
    }
}
