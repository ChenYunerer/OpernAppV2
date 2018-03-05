package com.yun.opernv2.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.lang.ref.SoftReference;

/**
 * Created by Yun on 2017/8/29 0029.
 */

public class WeiBoUserInfoKeeper {
    private static final String WEIBO_USER_INFO = "weibo_user_info";
    private static SoftReference<WeiBoUserInfo> weiBoUserInfoSoftReference;

    public static void write(Context context, WeiBoUserInfo weiBoUserInfo) {
        weiBoUserInfoSoftReference = null;
        weiBoUserInfoSoftReference = new SoftReference<>(weiBoUserInfo);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(WEIBO_USER_INFO, new Gson().toJson(weiBoUserInfo));
        editor.apply();
    }

    public static WeiBoUserInfo read(Context context) {
        if(weiBoUserInfoSoftReference != null && weiBoUserInfoSoftReference.get() != null){
            return weiBoUserInfoSoftReference.get();
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(WEIBO_USER_INFO, "");
        WeiBoUserInfo weiBoUserInfo = new Gson().fromJson(json, WeiBoUserInfo.class);
        weiBoUserInfoSoftReference = null;
        weiBoUserInfoSoftReference = new SoftReference<>(weiBoUserInfo);
        return weiBoUserInfo;
    }

    public static void clear(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(WEIBO_USER_INFO);
        editor.apply();
        weiBoUserInfoSoftReference = null;
    }

}
