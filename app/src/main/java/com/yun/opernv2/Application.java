package com.yun.opernv2;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;


/**
 * Created by Yun on 2017/8/10 0010.
 */
public class Application extends android.app.Application {
    private static Application context;
    private static final String BUGLY_APP_ID = "4713b8ea88";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initBugly();
        initLog();
    }

    public static Context getAppContext() {
        return context;
    }

    /**
     * 初始化Bugly(APP异常捕获)
     */
    private void initBugly() {
        Bugly.init(getApplicationContext(), BUGLY_APP_ID, BuildConfig.DEBUG);
    }

    /**
     * 初始化Log
     */
    private void initLog() {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    /**
     * 设置app字体不随系统改变
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }
}
