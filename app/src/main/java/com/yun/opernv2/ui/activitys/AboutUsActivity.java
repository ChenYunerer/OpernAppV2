package com.yun.opernv2.ui.activitys;

import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.tencent.bugly.beta.Beta;
import com.yun.opernv2.BuildConfig;
import com.yun.opernv2.R;
import com.yun.opernv2.ui.bases.BaseActivity;
import com.yun.opernv2.utils.ToastUtil;

import butterknife.BindView;

public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.check_update_btn)
    Button checkUpdateBtn;

    @Override
    protected int contentViewRes() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initView() {
        Beta.checkUpgrade(true, false);
        checkUpdateBtn.setText(String.valueOf("当前版本 " + BuildConfig.BUILD_TYPE + BuildConfig.VERSION_NAME + " 检测更新"));
        RxView.clicks(checkUpdateBtn).subscribe(o -> {
            ToastUtil.showShort("123");
            Beta.checkUpgrade(true, false);
        });
    }
}
