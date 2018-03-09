package com.yun.opernv2.ui.activitys;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.yun.opernv2.R;
import com.yun.opernv2.common.WeiBoConstants;
import com.yun.opernv2.common.WeiBoUserInfo;
import com.yun.opernv2.common.WeiBoUserInfoKeeper;
import com.yun.opernv2.model.event.StartWeiBoAuthorizeEvent;
import com.yun.opernv2.model.event.WeiBoAuthorizeSuccessEvent;
import com.yun.opernv2.ui.bases.BaseActivity;
import com.yun.opernv2.ui.fragments.CategoryFragment;
import com.yun.opernv2.ui.fragments.HomeFragment;
import com.yun.opernv2.ui.fragments.MineFragment;
import com.yun.opernv2.utils.ScreenUtils;
import com.yun.opernv2.utils.ToastUtil;
import com.yun.opernv2.views.ViewPagerFix;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.view_pager)
    ViewPagerFix viewPager;
    @BindView(R.id.home_index)
    TextView homeIndex;
    @BindView(R.id.category_index)
    TextView categoryIndex;
    @BindView(R.id.mine_index)
    TextView mineIndex;
    @BindView(R.id.indicator)
    View indicator;

    private ViewPagerAdapter viewPagerAdapter;

    private SsoHandler mSsoHandler;

    @Override
    protected int contentViewRes() {
        EventBus.getDefault().register(this);
        WbSdk.install(context, new AuthInfo(context, WeiBoConstants.APP_KEY, WeiBoConstants.REDIRECT_URL, WeiBoConstants.SCOPE));
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) indicator.getLayoutParams();
                lp.width = ScreenUtils.getScreenWidth() / 3;
                lp.leftMargin = (int) (ScreenUtils.getScreenWidth() / 3 * position + ScreenUtils.getScreenWidth() / 3 * positionOffset);
                indicator.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        edtSearch.setOnClickListener(v -> startActivity(new Intent(context, SearchActivity.class)));
    }

    @Override
    protected void initedView() {
        super.initedView();
        RxView.clicks(homeIndex)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> viewPager.setCurrentItem(0, true));
        RxView.clicks(categoryIndex)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> viewPager.setCurrentItem(1, true));
        RxView.clicks(mineIndex)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> viewPager.setCurrentItem(2, true));
    }

    private void weiboAuthorize() {
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        if (!accessToken.isSessionValid() || weiBoUserInfo == null) {
            mSsoHandler = new SsoHandler(this);
            mSsoHandler.authorize(new SelfWbAuthListener());
        }
    }

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            if (token.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(context, token);
                EventBus.getDefault().post(new WeiBoAuthorizeSuccessEvent());
            }
        }

        @Override
        public void cancel() {
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(context, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(new HomeFragment());
            fragments.add(new CategoryFragment());
            fragments.add(new MineFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startWeiBoAuthorize(StartWeiBoAuthorizeEvent event) {
        weiboAuthorize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private long currentTime = 0;

    /**
     * 双击退出整个应用(间隔3s)，MainActivity launchMode 设置为singleTop
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() / 1000 - currentTime < 3) {
            finish();
            System.exit(0);
        } else {
            ToastUtil.showShort("再次点击退出应用");
            currentTime = System.currentTimeMillis() / 1000;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
