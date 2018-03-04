package com.yun.opern.ui.activitys;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yun.opern.R;
import com.yun.opern.model.event.ReceiveMessageFromJPushEvent;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.ui.fragments.CategoryFragment;
import com.yun.opern.ui.fragments.HomeFragment;
import com.yun.opern.ui.fragments.MineFragment;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.utils.ScreenUtils;
import com.yun.opern.views.ViewPagerFix;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

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
    private int searchFabInitBottomMargin;

    @Override
    protected int contentViewRes() {
        EventBus.getDefault().register(this);
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
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
    public void onReceiveMessageFromJPush(ReceiveMessageFromJPushEvent receiveMessageFromJPushEvent) {
        showDialog("开发者消息", receiveMessageFromJPushEvent.getMessage(), "嗯,知道了", (dialog, which) -> {
        });
    }


    private long currentTime = 0;

    /**
     * 双击退出整个应用(间隔3s)，MainActivity launchMode 设置为singleTop
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() / 1000 - currentTime < 3) {
            finish(); //结束当前activity
            System.exit(0); //系统退出
        } else {
            ErrorMessageUtil.showErrorByToast("再次点击退出应用");
            currentTime = System.currentTimeMillis() / 1000;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
