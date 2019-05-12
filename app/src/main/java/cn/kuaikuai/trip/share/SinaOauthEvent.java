package cn.kuaikuai.trip.share;

/**
 * Created by lyc on 2015/1/8.
 * 主要用于在sinawb分享时候授权消息的回调
 */
public class SinaOauthEvent {

    public boolean isOauthSuccess = false;

    public SinaOauthEvent(boolean isOauthSuccess) {
        this.isOauthSuccess = isOauthSuccess;
    }
}
