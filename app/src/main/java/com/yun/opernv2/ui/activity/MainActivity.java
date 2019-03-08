package com.yun.opernv2.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yun.opernv2.R;
import com.yun.opernv2.ui.base.BaseActivity;
import com.yun.opernv2.ui.fragment.HomeFragment;
import com.yun.opernv2.ui.fragment.MineFragment;
import com.yun.opernv2.utils.ScreenUtils;
import com.yun.opernv2.utils.ToastUtil;
import com.yun.opernv2.views.ViewPagerFix;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.view_pager)
    ViewPagerFix viewPager;
    @BindView(R.id.home_index)
    TextView homeIndex;
    @BindView(R.id.mine_index)
    TextView mineIndex;
    @BindView(R.id.indicator)
    View indicator;

    private ViewPagerAdapter viewPagerAdapter;


    @Override
    protected int contentViewRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) indicator.getLayoutParams();
                lp.width = ScreenUtils.getScreenWidth() / 2;
                lp.leftMargin = (int) (ScreenUtils.getScreenWidth() / 2 * position + ScreenUtils.getScreenWidth() / 2 * positionOffset);
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
        RxView.clicks(mineIndex)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> viewPager.setCurrentItem(1, true));
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(new HomeFragment());
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

}
