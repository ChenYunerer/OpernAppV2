package com.yun.opernv2.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yun.opernv2.R;
import com.yun.opernv2.ui.activity.AboutUsActivity;
import com.yun.opernv2.ui.activity.DonateActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineFragment extends Fragment {

    @BindView(R.id.user_head_img)
    CircleImageView userHeadImg;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.user_info_tv)
    TextView userInfoTv;
    @BindView(R.id.app_cache_size_tv)
    TextView appCacheSizeTv;

    private Unbinder unbind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbind = ButterKnife.bind(this, view);
        initView();
        return view;
    }


    private void initView() {
        String androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        userInfoTv.setText(id);
    }


    @OnClick(R.id.clear_app_cache_rl)
    public void onClearAppCacheClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setPositiveButton("确定", (dialog, which) -> {

                })
                .setTitle("清除缓存")
                .setMessage("清除缓存会删除所有本地的曲谱哦~")
                .setCancelable(true)
                .create();
        alertDialog.show();
    }


    @OnClick(R.id.about_us_rl)
    public void onAboutUsRlClicked() {
        startActivity(new Intent(getContext(), AboutUsActivity.class));
    }

    @OnClick(R.id.donate_rl)
    public void OnDonateRlClicked() {
        startActivity(new Intent(getContext(), DonateActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbind.unbind();
    }
}
