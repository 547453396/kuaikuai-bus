package cn.kuaikuai.trip.event;

/**
 * JXH
 * 2018/8/14
 */
public class ShareEvent {
    /**
     * 0 发送成功
     * 1 发送取消
     * 2 发送失败
     */
    int mType;

    public ShareEvent(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
