package com.yun.opernv2.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fynn.fluidlayout.FluidLayout;
import com.yun.opernv2.Application;
import com.yun.opernv2.R;
import com.yun.opernv2.db.DBCore;
import com.yun.opernv2.db.SearchHistory;
import com.yun.opernv2.db.SearchHistoryDao;
import com.yun.opernv2.model.ScoreBaseInfoDO;
import com.yun.opernv2.net.HttpCore;
import com.yun.opernv2.ui.base.BaseActivity;
import com.yun.opernv2.utils.DisplayUtil;
import com.yun.opernv2.utils.KeyboardUtils;
import com.yun.opernv2.utils.ToastUtil;
import com.yun.opernv2.views.ActionBarNormal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.NewThreadScheduler;

public class SearchActivity extends BaseActivity {
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.search_input_edt)
    EditText searchInputEdt;
    @BindView(R.id.search_btn)
    ImageView searchBtn;
    @BindView(R.id.opern_lv)
    RecyclerView opernLv;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.search_history)
    FluidLayout searchHistoryView;

    private String searchParameter;
    private ArrayList<ScoreBaseInfoDO> scoreBaseInfoDOArrayList = new ArrayList<>();
    private Adapter adapter;
    private boolean requesting = false;
    private Disposable searchDisposable;

    private String[] searchHistoryBackGroundColor = new String[]{"#f8f2ec", "#f9eaeb", "#f2f2f2", "#f2f6e9",};
    private String[] searchHistoryStockColor = new String[]{"#f7cfac", "#f9b8bd", "#d4d4d4", "#cfdcb5"};

    @Override
    protected int contentViewRes() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        searchInputEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBtn.callOnClick();
            }
            return false;
        });
        searchInputEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    searchHistoryView.setVisibility(View.VISIBLE);
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        opernLv.setLayoutManager(linearLayoutManager);
        opernLv.setItemAnimator(new DefaultItemAnimator());
        adapter = new Adapter(scoreBaseInfoDOArrayList);
        opernLv.setAdapter(adapter);
        searchBtn.setOnClickListener(v -> {
            if (requesting) {
                return;
            }
            searchParameter = searchInputEdt.getText().toString().trim();
            if (searchParameter.equals("")) {
                return;
            }
            net();
        });
        initSearchHistoryView();
    }

    private void initSearchHistoryView() {
        searchHistoryView.removeAllViews();
        int max = 3;
        int min = 0;
        Random random = new Random();
        for (SearchHistory item : getSerchHistory()) {
            int i = random.nextInt(max) % (max - min + 1) + min;
            View view = getLayoutInflater().inflate(R.layout.item_search_history_layout, searchHistoryView, false);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setStroke(DisplayUtil.dp2px(context, 1), Color.parseColor(searchHistoryStockColor[i]));
            gradientDrawable.setColor(Color.parseColor(searchHistoryBackGroundColor[i]));
            gradientDrawable.setCornerRadius(DisplayUtil.dp2px(context, 4));
            view.setBackground(gradientDrawable);
            ((TextView) view.findViewById(R.id.search_history_tv)).setText(item.getSearchParameter());
            view.setOnClickListener(v -> {
                searchParameter = item.getSearchParameter();
                searchInputEdt.setText(item.getSearchParameter());
                searchInputEdt.setSelection(searchInputEdt.getText().length());
                net();
            });
            searchHistoryView.addView(view);
        }
    }

    private List<SearchHistory> getSerchHistory() {
        return DBCore.getInstance(Application.getAppContext()).getSearchHistoryDao().queryBuilder().orderDesc(SearchHistoryDao.Properties.Date).limit(15).build().list();
    }

    private void addSearchStr2History(String searchStr) {
        SearchHistory searchHistory = new SearchHistory(searchStr, new Date());
        DBCore.getInstance(Application.getAppContext()).getSearchHistoryDao().insertOrReplace(searchHistory);
    }

    public void net() {
        searchHistoryView.setVisibility(View.GONE);
        initSearchHistoryView();
        addSearchStr2History(searchParameter);
        KeyboardUtils.hideSoftInput(searchInputEdt);
        requesting = true;
        opernLv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        searchDisposable = HttpCore.getInstance().getApi()
                .scoreBase(searchParameter)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    scoreBaseInfoDOArrayList.clear();
                    ArrayList<ScoreBaseInfoDO> data = arrayListBaseResponse.getData();
                    scoreBaseInfoDOArrayList.addAll(data);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    opernLv.setVisibility(View.VISIBLE);
                    requesting = false;
                }, throwable -> {
                    throwable.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    opernLv.setVisibility(View.VISIBLE);
                    ToastUtil.showError(throwable);
                    requesting = false;
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchDisposable != null) {
            searchDisposable.dispose();
        }
    }

    public class Adapter extends RecyclerView.Adapter<SearchActivity.Adapter.ViewHolder> {
        private ArrayList<ScoreBaseInfoDO> scoreBaseInfoDOArrayList;


        public Adapter(ArrayList<ScoreBaseInfoDO> scoreBaseInfoDOArrayList) {
            this.scoreBaseInfoDOArrayList = scoreBaseInfoDOArrayList;
        }

        @Override
        public SearchActivity.Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_opern_list, parent, false));
        }

        @Override
        public void onBindViewHolder(SearchActivity.Adapter.ViewHolder viewHolder, int position) {
            ScoreBaseInfoDO scoreBaseInfoDO = scoreBaseInfoDOArrayList.get(position);
            Glide.with(context).load(scoreBaseInfoDO.getScoreCoverPicture()).into(viewHolder.opernImg);
            viewHolder.titleTv.setText(scoreBaseInfoDO.getScoreName());
            viewHolder.wordAuthorTv.setText("作词：" + scoreBaseInfoDO.getScoreWordWriter());
            viewHolder.songAuthorTv.setText("作曲：" + scoreBaseInfoDO.getScoreSongWriter());
            viewHolder.dataOriginTv.setText(scoreBaseInfoDO.getScoreOrigin());
            /*StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(scoreBaseInfoDO.getCategoryOne());
            if (!scoreBaseInfoDO.getCategoryTwo().equals("")) {
                stringBuilder.append("/");
                stringBuilder.append(scoreBaseInfoDO.getCategoryTwo());
            }
            if (!scoreBaseInfoDO.getCategoryThree().equals("")) {
                stringBuilder.append("/");
                stringBuilder.append(scoreBaseInfoDO.getCategoryThree());
            }*/
        }

        @Override
        public int getItemCount() {
            return scoreBaseInfoDOArrayList.size();
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
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchActivity.this, ShowImageActivity.class);
                    intent.putExtra("scoreBaseInfoDO", scoreBaseInfoDOArrayList.get(getAdapterPosition()));
                    startActivity(intent);
                });
            }
        }
    }
}
