package cn.kuaikuai.common.libs;

public class WeliLib {
    static{
        System.loadLibrary("WeliLib");
    }

    /**加密*/
    private native String doEncrypt(String str,int type);
    /**解密*/
    private native String doSecrypt(String str,int type);

    private static WeliLib weliLib=null;
    public static WeliLib getInstance(){
        if(weliLib==null){
            weliLib=new WeliLib();
        }
        return weliLib;
    }
    private WeliLib(){}


    /**执行加密操作
     * type =0时密钥为 U&ce...dCUR（前四位和后四位数）用户uid密钥
     * type =1时密钥为 G^*·...RCnq（前四位和后四位数）签名密钥
     * type =2时密钥为 Miad...TwET（前四位和后四位数）登录密钥
     * */
    public  String doTheEncrypt(String str,int type){
        String result=doEncrypt(str, type);
        return result;
    }
    /**执行解密操作
     * type =0时密钥为 U&ce...dCUR（前四位和后四位数）用户uid密钥
     * type =1时密钥为 G^*·...RCnq（前四位和后四位数）签名密钥
     * type =2时密钥为 Miad...TwET（前四位和后四位数）登录密钥
     * */
    public  String doTheSecrypt(String str,int type){
        String result=doSecrypt(str,type);
        return result;
    }

   
}
