package com.yun.opern.ui.activitys;

import android.support.v7.app.AlertDialog;

import com.yun.opern.R;
import com.yun.opern.ui.bases.BaseActivity;

public class DonateActivity extends BaseActivity {


    @Override
    protected int contentViewRes() {
        return R.layout.activity_donate;
    }

    @Override
    protected void initView() {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("话不多说")
                .setMessage("嘿嘿...嘿嘿嘿")
                .setCancelable(true)
                .create();
        alertDialog.show();
    }
}
