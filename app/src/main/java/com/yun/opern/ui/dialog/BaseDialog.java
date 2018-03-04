package com.yun.opern.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yun.opern.R;


/**
 * Created by Yun on 2018/1/7.
 * base dialog
 */
public abstract class BaseDialog extends Dialog {

    protected TextView tvTitle;
    private FrameLayout flContent;
    protected Button btnNegative;
    protected Button btnPositive;
    protected View btnButton;
    protected View divider;
    private OnNegativeButtonClickListener onNegativeButtonClickListener;
    private OnPositiveButtonClickListener onPositiveButtonClickListener;

    protected String titleStr;
    protected String positiveButtonStr;
    protected String negativeButtonStr;
    private View contentView;


    public interface OnNegativeButtonClickListener {
        void onClick(Dialog dialog, View view);
    }

    public interface OnPositiveButtonClickListener {
        void onClick(Dialog dialog, View view);
    }

    public void setOnNegativeButtonClickListener(OnNegativeButtonClickListener onNegativeButtonClickListener) {
        this.onNegativeButtonClickListener = onNegativeButtonClickListener;
    }

    public void setOnPositiveButtonClickListener(OnPositiveButtonClickListener onPositiveButtonClickListener) {
        this.onPositiveButtonClickListener = onPositiveButtonClickListener;
    }

    public void setTitleText(String titleStr) {
        this.titleStr = titleStr;
    }


    public void setPositiveButtonText(String positiveButtonStr) {
        this.positiveButtonStr = positiveButtonStr;
    }

    public void setNegativeButtonText(String negativeButtonStr) {
        this.negativeButtonStr = negativeButtonStr;
    }

    protected abstract View setContentView();

    protected abstract void initContentView(View contentView);

    public BaseDialog(Context context) {
        super(context);
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
        setContentView(R.layout.layout_base_dialog);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView() {
        contentView = setContentView();
        tvTitle = findViewById(R.id.tv_title);
        flContent = findViewById(R.id.fl_content);
        btnNegative = findViewById(R.id.btn_negative);
        btnPositive = findViewById(R.id.btn_positive);
        btnButton = findViewById(R.id.button_btn);
        divider = findViewById(R.id.divider);
        btnNegative.setOnClickListener(v -> {
            if (onNegativeButtonClickListener != null) {
                onNegativeButtonClickListener.onClick(this, v);
            }
            dismiss();
        });

        btnPositive.setOnClickListener(v -> {
            if (onPositiveButtonClickListener != null) {
                onPositiveButtonClickListener.onClick(this, v);
            }
        });
        initContentView(contentView);
        if (titleStr != null && !titleStr.equals("")) {
            tvTitle.setText(titleStr);
        }
        if (positiveButtonStr != null && !positiveButtonStr.equals("")) {
            btnPositive.setText(positiveButtonStr);
        }
        if (negativeButtonStr != null && !negativeButtonStr.equals("")) {
            btnNegative.setText(negativeButtonStr);
        }
        if (contentView != null) {
            flContent.removeAllViews();
            flContent.addView(contentView);
        }
    }

}
