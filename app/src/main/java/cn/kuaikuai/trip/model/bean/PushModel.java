package cn.kuaikuai.trip.model.bean;

/**
 * create by guangqi at 18/8/2
 * push 推送跳转的数据模型
 */
public class PushModel {

    /**
     * A : 邀请成功
     * B : 成功邀请好友加入，这是您的免费优惠券，请拿好
     * C : {"a":1,"b":"wlcc:","e":"1","img":"http://static.etouch.cn/zhwnl/default_icon/745.png"}
     */

    /*** 标题 */
    public String A;
    /*** 推送描述 */
    public String B;
    /*** 一些自定义事件及字段、跳转等 */
    public C C;

    public static class C {
        /**
         * a : 1
         * b : wlcc:
         * e : 1
         * img : http://static.etouch.cn/zhwnl/default_icon/745.png
         */

        /*** 消息类型 */
//        private int a;
        /*** 跳转的链接url或scheme或参数值 */
        public String b;
        /*** 消息的唯一标示字段(类似msgid) */
        public String e;
        /*** 展示图片icon的url */
        public String img;
        /*** 推送content_moudle字段，统计时将该字段取出原样放在统计报文中 */
        public Object c_m;
    }
}
