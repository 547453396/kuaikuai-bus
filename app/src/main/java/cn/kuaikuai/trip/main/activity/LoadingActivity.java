package cn.kuaikuai.trip.main.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.tbruyelle.rxpermissions.RxPermissions;

import cn.kuaikuai.base.activity.BaseActivity;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.utils.DeviceHelper;
import cn.kuaikuai.trip.utils.LocationUtils;
import rx.functions.Action1;

public class LoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity instance
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE).subscribe(new Action1<Boolean>() {


            @Override
            public void call(Boolean permission) {
                initConfigData();
                startActivity(new Intent(LoadingActivity.this,MainActivity.class));
                finish();
                overridePendingTransition(R.anim.alpha_show, R.anim.alpha_gone);
            }
        });
    }

    private void initConfigData() {
        DeviceHelper.initDeviceInfo(getApplicationContext());
        LocationUtils.getInstance(getApplicationContext()).startLocation(getLocalClassName());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
