package com.yun.opernv2.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yun.opernv2.R;


/**
 * Created by Yun on 2018/1/8.
 * 文本显示dialog
 */
public class CommonDialog extends BaseDialog {
    private TextView tvContent;
    private String contentText;

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public CommonDialog(Context context) {
        super(context);
    }

    @Override
    protected View setContentView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_common_dialog_content, null);

        return view;
    }

    @Override
    protected void initContentView(View contentView) {
        if (TextUtils.isEmpty(titleStr)) {
            titleStr = "提示";
        }

        if (TextUtils.isEmpty(positiveButtonStr)) {
            positiveButtonStr = "确认";
        }

        if (TextUtils.isEmpty(negativeButtonStr)) {
            negativeButtonStr = "取消";
        }

        tvTitle.setVisibility(View.GONE);
//        setTitleText(titleStr);
        setPositiveButtonText(positiveButtonStr);
        setNegativeButtonText(negativeButtonStr);
        tvContent = contentView.findViewById(R.id.tv_content);
        tvContent.setText(contentText);
    }
}
