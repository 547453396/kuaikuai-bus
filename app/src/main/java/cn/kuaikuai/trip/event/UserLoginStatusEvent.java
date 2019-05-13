package cn.kuaikuai.trip.event;

/**
 * Created by xujun on 17/2/27.
 * 用户登录状态的消息对象
 */
public class UserLoginStatusEvent {
    public int status;
    public boolean from_task;
    public static final int type_user_login = 0;//登录
    public static final int type_user_logout = 1;//登出

    public UserLoginStatusEvent(int status) {
        this(status, false);
    }

    public UserLoginStatusEvent(int status, boolean from_task) {
        this.status = status;
        this.from_task = from_task;
    }
}
