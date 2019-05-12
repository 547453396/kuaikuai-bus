package cn.kuaikuai.trip.push;

/**
 * 服务器定义的消息格式
 * http://pm.etouch.cn/doc-view-1865.html
 */
public interface PushInfo{

        /**ACTIVITY(a)：代表跳转方式*/
        interface a {
            int HTTP = 1;//http超链接或scheme
        }

        /**MODULE(g):客户端路径,即跳转至APP后按返回的跳转路径 */
        interface g {
            int NONE = -1;//空
        }

        /** tag(h):分类标签 */
        interface h {
            int TAG_DEFAULT = -1;//生活圈消息
        }
    }