package com.yun.opernv2.ui.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.yun.opernv2.ui.dialog.BaseDialog;
import com.yun.opernv2.ui.dialog.CommonDialog;
import com.yun.opernv2.ui.dialog.LoadingDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Yun on 2017/8/25 0025.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Context context;
    protected LoadingDialog loadingDialog;
    protected CommonDialog alertDialog;
    protected Unbinder unbinder;

    protected abstract int contentViewRes();

    protected abstract void initView();

    protected void initedView(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        context = this;
        setContentView(contentViewRes());
        unbinder = ButterKnife.bind(this);
        initView();
        initedView();
    }

    public void showProgressDialog(boolean show) {
        if(show){
            loadingDialog = LoadingDialog.show(context, "请求中···");
        }else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    protected void showDialog(String title, String message, String positive, BaseDialog.OnPositiveButtonClickListener positiveListener) {
        alertDialog = new CommonDialog(context);
        if (title != null) {
            alertDialog.setTitleText(title);
        }
        if (message != null) {
            alertDialog.setContentText(message);
        }
        if (positive != null && positiveListener != null) {
            alertDialog.setPositiveButtonText(positive);
            alertDialog.setOnPositiveButtonClickListener(positiveListener);
        }
        alertDialog.show();
    }

    protected void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
