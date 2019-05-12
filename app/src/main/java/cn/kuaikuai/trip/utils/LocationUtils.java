package cn.kuaikuai.trip.utils;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.kuaikuai.common.LogUtils;

/**
 * 定位类，用于定位当前位置，位置通过回调方式返回；
 * (每次定位到当前位置后，均将经纬度、位置描述、地级市名，地级市key,区县名、区县key存储在缓存中)
 */
public class LocationUtils implements AMapLocationListener {

    private static LocationUtils mLocationUtils = null;

    public static LocationUtils getInstance(Context ctx) {
        if (mLocationUtils == null) {
            mLocationUtils = new LocationUtils(ctx.getApplicationContext());
        }
        return mLocationUtils;
    }

    public static void clearInstance() {
        if (mLocationUtils != null) {
            mLocationUtils.onPause();
        }
        mLocationUtils = null;
    }

    private Context ctx;
    private AMapLocationClient mLocationManagerProxy;
    private AMapLocationClientOption mLocationOption;
    /**
     * city1/cityKey1 地级市城市，city2/cityKey2 区县城市
     */
    private String lon = "", lat = "", address = "", city1 = "", city2 = "", cityKey1 = "", cityKey2 = "";
    private String adCode = "";

    private LocationUtils(Context ctx) {
        this.ctx = ctx;
        init();
    }

    /**
     * @param from 调用该方法的类的类名，确保listeners中item的唯一性
     */
    public void startLocation(String from) {
        //启动定位
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.startLocation();
        }
    }

    /**
     * 初始化定位
     */
    private void init() {
        // 初始化定位，只采用网络定位
        mLocationManagerProxy = new AMapLocationClient(ctx);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationManagerProxy.setLocationOption(mLocationOption);
        mLocationManagerProxy.setLocationListener(this);
    }

    public void onPause() {
        if (mLocationManagerProxy != null) {
            // 移除定位请求
            mLocationManagerProxy.stopLocation();
            // 销毁定位
            mLocationManagerProxy.onDestroy();
            mLocationManagerProxy = null;
        }
    }

    @Override
    public void onLocationChanged(final AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            try {
                mLocationManagerProxy.stopLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }
            city1 = amapLocation.getCity();
            city2 = amapLocation.getDistrict();
            if (!TextUtils.isEmpty(city2)) {
                lon = String.valueOf(amapLocation.getLongitude());
                lat = String.valueOf(amapLocation.getLatitude());
                address = amapLocation.getAddress();
            }
            adCode = amapLocation.getAdCode();
            LogUtils.d("city1:"+city1);
            LogUtils.d("city2:"+city2);

            LogUtils.d("lon:" + lon + "--lat:" + lat + "--address:" + address);
        }
    }
}
