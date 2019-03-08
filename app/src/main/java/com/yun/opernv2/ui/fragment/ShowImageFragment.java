package com.yun.opernv2.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.yun.opernv2.R;
import com.yun.opernv2.utils.ToastUtil;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ShowImageFragment extends Fragment {
    @BindView(R.id.photo_view)
    PhotoView photoView;
    Unbinder unbinder;
    @BindView(R.id.loading_pb)
    ProgressBar loadingPb;
    private String pictureHref;
    private int retry = 3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_image, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null && getArguments().containsKey("pictureHref")) {
            pictureHref = getArguments().getString("pictureHref");
            initView();
        }
        return view;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null && args.containsKey("pictureHref")) {
            pictureHref = args.getString("pictureHref");
            initView();
        }
    }

    private void initView() {
        if (unbinder == null) {
            return;
        }
        Glide.with(this).asBitmap().load(pictureHref).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                if (retry > 0) {
                    initView();
                    ToastUtil.showShort("加载图片失败，正在重新加载");
                } else {
                    ToastUtil.showShort("加载图片失败，请重试");
                    getActivity().finish();
                }
                retry--;
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                loadingPb.setVisibility(View.GONE);
                return false;
            }
        }).transition(withCrossFade()).into(photoView);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
