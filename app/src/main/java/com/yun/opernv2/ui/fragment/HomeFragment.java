package com.yun.opernv2.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yun.opernv2.R;
import com.yun.opernv2.model.ScoreBaseInfoDO;
import com.yun.opernv2.net.HttpCore;
import com.yun.opernv2.ui.activity.ShowImageActivity;
import com.yun.opernv2.utils.ToastUtil;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

    private ArrayList<ScoreBaseInfoDO> scoreBaseInfoDOArrayList = new ArrayList<>();
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int page = 0;
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
        adapter = new Adapter(scoreBaseInfoDOArrayList);
        opernLv.setAdapter(adapter);
        opernLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    int totalItemCount = scoreBaseInfoDOArrayList.size();
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
            page = 0;
            net();
        });
        net();
    }

    private void net() {
        requesting = true;
        opernSrl.setRefreshing(true);

        HttpCore.getInstance().getApi()
                .homeRecommend(page)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    if (page == 0) {
                        scoreBaseInfoDOArrayList.clear();
                    }
                    ArrayList<ScoreBaseInfoDO> data = arrayListBaseResponse.getData();
                    if (data == null || data.size() == 0) {
                        ToastUtil.showShort("没有更多数据了");
                    } else {
                        scoreBaseInfoDOArrayList.addAll(data);
                        page++;
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

    public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ScoreBaseInfoDO> scoreBaseInfoDOArrayList;

        public Adapter(ArrayList<ScoreBaseInfoDO> scoreBaseInfoDOArrayList) {
            this.scoreBaseInfoDOArrayList = scoreBaseInfoDOArrayList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_opern_list, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ViewHolder holder = (ViewHolder) viewHolder;
            ScoreBaseInfoDO scoreBaseInfoDO = scoreBaseInfoDOArrayList.get(position);
            Glide.with(getContext()).load(scoreBaseInfoDO.getScoreCoverPicture()).into(holder.opernImg);
            holder.titleTv.setText(scoreBaseInfoDO.getScoreName());
            holder.wordAuthorTv.setText("作词：" + scoreBaseInfoDO.getScoreWordWriter());
            holder.songAuthorTv.setText("作曲：" + scoreBaseInfoDO.getScoreSongWriter());
            holder.dataOriginTv.setText(scoreBaseInfoDO.getScoreOrigin());
            holder.categoryTv.setText(scoreBaseInfoDO.getScoreFormat());
        }

        @Override
        public int getItemCount() {
            emptyView.setVisibility(scoreBaseInfoDOArrayList.size() == 0 ? View.VISIBLE : View.GONE);
            return scoreBaseInfoDOArrayList == null ? 0 : scoreBaseInfoDOArrayList.size();
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
                    intent.putExtra("scoreBaseInfoDO", scoreBaseInfoDOArrayList.get(getAdapterPosition()));
                    startActivity(intent);
                });
            }
        }

    }

}
