package com.yun.opernv2.ui.activitys;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yun.opernv2.BuildConfig;
import com.yun.opernv2.R;
import com.yun.opernv2.ui.bases.BaseActivity;
import com.yun.opernv2.utils.CacheFileUtil;
import com.yun.opernv2.utils.ErrorMessageUtil;
import com.yun.opernv2.utils.NetworkUtils;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

import static com.yun.opernv2.utils.NetworkUtils.NetworkType.NETWORK_WIFI;

public class LauncherActivity extends BaseActivity {
    @BindView(R.id.tv_tip)
    TextView tvTip;

    private Disposable disposable;
    private Handler handler;
    private Runnable checkPermissionRunnable;

    @Override
    protected int contentViewRes() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void initView() {
        tvTip.setText(String.valueOf("搜谱 v " + BuildConfig.VERSION_NAME + " developed by 陈允"));
        tipNetType();
        checkPermissions();
    }

    private void tipNetType() {
        if (NetworkUtils.getNetworkType() != NETWORK_WIFI) {
            ErrorMessageUtil.showErrorByToast("当前处于非WIFI环境");
        }
    }

    private void checkPermissions() {
        handler = new Handler();
        checkPermissionRunnable = () -> {
            //检测权限
            RxPermissions reRxPermissions = new RxPermissions(LauncherActivity.this);
            disposable = reRxPermissions
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    //true表示获取权限成功（android6.0以下默认为true）
                                    //初始化缓存目录
                                    CacheFileUtil.init();
                                    startActivity(new Intent(context, MainActivity.class));
                                    finish();
                                } else {
                                    System.exit(0);
                                }
                            }
                    );
        };
        handler.postDelayed(checkPermissionRunnable, 1800);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(checkPermissionRunnable);
        }
        if (disposable != null) {
            disposable.dispose();
        }
    }

}
