package com.yun.opernv2.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;
import com.yun.opernv2.R;
import com.yun.opernv2.model.OpernInfo;
import com.yun.opernv2.net.HttpCore;
import com.yun.opernv2.net.request.GetRandomOpernReq;
import com.yun.opernv2.ui.activity.ShowImageActivity;
import com.yun.opernv2.ui.activity.WebViewActivity;
import com.yun.opernv2.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;


public class HomeFragment extends Fragment {
    @BindView(R.id.opern_lv)
    RecyclerView opernLv;
    @BindView(R.id.opern_srl)
    SwipeRefreshLayout opernSrl;
    @BindView(R.id.empty_view)
    View emptyView;

    private ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int index = 0;
    private boolean requesting = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        opernLv.setLayoutManager(linearLayoutManager);
        opernLv.setItemAnimator(new DefaultItemAnimator());
        adapter = new Adapter(opernInfoArrayList);
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.header_home_rv, null);
        adapter.addHeaderView(headerView);
        opernLv.setAdapter(adapter);
        opernLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    int totalItemCount = opernInfoArrayList.size();
                    if (lastVisibleItem >= totalItemCount - 10) {
                        if (!requesting) {
                            net();
                        }
                    }
                }
            }
        });
        opernSrl.setColorSchemeColors(getResources().getColor(R.color.black));
        opernSrl.setOnRefreshListener(() -> {
            index = 0;
            net();
        });
        net();
    }

    private void net() {
        requesting = true;
        opernSrl.setRefreshing(true);
        GetRandomOpernReq req = new GetRandomOpernReq();
        req.setPageSize(40);
        HttpCore.getInstance().getApi()
                .getRandomOpernInfo(req)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    if (index == 0) {
                        opernInfoArrayList.clear();
                    }
                    ArrayList<OpernInfo> data = arrayListBaseResponse.getData();
                    if (data == null || data.size() == 0) {
                        ToastUtil.showShort("没有更多数据了");
                    } else {
                        opernInfoArrayList.addAll(data);
                        index++;
                    }
                    adapter.notifyDataSetChanged();
                    opernSrl.setRefreshing(false);
                    requesting = false;
                }, throwable -> {
                    throwable.printStackTrace();
                    opernSrl.setRefreshing(false);
                    requesting = false;
                    ToastUtil.showError(throwable);
                });
    }


    public class BannerImageLoader extends ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }

        @Override
        public ImageView createImageView(Context context) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }
    }


    public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_NORMAL = 1;
        private ArrayList<OpernInfo> opernInfoArrayList;
        private View headerView;
        private HeaderViewViewHolder headerViewViewHolder;

        public HeaderViewViewHolder getHeaderViewViewHolder() {
            return headerViewViewHolder;
        }

        public void addHeaderView(View headerView) {
            this.headerView = headerView;
            notifyItemInserted(0);
        }

        public View getHeaderView() {
            return headerView;
        }

        public int getRealPosition(RecyclerView.ViewHolder holder) {
            int position = holder.getLayoutPosition();
            return headerView == null ? position : position - 1;
        }

        public int getRealPosition(int position) {
            return headerView == null ? position : position - 1;
        }


        public Adapter(ArrayList<OpernInfo> opernInfoArrayList) {
            this.opernInfoArrayList = opernInfoArrayList;
        }

        @Override
        public int getItemViewType(int position) {
            if (headerView == null) return TYPE_NORMAL;
            if (position == 0) return TYPE_HEADER;
            return TYPE_NORMAL;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return headerViewViewHolder = new HeaderViewViewHolder(headerView);
            }
            return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_opern_list, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (getItemViewType(position) == TYPE_HEADER) {
                HeaderViewViewHolder holder = (HeaderViewViewHolder) viewHolder;
                holder.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
                holder.banner.setImageLoader(new BannerImageLoader());
                ArrayList<String> images = new ArrayList<>();
                images.add(HttpCore.BaseUrl + "resources/image/ic_my_website.png");
                holder.banner.setImages(images);
                holder.banner.setBannerAnimation(Transformer.Default);
                holder.banner.isAutoPlay(true);
                holder.banner.setDelayTime(4000);
                holder.banner.setIndicatorGravity(BannerConfig.CENTER);
                holder.banner.start();
                holder.banner.setOnBannerListener(position1 -> {
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    switch (position1) {
                        case 0:
                            intent.putExtra("url", "http://60.205.182.130:8080/OpernServer/");
                            break;
                    }
                    startActivity(intent);
                });
            } else {
                ViewHolder holder = (ViewHolder) viewHolder;
                OpernInfo opernInfo = opernInfoArrayList.get(getRealPosition(position));
                Glide.with(getContext()).load(opernInfo.getOpernFirstPicUrl()).into(holder.opernImg);
                holder.titleTv.setText(opernInfo.getOpernName());
                holder.wordAuthorTv.setText("作词：" + opernInfo.getOpernWordAuthor());
                holder.songAuthorTv.setText("作曲：" + opernInfo.getOpernSongAuthor());
                holder.dataOriginTv.setText(opernInfo.getOriginName());

            }
        }

        @Override
        public int getItemCount() {
            emptyView.setVisibility(opernInfoArrayList.size() == 0 ? View.VISIBLE : View.GONE);
            return headerView == null ? opernInfoArrayList.size() : opernInfoArrayList.size() + 1;
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_opern_list_img)
            ImageView opernImg;
            @BindView(R.id.item_opern_list_title_tv)
            TextView titleTv;
            @BindView(R.id.item_opern_list_wordAuthor_tv)
            TextView wordAuthorTv;
            @BindView(R.id.item_opern_list_songAuthor_tv)
            TextView songAuthorTv;
            @BindView(R.id.item_opern_list_data_origin_tv)
            TextView dataOriginTv;
            @BindView(R.id.item_opern_list_data_category_tv)
            TextView categoryTv;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                RxView.clicks(itemView).subscribe(o -> {
                    Intent intent = new Intent(getActivity(), ShowImageActivity.class);
                    intent.putExtra("opernInfo", opernInfoArrayList.get(getRealPosition(this)));
                    startActivity(intent);
                });
            }
        }

        class HeaderViewViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.banner)
            Banner banner;
            @BindView(R.id.lastUpdateCardView)
            CardView lastUpdateCardView;
            @BindView(R.id.lastUpdateTimeTv)
            TextView lastUpdateTimeTv;
            @BindView(R.id.musicChart_cardview)
            CardView musicChartCardView;

            public HeaderViewViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                RxView.clicks(lastUpdateCardView).subscribe(o -> ToastUtil.showShort("暂未开放"));
                RxView.clicks(musicChartCardView).subscribe(o -> ToastUtil.showShort("暂未开放"));
            }
        }
    }

}
