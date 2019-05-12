package cn.kuaikuai.trip.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.auth.LoginInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.etouch.ecalendar.nongliManager.CnNongLiManager;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.view.pullrefreshview.utils.DensityUtil;

public class UtilsManager {
    /**
     * 判断电话号码是否合法
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        // 区号+座机号码+分机号码：regexp="^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$"
        // 手机(中国移动手机号码)：regexp="^((\(\d{3}\))|(\d{3}\-))?13[456789]\d{8}|15[89]\d{8}"

        // 所有手机号码：regexp="^((\(\d{3}\))|(\d{3}\-))?13[0-9]\d{8}|15[89]\d{8}"(新添加了158,159两个号段)
//		String expression = "^(((13)(\\d{9}))|((14)(0|5|7)(\\d{8}))|((15)(0|1|2|3|5|6|7|8|9)(\\d{8}))|((18)(0|1|2|3|5|6|7|8|9)(\\d{8}))|((17)(0)(\\d{8})))$";
        // String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        //手机号码以1开头的11位数
        String expression = "^((\\+86){0,1}((1)(\\d{10})))$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);

        // Pattern pattern2 = Pattern.compile(expression2);
        // Matcher matcher2 = pattern2.matcher(inputStr);
        if (matcher.matches()) {// || matcher2.matches()
            isValid = true;
        }
        return isValid;
    }

    /**
     * add by zxl 20131124 edittext set error message
     */
    public static Spanned errorHtml(Context ctx, int stringId) {
        return Html.fromHtml("<font color=\"#ffffff\">"
                + ctx.getResources().getString(stringId) + "</font>");
    }

    public static Spanned errorHtml(Context ctx, String msg) {
        return Html.fromHtml("<font color=\"#000000\">"
                + msg + "</font>");
    }

    private static Toast toast = null;

    /**
     * Toast消息
     */
    public static void Toast(Context ctx, int stringId) {
        Toast(ctx, ctx.getResources().getString(stringId));
    }

    public static void Toast(Context ctx, String string) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(ctx, string, Toast.LENGTH_SHORT);
        View inflate = View.inflate(ctx, R.layout.layout_toast, null);
        toast.setView(inflate);
        toast.setGravity(Gravity.CENTER, 0, 0);
        TextView tvContent = toast.getView().findViewById(R.id.tv_content);
        if (tvContent != null) {
            tvContent.setText(string);
        }
        toast.show();
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    /**
     * 保存bitmap到路径
     *
     * @param folder
     * @param bitmap
     * @return
     */
    public static String saveBitmapToPath(String folder, Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        File file = new File(folder, System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * Converts a byte array into a String hexidecimal characters
     * <p/>
     * null returns null
     */
    private static String bytesToHexString(byte[] bytes) {
        if (bytes == null)
            return null;
        String table = "0123456789abcdef";
        StringBuilder ret = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int b;
            b = 0x0f & (bytes[i] >> 4);
            ret.append(table.charAt(b));
            b = 0x0f & bytes[i];
            ret.append(table.charAt(b));
        }
        return ret.toString();
    }

    /**
     * 计算给定 byte [] 串的 MD5
     */
    private static byte[] MD5(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(input);
            return md.digest();
        } else
            return null;
    }

    /**
     * 获取md5字符串
     */
    public static String getMD5(byte[] input) {
        return bytesToHexString(MD5(input));
    }

    /**
     * 通知栏icon id
     **/
    public static int getNotificationIconResId() {
        return R.mipmap.ic_launcher;
    }


    /**
     * 获取当前进程名字
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess :
                    mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return null;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static float scale = -1;

    public static int dp2px(Context context, float dpValue) {
        if (scale == -1) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将整数转化为两位的string
     */
    public static String intTo2String(int num) {
        return String.format(Locale.getDefault(), "%02d", num);
    }

    /**
     * 获取年月日拼接字符串
     *
     * @param year
     * @param month
     * @param date
     * @param isGongli
     * @return
     */
    public static String getYearAndMonth(Context context, int year, int month, int date, boolean isGongli) {
        return getYearAndMonth(context, year, month, date, isGongli, true);
    }

    /**
     * 获取年月日拼接字符串
     *
     * @param year
     * @param month
     * @param date
     * @param isGongli
     * @param isShowText 是显示年月日还是 -- 的方式
     * @return
     */
    public static String getYearAndMonth(Context context, int year, int month, int date, boolean isGongli, boolean isShowText) {
        return getYearAndMonth(context, year, month, date, isGongli, isShowText, 0);
    }

