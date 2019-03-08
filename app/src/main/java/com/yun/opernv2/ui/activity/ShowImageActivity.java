package com.yun.opernv2.ui.activity;

import android.os.Bundle;

import com.yun.opernv2.R;
import com.yun.opernv2.model.ScoreBaseInfoDO;
import com.yun.opernv2.net.HttpCore;
import com.yun.opernv2.ui.base.BaseActivity;
import com.yun.opernv2.ui.fragment.ShowImageFragment;
import com.yun.opernv2.utils.ToastUtil;
import com.yun.opernv2.views.ActionBarNormal;
import com.yun.opernv2.views.ViewPagerFix;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

public class ShowImageActivity extends BaseActivity {

    @BindView(R.id.image_vp)
    ViewPagerFix imageVp;
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;

    private ScoreBaseInfoDO scoreBaseInfoDO;
    private ViewPagerAdapter adapter;
    private ArrayList<ShowImageFragment> fragments;


    @Override
    protected int contentViewRes() {
        scoreBaseInfoDO = (ScoreBaseInfoDO) getIntent().getExtras().get("scoreBaseInfoDO");
        return R.layout.activity_show_image;
    }

    @Override
    protected void initView() {
        actionbar.setTitle(scoreBaseInfoDO.getScoreName());
        fragments = new ArrayList<>();
        for (int i = 0; i < scoreBaseInfoDO.getScorePictureCount(); i++) {
            ShowImageFragment showImageFragment = new ShowImageFragment();
            fragments.add(showImageFragment);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("pictureHref", scoreBaseInfoDO.getScoreCoverPicture());
        fragments.get(0).setArguments(bundle);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        imageVp.setAdapter(adapter);
        imageVp.setOffscreenPageLimit(fragments.size() - 1);
        imageVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (scoreBaseInfoDO.getScorePictureCount() != 1) {
            scorePicture();
        }
    }

    public void scorePicture() {
        HttpCore.getInstance().getApi()
                .scorePicture(scoreBaseInfoDO.getScoreId())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                            for (int i = 1; i < baseResponse.getData().size(); i++) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("pictureHref", baseResponse.getData().get(i).getScorePictureHref());
                                fragments.get(i).setArguments(bundle);
                            }
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            ToastUtil.showError(throwable);
                        }
                );
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<ShowImageFragment> fragments;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<ShowImageFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return scoreBaseInfoDO.getScorePictureCount();
        }
    }
}
