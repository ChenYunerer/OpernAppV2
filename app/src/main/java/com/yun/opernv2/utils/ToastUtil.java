package com.yun.opernv2.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.yun.opernv2.Application;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;


/**
 * Toast统一管理类
 *
 * @author Yun
 */
public class ToastUtil {
    private static Toast toast;

    public static void showError(Throwable t) {
        if (t instanceof TimeoutException || t instanceof SocketTimeoutException) {
            ToastUtil.showShort("网络超时，请重试");
        } else if (t instanceof ConnectException || t instanceof UnknownHostException) {
            ToastUtil.showShort("网络连接错误，请检查网络");
        } else {
            ToastUtil.showShort("网络异常，请重试");
        }
    }

    public static void showShort(CharSequence message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(Application.getAppContext(), message, Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(message);
        }
        toast.show();
    }

    public static void hideToast() {
        if (null != toast) {
            toast.cancel();
        }
    }
}