    /**
     * 通过传是否显示文字还是横杠，如果是true，显示文字
     *
     * @param year
     * @param month
     * @param date
     * @param isGongli
     * @param isShowText
     * @param isLeapMonth 是否是闰月在 农历的时候有效
     * @return
     */
    public static String getYearAndMonth(Context context, int year, int month, int date, boolean isGongli, boolean isShowText, int isLeapMonth) {

        if (isGongli) {
            if (year <= 0) {
                if (isShowText) {
                    return UtilsManager.intTo2String(month) + context.getString(R.string.str_month) +
                            UtilsManager.intTo2String(date) +
                            context.getString(R.string.str_day);
                } else {
                    return UtilsManager.intTo2String(month) + "-" + UtilsManager.intTo2String(date);
                }

            } else {
                if (isShowText) {
                    return year + context.getString(R.string.str_year) + UtilsManager.intTo2String(month)
                            + context.getString(R.string.str_month) + UtilsManager.intTo2String(date)
                            + context.getString(R.string.str_day);
                } else {
                    return year + "-" + UtilsManager.intTo2String(month)
                            + "-" + UtilsManager.intTo2String(date);
                }
            }
        } else { //农历
            if (month <= 0 || date <= 0) {
                return "";
            }
            //月日不能大于12和30不然要越界，加判断是防止服务器返回错误
            if (month > 12) {
                month = 12;
            }
            if (date > 30) {
                date = 30;
            }
            if (year <= 0) {//农历忽略年是不会有闰月的
                return CnNongLiManager.lunarMonth[month - 1] + CnNongLiManager.lunarDate[date - 1];
            } else {
                String[] lunarMonth = DatePickerUtils.getNongLiMonths(year);
                return year + context.getString(R.string.str_year)
                        + lunarMonth[DatePickerUtils.getNongLiMonthPosition(year, month, isLeapMonth)]
                        + CnNongLiManager.lunarDate[date - 1];
            }

        }
    }

