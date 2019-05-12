package cn.kuaikuai.trip.utils;

import cn.etouch.ecalendar.nongliManager.CnNongLiManager;

/**
 * Created by xujun on 2017/8/11.
 * 时间选择器中公共的方法
 */

public class DatePickerUtils {
    /**
     * 获取农历月当前应该设置的位置
     * @return
     */
    public static int getNongLiMonthPosition(int now_year, int now_month, int isLeapMonth){
        CnNongLiManager cm = new CnNongLiManager();
        int leapMonth;
        try {
            leapMonth = cm.leapMonth(now_year);//当前年是闰几月,0表示不闰
        }catch (Exception e){
            leapMonth = 0;
        }
        if(leapMonth > 0 && leapMonth <= 12) {//有闰月
            if(now_month == leapMonth){//刚好负责到第几个月
                if(isLeapMonth == 1){//是闰月
                    return now_month;
                }else {
                    return now_month - 1;
                }
            }else if(now_month > leapMonth){
                return now_month;
            }else {
                return now_month - 1;
            }
        }else {
            return now_month - 1;
        }
    }

    /**
     * 获取当前位置是几月
     * @param now_year
     * @param position
     * @return
     */
    public static int[] getCurrentNongLiMonth(int now_year, int position){
        int result[] = new int[2];
        CnNongLiManager cm = new CnNongLiManager();
        int leapMonth;
        try {
            leapMonth = cm.leapMonth(now_year);//当前年是闰几月,0表示不闰
        }catch (Exception e){
            leapMonth = 0;
        }
        if(leapMonth > 0 && leapMonth <= 12) {//有闰月
            if(position >= leapMonth){//刚好负责到第几个月
                result [0] = position;
                if(position == leapMonth){
                    result [1] = 1;
                }
            }else {
                result [0] = position + 1;
            }
        }else {
            result [0] = position + 1;
        }
        return result;
    }

    /**
     * 获取当前年农历的数组，如果是闰月会包含闰几月
     * @param now_year
     * @return
     */
    public static String [] getNongLiMonths(int now_year){
        CnNongLiManager cm = new CnNongLiManager();
        int leapMonth;
        try {
            leapMonth = cm.leapMonth(now_year);//当前年是闰几月,0表示不闰
        }catch (Exception e){
            leapMonth = 0;
        }
        String[] lunarMonth;
        if(leapMonth > 0 && leapMonth <= 12){//有闰月
            lunarMonth = new String[13];
            for (int i = 0; i < 13; i++) {
                if(i == leapMonth){//刚好负责到第几个月
                    lunarMonth[i] = "闰" + CnNongLiManager.lunarMonth[i - 1];
                }else if(i < leapMonth){
                    lunarMonth[i] = CnNongLiManager.lunarMonth[i];
                }else {
                    lunarMonth[i] = CnNongLiManager.lunarMonth[i - 1];
                }
            }

        }else {
            lunarMonth = CnNongLiManager.lunarMonth;
        }
        return lunarMonth;
    }

}
