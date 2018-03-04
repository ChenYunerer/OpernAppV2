package com.yun.opern.ui.bases;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yun.opern.ui.dialog.BaseDialog;
import com.yun.opern.ui.dialog.CommonDialog;
import com.yun.opern.ui.dialog.LoadingDialog;
import com.yun.opern.views.ProgressDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Yun on 2017/8/25 0025.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Context context;
    protected LoadingDialog progressDialog;
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

    protected void showProgressDialog(boolean show, DialogInterface.OnCancelListener onCancelListener) {
        if(show){
            if(progressDialog == null){
                progressDialog = new LoadingDialog(context);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(true);
                if(onCancelListener != null){
                    progressDialog.setOnCancelListener(onCancelListener);
                }
            }
            progressDialog.show();
        }else {
            if(progressDialog != null && progressDialog.isShowing()){
                new Handler().postDelayed(() -> progressDialog.dismiss(), 300);
            }
        }
    }

    protected void showProgressDialog(boolean show) {
        showProgressDialog(show, null);
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
