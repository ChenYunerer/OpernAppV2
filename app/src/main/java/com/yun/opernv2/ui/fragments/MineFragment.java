package com.yun.opernv2.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.yun.opernv2.R;
import com.yun.opernv2.common.WeiBoUserInfo;
import com.yun.opernv2.common.WeiBoUserInfoKeeper;
import com.yun.opernv2.model.event.OpernFileDeleteEvent;
import com.yun.opernv2.model.event.StartWeiBoAuthorizeEvent;
import com.yun.opernv2.model.event.WeiBoAuthorizeSuccessEvent;
import com.yun.opernv2.net.HttpCore;
import com.yun.opernv2.ui.activitys.AboutUsActivity;
import com.yun.opernv2.ui.activitys.DonateActivity;
import com.yun.opernv2.ui.activitys.MyCollectionActivity;
import com.yun.opernv2.ui.bases.BaseActivity;
import com.yun.opernv2.utils.CacheFileUtil;
import com.yun.opernv2.utils.LogUtil;
import com.yun.opernv2.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

public class MineFragment extends Fragment {

    @BindView(R.id.my_download_rl)
    RelativeLayout myDownloadRl;
    @BindView(R.id.my_collection_rl)
    RelativeLayout myCollectionRl;
    @BindView(R.id.about_us_rl)
    RelativeLayout aboutUsRl;
    @BindView(R.id.user_head_img)
    CircleImageView userHeadImg;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.user_info_tv)
    TextView userInfoTv;
    @BindView(R.id.app_cache_size_tv)
    TextView appCacheSizeTv;
    @BindView(R.id.clear_app_cache_rl)
    RelativeLayout clearAppCacheRl;
    @BindView(R.id.logout_btn)
    View logoutBtn;
    @BindView(R.id.donate_rl)
    RelativeLayout donateRl;

    private Unbinder unbind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbind = ButterKnife.bind(this, view);
        initView();
        return view;
    }


    private void initView() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(getContext());
        if (weiBoUserInfo != null) {
            Glide.with(this).asBitmap()
                    .load(weiBoUserInfo.getAvatar_hd())
                    .into(userHeadImg);
            userNameTv.setText(weiBoUserInfo.getName());
            userInfoTv.setText(weiBoUserInfo.getDescription());
            logoutBtn.setVisibility(View.VISIBLE);
        } else {
            userHeadImg.setImageResource(R.mipmap.ic_weibo);
            userNameTv.setText(R.string.weibo_login);
            userInfoTv.setText(R.string.login_info);
            logoutBtn.setVisibility(View.GONE);
        }
        appCacheSizeTv.setText("APP缓存:" + CacheFileUtil.size());
    }

    @OnClick(R.id.user_info_rl)
    public void onUserInfoRlClicked() {
        if (WeiBoUserInfoKeeper.read(getContext()) == null) {
            EventBus.getDefault().post(new StartWeiBoAuthorizeEvent());
        }
    }

    @OnClick(R.id.my_download_rl)
    public void onMyDownloadRlClicked() {
        ToastUtil.showShort("暂未开放");
        //startActivity(new Intent(getContext(), MyDownloadActivity.class));
    }

    @OnClick(R.id.my_collection_rl)
    public void onMyCollectionRlClicked() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(getContext());
        if (weiBoUserInfo != null) {
            startActivity(new Intent(getContext(), MyCollectionActivity.class));
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle("收藏")
                    .setMessage("登录之后才能使用收藏功能哦~")
                    .setPositiveButton("登录", (dialog, which) -> onUserInfoRlClicked())
                    .setCancelable(true)
                    .create();
            alertDialog.show();
        }
    }

    @OnClick(R.id.clear_app_cache_rl)
    public void onClearAppCacheClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setPositiveButton("确定", (dialog, which) -> {
                    boolean clear = CacheFileUtil.clear();
                    ToastUtil.showShort(clear ? "缓存已清除" : "清除缓存失败");
                    initView();
                })
                .setTitle("清除缓存")
                .setMessage("清除缓存会删除所有本地的曲谱哦~")
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    @OnClick(R.id.logout_btn)
    public void onLogoutBtnClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_info)
                .setPositiveButton("退出!", (dialog, witch) -> {
                    AccessTokenKeeper.clear(getContext());
                    WeiBoUserInfoKeeper.clear(getContext());
                    initView();
                })
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOpernFileDeleted(OpernFileDeleteEvent opernFileDeleteEvent) {
        initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnWeiBoAuthorizeSuccess(WeiBoAuthorizeSuccessEvent event) {
        getUserInfoFromWeiBo();
    }

    /**
     * 获取微博用户信息
     */
    public void getUserInfoFromWeiBo() {
        ((BaseActivity) getActivity()).showProgressDialog(true);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getContext());
        HttpCore.getInstance().getApi()
                .getWeiBoUserInfo(accessToken.getToken(), accessToken.getUid())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weiBoUserInfo -> {
                    ((BaseActivity) getActivity()).showProgressDialog(false);
                    LogUtil.i("tag", weiBoUserInfo.toString());
                    WeiBoUserInfoKeeper.write(getContext(), weiBoUserInfo);
                    initView();
                }, throwable -> {
                    ((BaseActivity) getActivity()).showProgressDialog(false);
                    throwable.printStackTrace();
                    ToastUtil.showError(throwable);
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbind.unbind();
    }
}
