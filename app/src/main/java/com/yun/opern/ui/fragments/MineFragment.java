package com.yun.opern.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.bugly.beta.Beta;
import com.yun.opern.R;
import com.yun.opern.common.WeiBoConstants;
import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.common.WeiBoUserInfoKeeper;
import com.yun.opern.model.UserLoginRequestInfo;
import com.yun.opern.model.event.OpernFileDeleteEvent;
import com.yun.opern.model.event.UserLoginOrLogoutEvent;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.activitys.AboutUsActivity;
import com.yun.opern.ui.activitys.DonateActivity;
import com.yun.opern.ui.activitys.MyCollectionActivity;
import com.yun.opern.ui.activitys.MyDownloadActivity;
import com.yun.opern.ui.activitys.TellUsActivity;
import com.yun.opern.utils.CacheFileUtil;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.views.ActionBarNormal;
import com.yun.opern.views.SmallRedPoint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MineFragment extends Fragment {

    @BindView(R.id.my_download_rl)
    RelativeLayout myDownloadRl;
    @BindView(R.id.my_collection_rl)
    RelativeLayout myCollectionRl;
    @BindView(R.id.tell_us_rl)
    RelativeLayout tellUsRl;
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

    private SsoHandler mSsoHandler;
    private Unbinder unbind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        WbSdk.install(getContext(), new AuthInfo(getContext(), WeiBoConstants.APP_KEY, WeiBoConstants.REDIRECT_URL, WeiBoConstants.SCOPE));
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbind = ButterKnife.bind(this, view);
        initView();
        return view;
    }


    private void initView() {
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getContext());
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(getContext());
        if (accessToken.isSessionValid() && weiBoUserInfo != null) {
            Glide.with(getContext()).asBitmap().load(weiBoUserInfo.getAvatar_hd()).transition(withCrossFade()).into(userHeadImg);
            userNameTv.setText(weiBoUserInfo.getScreen_name());
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
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getContext());
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(getContext());
        if (!accessToken.isSessionValid() || weiBoUserInfo == null) {
            mSsoHandler = new SsoHandler(getActivity());
            mSsoHandler.authorize(new SelfWbAuthListener());
        }
    }

    @OnClick(R.id.my_download_rl)
    public void onMyDownloadRlClicked() {
        startActivity(new Intent(getContext(), MyDownloadActivity.class));
    }

    @OnClick(R.id.my_collection_rl)
    public void onMyCollectionRlClicked() {
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getContext());
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(getContext());
        if (accessToken.isSessionValid() && weiBoUserInfo != null) {
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
                    ErrorMessageUtil.showErrorByToast(clear ? "缓存已清除" : "清除缓存失败");
                    initView();
                })
                .setTitle("清除缓存")
                .setMessage("清除缓存会删除所有本地的曲谱哦~")
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    @OnClick(R.id.tell_us_rl)
    public void onTellUsRlClicked() {
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getContext());
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(getContext());
        if (accessToken.isSessionValid() && weiBoUserInfo != null) {
            startActivity(new Intent(getContext(), TellUsActivity.class));
        } else {
            //showDialog("告诉我们", "登录之后才能使用该功能哦~", "登录", (dialog, which) -> onUserInfoRlClicked());
        }
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
                    EventBus.getDefault().post(new UserLoginOrLogoutEvent(false));
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

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            getActivity().runOnUiThread(() -> {
                Oauth2AccessToken mAccessToken = token;
                if (mAccessToken.isSessionValid()) {
                    AccessTokenKeeper.writeAccessToken(getContext(), mAccessToken);
                    getUserInfoFromWeiBo();
                }
            });
        }

        @Override
        public void cancel() {
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(getContext(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取微博用户信息
     */
    public void getUserInfoFromWeiBo() {
        //showProgressDialog(true);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getContext());
        HttpCore.getInstance().getApi()
                .getWeiBoUserInfo(accessToken.getToken(), accessToken.getUid())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weiBoUserInfo -> {
                    WeiBoUserInfoKeeper.write(getContext(), weiBoUserInfo);
                    login();
                    initView();
                    EventBus.getDefault().post(new UserLoginOrLogoutEvent(true));
                    //showProgressDialog(false);
                }, throwable -> {
                    throwable.printStackTrace();
                    ErrorMessageUtil.showErrorByToast(throwable);
                    //showProgressDialog(false);
                });
    }

    public void login() {
        UserLoginRequestInfo userLoginRequestInfo = new UserLoginRequestInfo();
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(getContext());
        userLoginRequestInfo.setUserId(weiBoUserInfo.getIdstr());
        userLoginRequestInfo.setUserName(weiBoUserInfo.getScreen_name());
        userLoginRequestInfo.setUserGender(weiBoUserInfo.getGender());
        HttpCore.getInstance().getApi()
                .userLogin(userLoginRequestInfo)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbind.unbind();
    }
}
