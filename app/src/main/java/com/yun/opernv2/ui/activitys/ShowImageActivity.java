package com.yun.opernv2.ui.activitys;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.yun.opernv2.R;
import com.yun.opernv2.common.WeiBoUserInfo;
import com.yun.opernv2.common.WeiBoUserInfoKeeper;
import com.yun.opernv2.model.OpernInfo;
import com.yun.opernv2.model.OpernPicInfo;
import com.yun.opernv2.net.HttpCore;
import com.yun.opernv2.net.request.AddCollectionReq;
import com.yun.opernv2.net.request.IsCollectedReq;
import com.yun.opernv2.net.request.RemoveCollectionReq;
import com.yun.opernv2.ui.bases.BaseActivity;
import com.yun.opernv2.ui.fragments.ShowImageFragment;
import com.yun.opernv2.utils.FileUtil;
import com.yun.opernv2.utils.ImageDownloadUtil;
import com.yun.opernv2.utils.ToastUtil;
import com.yun.opernv2.views.ActionBarNormal;
import com.yun.opernv2.views.ViewPagerFix;

import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

public class ShowImageActivity extends BaseActivity {

    @BindView(R.id.image_vp)
    ViewPagerFix imageVp;
    @BindView(R.id.download_fab)
    FloatingActionButton downloadFab;
    @BindView(R.id.collection_fab)
    FloatingActionButton collectionFab;
    @BindView(R.id.fab_btns)
    LinearLayout fabBtns;
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;

    private OpernInfo opernInfo;
    private ViewPagerAdapter adapter;

    private boolean isCollected = false;
    private ImageDownloadUtil imageDownloadUtil;

    public ActionBarNormal getActionbar() {
        return actionbar;
    }

    public LinearLayout getFabBtns() {
        return fabBtns;
    }

    @Override
    protected int contentViewRes() {
        opernInfo = (OpernInfo) getIntent().getExtras().get("opernInfo");
        return R.layout.activity_show_image;
    }

    @Override
    protected void initView() {
        actionbar.setTitle(opernInfo.getOpernName());
        ArrayList<ShowImageFragment> fragments = new ArrayList<>();
        for (OpernPicInfo opernPicInfo : opernInfo.getOpernPicInfoList()) {
            ShowImageFragment showImageFragment = new ShowImageFragment();
            showImageFragment.setAnimView(actionbar, fabBtns);
            Bundle bundle = new Bundle();
            bundle.putSerializable("opernPicInfo", opernPicInfo);
            showImageFragment.setArguments(bundle);
            fragments.add(showImageFragment);
        }
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
        downloadFab.setOnClickListener((view) -> {
            ToastUtil.showShort("暂未开放");
                    /*imageDownloadUtil = new ImageDownloadUtil(opernInfo, new ImageDownloadUtil.CallBack() {
                        @Override
                        public void success() {
                            if (downloadFab != null) {
                                downloadFab.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void fail() {
                            ErrorMessageUtil.showErrorByToast("下载失败，请重试");
                        }
                    });
                    imageDownloadUtil.start();*/
                }
        );
        collectionFab.setOnClickListener((view) -> {
                    Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
                    WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
                    if (!accessToken.isSessionValid() || weiBoUserInfo == null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setTitle("收藏")
                                .setMessage("登录之后才能使用收藏功能哦~")
                                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                                .setCancelable(true)
                                .create();
                        alertDialog.show();
                    } else {
                        if (isCollected) {
                            removeCollect();
                        } else {
                            addCollection();
                        }

                    }
                }
        );
        downloadFab.setVisibility(FileUtil.isOpernImgsExist(opernInfo) ? View.GONE : View.VISIBLE);
        isCollected();
    }

    /**
     * 是否收藏
     */
    public void isCollected() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        if (weiBoUserInfo == null) {
            return;
        }
        IsCollectedReq request = new IsCollectedReq();
        request.setOpernId(opernInfo.getId());
        request.setUserId(weiBoUserInfo.getId());
        HttpCore.getInstance().getApi().isCollected(request)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    if (baseResponse.isSuccess()) {
                        isCollected = baseResponse.getData();
                    } else {
                        isCollected = false;
                    }
                    changeCollectIcon();
                }, Throwable::printStackTrace);
    }

    /**
     * 收藏
     */
    public void addCollection() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        AddCollectionReq request = new AddCollectionReq();
        request.setUserId(weiBoUserInfo.getId());
        request.setOpernId(opernInfo.getId());
        HttpCore.getInstance().getApi()
                .addCollection(request)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                            if (baseResponse.isSuccess()) {
                                isCollected = true;
                                changeCollectIcon();
                                ToastUtil.showShort(baseResponse.getMessage());
                            }
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            ToastUtil.showError(throwable);
                        }
                );
    }

    /**
     * 取消收藏
     */
    public void removeCollect() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        RemoveCollectionReq request = new RemoveCollectionReq();
        request.setUserId(weiBoUserInfo.getId());
        request.setOpernId(opernInfo.getId());
        HttpCore.getInstance().getApi()
                .removeCollection(request)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    if (baseResponse.isSuccess()) {
                        isCollected = false;
                        changeCollectIcon();
                        ToastUtil.showShort(baseResponse.getMessage());
                    }
                }, Throwable::printStackTrace);
    }

    public void changeCollectIcon() {
        collectionFab.setImageResource(isCollected ? R.mipmap.ic_collected : R.mipmap.ic_collection);
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
            return opernInfo.getOpernPicInfoList().size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageDownloadUtil != null) {
            imageDownloadUtil.cancel();
        }
    }
}
