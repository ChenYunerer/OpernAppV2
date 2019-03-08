package com.yun.opernv2.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yun.opernv2.R;

import androidx.annotation.NonNull;


/**
 * Created by Yun on 2018/2/11 0011.
 * 加载dialog
 */
public class LoadingDialog extends Dialog {
    private ProgressBar mProgress;
    private TextView mMessage;
    private String mMessageStr;

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public static LoadingDialog show(Context context, String message) {
        LoadingDialog dialog = new LoadingDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setTitle(null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_loading_dialog);
        initView();
    }

    private void initView() {
        mProgress = findViewById(R.id.progress);
        mMessage = findViewById(R.id.message);
        mMessage.setText(mMessageStr);
    }

    private void setMessage(String message) {
        mMessageStr = message;
    }
}