    /**
     * 获取云信信息
     */
    public static LoginInfo getImLoginInfo(Context context) {
        String account = UserAccountPreferences.getInstance(context).getImAccid();
        String token = UserAccountPreferences.getInstance(context).getImToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        try {
            int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = ctx.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 显示软键盘
     */
    public static void showKeyBord(final Context ctx, final EditText edt) {
        edt.requestFocus();
        Timer timer = new Timer(); // 设置定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() { // 弹出软键盘的代码
                if (edt.getParent() != null) {
                    InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edt, InputMethodManager.RESULT_SHOWN);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        }, 200);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyBord(Context ctx, EditText edt) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
        }
    }

    public static void hideKeyBord(Context context, Window window) {
        if (context == null || window == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(window.getDecorView().getWindowToken(), 0);
        }
    }


    /**
     * 通过比较详细的参数属性设置view的默认和选中样式
     *
     * @param view
     * @param stroke         边框宽度 默认必须传人dp转px
     * @param stroke_color_1 边框默认颜色
     * @param stroke_color_2 边框选中的颜色
     * @param bg_color_1     背景默认颜色
     * @param bg_color_2     背景点击状态的颜色
     * @param radius         圆角的大小，如果相传dp，请用dip2px方法转成px，
     */
    public static void setViewSelectorBg(View view, int stroke,
                                         int stroke_color_1, int stroke_color_2,
                                         int bg_color_1, int bg_color_2,
                                         int radius) {
        setViewSelectorBg(view, stroke,
                stroke_color_1, stroke_color_2,
                bg_color_1, bg_color_2,
                radius, radius,
                radius, radius);
    }

    /**
     * 设置viewd点击和显示的默认样式，这个是总方法，
     * 显示，点击时候的状态，主要就是两个色值
     * 圆角：传入每个角的圆角大小，四个圆角的顺序为左上，右上，右下，左下,并且四个角都为dp转px
     * 边宽：如果传入的边框不大于0 这不显示边框，边框同样有两个颜色,如果边框没有，那么颜色也用不到
     *
     * @param view
     * @param stroke         边框宽度 默认必须传人dp转px
     * @param stroke_color_1 边框默认颜色
     * @param stroke_color_2 边框选中的颜色
     * @param bg_color_1     背景默认颜色
     * @param bg_color_2     背景点击状态的颜色
     * @param radius_l_t     左上
     * @param radius_r_t     右上
     * @param radius_r_b     右下
     * @param radius_l_b     左下
     */
    public static void setViewSelectorBg(View view, int stroke,
                                         int stroke_color_1, int stroke_color_2,
                                         int bg_color_1, int bg_color_2,
                                         float radius_l_t, float radius_r_t,
                                         float radius_r_b, float radius_l_b) {
        //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        float radius[] = new float[]{radius_l_t, radius_l_t, radius_r_t, radius_r_t, radius_r_b, radius_r_b, radius_l_b, radius_l_b};
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(bg_color_1);
        normalDrawable.setCornerRadii(radius);
        if (stroke > 0) {
            normalDrawable.setStroke(stroke, stroke_color_1);
        }
        GradientDrawable selectedDrawable = new GradientDrawable();
        selectedDrawable.setColor(bg_color_2);
        selectedDrawable.setCornerRadii(radius);
        if (stroke > 0) {
            selectedDrawable.setStroke(stroke, stroke_color_2);
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed}, normalDrawable);
        stateListDrawable.addState(new int[]{-android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed}, selectedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused, -android.R.attr.state_pressed}, selectedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, selectedDrawable);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(stateListDrawable);
        } else {
            view.setBackgroundDrawable(stateListDrawable);
        }
    }


    /**
     * 设置textView 的字体颜色和颜色 --> 对应的背景
     *
     * @param textView
     * @param position 根据位置标签，设置textView的字体和背景
     */
    public static void setTextDefaultColorWithBg(TextView textView, int position) {
        /*** 默认背景色 */
        int[] bgIds = {R.drawable.round_corner_color_ffad0b_10_bg, R.drawable.round_corner_pink_rect_bg, R.drawable.round_corner_green_rect_bg, R.drawable.round_corner_blue_rect_bg};
        /*** 默认字体色 与背景对应 */
        int[] colorIds = {R.color.color_FFAD0B, R.color.color_ff7c92, R.color.color_66dab5, R.color.color_theme};

        int rem = position % colorIds.length;

        textView.setTextColor(ActivityCompat.getColor(textView.getContext(), colorIds[rem]));
        textView.setBackgroundResource(bgIds[rem]);
    }

    /***
     * 背景是圆角
     * @param textView
     * @param colorId
     * @param bgcolorId
     */
    public static void setTextColorWithRoundBg(TextView textView, int colorId, int bgcolorId) {
        textView.setTextColor(ActivityCompat.getColor(textView.getContext(), colorId));
        setViewSelectorBg(textView, 0, 0, 0, bgcolorId, 0, DensityUtil.dp2px(3));
    }

    /**
     * 判断能否使用外置存储
     */
    public static boolean isExternalStorageExist(Context context) {
        String externalRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        String externalPath = Environment.getExternalStorageDirectory().getPath();
        String sdkStorageRoot = externalPath + "/" + context.getPackageName() + "/";
        if (sdkStorageRoot.startsWith(externalRoot)) {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } else {
            return true;
        }
    }

    public static long getSecondsByMilliseconds(long milliseconds) {
        long seconds = new BigDecimal((float) ((float) milliseconds / (float) 1000)).setScale(0,
                BigDecimal.ROUND_HALF_UP).intValue();
        return seconds;
    }

    public static final String FORMAT_MONEY_1 = "¥ ##0.00";
    /*** 去掉小数点后为0的 */
    public static final String FORMAT_MONEY_2 = "¥ ##0.##";
    public static final String FORMAT_MONEY_3 = "##0.00";

    /**
     * 格式化价格，单位分，
     *
     * @param number 金额单位分
     * @param format 默认(¥ 0.00);
     */
    public static String formatToMoney(long number, String format) {
        double money = number / 100.0;
        if (TextUtils.isEmpty(format)) {
            format = FORMAT_MONEY_1;
        }
        DecimalFormat fnum = new DecimalFormat(format);
        try {
            return fnum.format(money);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fnum.format(0.0);
    }

    public static boolean isAppInstalled(Context context, String pkg) {
        final PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 判断QQ是否安装
     */
    public static boolean isQQClientAvailable(Context context) {
        return isAppInstalled(context, "com.tencent.mobileqq");
    }

    /**
     * 小于1分钟显示刚刚，小于1小时显示x分钟前，小于24小时显示x小时前，小于10天显示x天前，大于等于10天都显示9天前
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        SimpleDateFormat format;
        long jiange = (System.currentTimeMillis() - time) / 1000;
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.setTimeInMillis(time);
        int theYear = cal.get(Calendar.YEAR);
        if (jiange < 60) {
            if (jiange < -10) {//未来的时间则显示format格式
                if (currentYear == theYear) {
                    format = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                } else {
                    format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                }
                return format.format(new Date(time));
            }
            return "刚刚";
        } else if (jiange < 3600) {
            return jiange / 60 + "分钟前";
        } else if (jiange < 86400) {
            return jiange / 3600 + "小时前";
        } else {
            int day = (int) (jiange / 86400);
            if (day >= 10) {
                day = 9;
            }
            return day + "天前";
        }
    }
}
