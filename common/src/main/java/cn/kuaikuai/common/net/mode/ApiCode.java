package cn.kuaikuai.common.net.mode;

/**
 * API Code，包含Http/Request/Response
 */
public class ApiCode {

    public static class Http {
        /*==========对应HTTP的状态码=================*/
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int REQUEST_TIMEOUT = 408;
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int BAD_GATEWAY = 502;
        public static final int SERVICE_UNAVAILABLE = 503;
        public static final int GATEWAY_TIMEOUT = 504;
        /*=======================================*/
    }

    public static class Request {
        /*===========Request请求码================*/
        //未知错误
        public static final int UNKNOWN = 1000;
        //解析错误
        public static final int PARSE_ERROR = 1001;
        //网络错误
        public static final int NETWORK_ERROR = 1002;
        //协议出错
        public static final int HTTP_ERROR = 1003;
        //证书出错
        public static final int SSL_ERROR = 1005;
        //连接超时
        public static final int TIMEOUT_ERROR = 1006;
        //调用错误
        public static final int INVOKE_ERROR = 1007;
        //类转换错误
        public static final int CONVERT_ERROR = 1008;
        /*========================================*/
    }

    public static class Response {
        /*===========Response响应码================*/
        //HTTP请求成功状态码
        public static final int HTTP_SUCCESS = 1000;
        //请求的HTTP METHOD不支持，请检查是否选择了正确的POST/GET方式
        public static final int HTPP_METHOD_ERROR = 1001;
        //Parameters Error 请求参数错误，具体请参考API文档
        public static final int PARAMETERS_ERROR = 1002;
        //Database Error 服务端数据库异常
        public static final int DATABASE_ERROR = 1003;
        //NOT Login	用户未登录
        public static final int NOT_LOGIN = 1004;
        //Access Forbidden	无访问权限
        public static final int ACCESS_FORBIDDEN = 1005;
        //Unknow Error	未知服务错误
        public static final int UNKNOW_ERROR = 1006;
        //服务器错误
        public static final int SERVER_ERROR = 1007;
        //服务忙，稍后再试
        public static final int SERVER_BUSY = 1008;
        //Oauth验证失败
        public static final int OAUTH_ERROR = 1008;
        //用户名已存在
        public static final int USER_NAME_EXIST = 1010;
        //请求body超过长度限制
        public static final int REQUEST_LENGTH_LIMIT = 1011;
        //请求频次超过上限
        public static final int REQUESTS_RATE_LIMIT = 1012;
        /*============================================*/
    }

}
