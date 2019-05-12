package cn.kuaikuai.common.net.mode;

/**
 * 封装的通用服务器返回对象，可自行定义
 */
public class ApiResult<T> {
    private int status;
    private String desc;
    private T data;

    public int getStatus() {
        return status;
    }

    public ApiResult setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public ApiResult setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public T getData() {
        return data;
    }

    public ApiResult setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "status=" + status +
                ", desc='" + desc + '\'' +
                ", data=" + data +
                '}';
    }
}
