package cn.kuaikuai.common.net.convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.net.UnknownServiceException;

import cn.kuaikuai.common.net.exception.ApiException;
import cn.kuaikuai.common.net.mode.ApiResult;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @Description: ResponseBody to T
 */
final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        if (adapter != null && gson != null) {
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                T data = adapter.read(jsonReader);
                if (data == null) throw new UnknownServiceException("server back data is null");
                if (data instanceof ApiResult) {
                    ApiResult apiResult = (ApiResult) data;
                    if (!ApiException.isSuccess(apiResult)) {
                        throw new UnknownServiceException(apiResult.getDesc() == null ? "unknow error" : apiResult.getDesc());
                    }
                }
                return data;
            } finally {
                value.close();
            }
        } else {
            return null;
        }
    }
}
