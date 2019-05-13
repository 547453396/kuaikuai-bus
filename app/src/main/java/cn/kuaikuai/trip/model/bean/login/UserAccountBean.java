package cn.kuaikuai.trip.model.bean.login;

public class UserAccountBean {
    /**
     * alipay_account : string
     * avatar : string
     * create_time : 0
     * id : 0
     * idcard : string
     * nick_name : string
     * open_id : string
     * real_name : string
     * sex : 0
     * status : 0
     * tb_avatar : string
     * tb_nick_name : string
     * tb_open_sid : string
     * tb_openid : string
     * tel : 0
     * invite_code : string
     * has_contact : 0
     * "im_account": {
     * "accid": "string",
     * "token": "string"
     * },
     */

    private String alipay_account = "";
    private String avatar = "";
    private long create_time;
    private long id;
    private String idcard = "";
    private String nick_name = "";
    private String open_id = "";
    private String real_name = "";
    private int sex = -1;
    private int status;
    //永久金
    private String base_principal;

    //淘宝信息
    private String tb_avatar = "";
    private String tb_nick_name = "";
    private String tb_open_sid = "";
    private String tb_openid = "";
    private String relation_id = "";
    private String relation_name = "";
    private String special_id = "";
    private String sprcial_name = "";

    //微信信息
    private String wx_openid = "";
    private String wx_unionid = "";
    private String wx_nick_name = "";
    private String wx_avatar = "";


    public String getWx_account() {
        return wx_account;
    }

    public void setWx_account(String wx_account) {
        this.wx_account = wx_account;
    }

    private String wx_account = "";

    private long tel;
    private String invite_code = "";
    private int has_contact;
    private int level = 1;//1普通会员 2超级会员 3灰度总监 4总监
    private boolean has_input = false;//是否有师傅
    private ImAccountBean im_account;
    public MasterBean master;


    public String getRelationId() {
        return relation_id;
    }

    public String getRelationName() {
        return relation_name;
    }

    public String getSpecialId() {
        return special_id;
    }

    public String getSprcialName() {
        return sprcial_name;
    }

    public int getLevel() {
        return level;
    }

    public String getAlipay_account() {
        return alipay_account;
    }

    public void setAlipay_account(String alipay_account) {
        this.alipay_account = alipay_account;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getNickName() {
        return nick_name;
    }

    public void setNickName(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTbAvatar() {
        return tb_avatar;
    }

    public void setTb_avatar(String tb_avatar) {
        this.tb_avatar = tb_avatar;
    }

    public String getTbNickName() {
        return tb_nick_name;
    }

    public void setTb_nick_name(String tb_nick_name) {
        this.tb_nick_name = tb_nick_name;
    }

    public String getTb_open_sid() {
        return tb_open_sid;
    }

    public void setTb_open_sid(String tb_open_sid) {
        this.tb_open_sid = tb_open_sid;
    }

    public String getTb_openid() {
        return tb_openid;
    }

    public void setTb_openid(String tb_openid) {
        this.tb_openid = tb_openid;
    }

    public long getTel() {
        return tel;
    }

    public void setTel(long tel) {
        this.tel = tel;
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
    }

    public int getHas_contact() {
        return has_contact;
    }

    public void setHas_contact(int has_contact) {
        this.has_contact = has_contact;
    }

    public String getWxOpenid() {
        return wx_openid;
    }

    public String getWxUnionid() {
        return wx_unionid;
    }

    public String getWxNickName() {
        return wx_nick_name;
    }

    public String getWxAvatar() {
        return wx_avatar;
    }

    public ImAccountBean getIm_account() {
        return im_account;
    }

    public void setIm_account(ImAccountBean im_account) {
        this.im_account = im_account;
    }

    public boolean isHas_input() {
        return has_input;
    }

    public void setHas_input(boolean has_input) {
        this.has_input = has_input;
    }

    public String getBase_principal() {
        return base_principal;
    }

    public void setBase_principal(String base_principal) {
        this.base_principal = base_principal;
    }

    public static class ImAccountBean {
        /**
         * accid : 1d9d5eebd4914479a4f7f309655941ca
         * token : 6dbc37a1cf9df4cf482efca9ec56a146
         */

        private String accid = "";
        private String token = "";

        public String getAccid() {
            return accid;
        }

        public void setAccid(String accid) {
            this.accid = accid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

    }

    public static class MasterBean {
        public long uid;
        public String nick_name;
        public String avatar;
        public String accid;
    }
}
