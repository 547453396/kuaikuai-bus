package cn.kuaikuai.trip.constant;

/**
 * 网络请求参数类
 * JXH
 * 2018/7/17
 */
public interface NetParamsConstant {

    /**
     * 99817749
     */
    String APP_KEY = "app_key";

    /**
     * 发起api请求时当前的时间戳，毫秒数，api请求在此毫秒数后5分钟内有效
     */
    String APP_TS = "app_ts";

    /**
     * API请求签名，签名方法为将当前api中除本app_sign参数以外的键值按照参数名的字典序排列append后得到的字符串，
     * 最后再将app_secret追加到最后面，进行MD5计算，app_sign为MD5值。MD5(key1value1key2value2 appSecret)
     */
    String APP_SIGN = "app_sign";

    /**
     * User唯一ID，在新用户注册时由注册接口返回，老用户登录时在登录接口返回
     */
    String UID = "uid";

    /**
     * Base64({"acctk":"","up":"","device":""})
     * acctk(登录用户的acctk)、up(见表1)、device(手机品牌+imei/idfa,IOS为IPHONE+idfa) 构造成的json然后Base64编码
     */
    String AUTH_TOKEN = "auth_token";

    /**
     * access token : 登录成功之后得到的一个鉴权的值，在所有需要登录的接口中必须传递，此值随时可能被服务器改变，
     * 客户端必须使用服务器返回的最新的acctk值。由服务器颁发，服务器销毁，客户端不可对此值做任何改变
     */
    String ACCTK = "acctk";

    /**
     * 版本code int值
     */
    String VER_CODE = "ver_code";

    /**
     * 版本号 如6.5.0
     */
    String VER_NAME = "ver_name";

    /**
     * 渠道
     */
    String CHANNEL = "channel";

    /**
     * 城市 地市级城市key
     */
    String CITY_KEY = "city_key";

    /**
     * 标识当前用户所在的平台，取值参见：表1 中的plaform字段，大小写铭感。有限取值，可以枚举
     */
    String UP = "up";

    /**
     * MD5(imei/idfa+mac)
     */
    String DEVICE_ID = "device_id";

    /**
     * 系统版本号 取值为版本号的前两位：如安卓4.1.6 则os_version=41，如果超过10，如10.0.1则100
     */
    String OS_VERSION = "os_version";

    /**
     * 标识当前用户所在的设备类型，可以是机型，设备串码等，服务器不会修改此值
     */
    String DEVICE = "device";
}
