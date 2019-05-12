package cn.kuaikuai.common.net.api;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

public interface ApiService {
    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url() String url, @FieldMap Map<String, Object> maps);

    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> postForm(@Url() String url, @FieldMap Map<String, Object> maps);

    @POST()
    Observable<ResponseBody> postJson(@Url() String url, @Body RequestBody jsonBody);

    @POST()
    Observable<ResponseBody> postJson(@Url() String url, @Body RequestBody jsonBody, @QueryMap Map<String, Object> maps);

    @POST()
    Observable<ResponseBody> postBody(@Url() String url, @Body Object object);

    @POST()
    Observable<ResponseBody> postBody(@Url() String url, @Body Object object, @QueryMap Map<String, Object> maps);

    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> maps);

    @DELETE()
    Observable<ResponseBody> delete(@Url() String url, @QueryMap Map<String, Object> maps);

    @PUT()
    Observable<ResponseBody> put(@Url() String url, @QueryMap Map<String, Object> maps);

    @PUT()
    Observable<ResponseBody> put(@Url() String url, @Body RequestBody jsonBody, @QueryMap Map<String, Object> maps);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadImage(@Url() String url,
                                         @Part("file\"; filename=\"image" + ".jpg") RequestBody
                                                 requestBody);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadImage(@Url() String url,
                                         @Part("file\"; filename=\"image" + ".jpg") RequestBody
                                                 requestBody, @Part() List<MultipartBody.Part> parts);

    @Multipart
    @POST()
    Call<ResponseBody> uploadImageCall(@Url() String url, @Part("file\"; filename=\"image" + ".jpg") RequestBody requestBody, @Part() List<MultipartBody.Part> parts);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadAudio(@Url() String url,
                                         @Part("file\"; filename=\"audio" + ".aac") RequestBody
                                                 requestBody, @Part() List<MultipartBody.Part> parts);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFile(@Url String fileUrl,
                                        @Part("description") RequestBody description, @Part("files") MultipartBody.Part file);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @PartMap() Map<String, RequestBody>
            maps);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String path, @Part() List<MultipartBody.Part> parts);

    @GET()
    Call getCall(String url, Map<String, String> maps);

    @POST()
    Call<ResponseBody> postJsonCall(@Url() String url, @Body RequestBody jsonBody, @QueryMap Map<String, Object> maps);
}
