package com.yun.opernv2.ui.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.opernv2.R;
import com.yun.opernv2.common.WeiBoUserInfo;
import com.yun.opernv2.common.WeiBoUserInfoKeeper;
import com.yun.opernv2.model.OpernInfo;
import com.yun.opernv2.net.HttpCore;
import com.yun.opernv2.net.request.GetCollectionReq;
import com.yun.opernv2.net.request.RemoveCollectionReq;
import com.yun.opernv2.ui.base.BaseActivity;
import com.yun.opernv2.utils.ToastUtil;
import com.yun.opernv2.views.ActionBarNormal;
import com.yun.opernv2.views.SquareImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MyCollectionActivity extends BaseActivity {

    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.img_gv)
    GridView imgGv;
    @BindView(R.id.empty_view)
    View emptyView;

    private GridViewAdapter adapter;
    private ArrayList<OpernInfo> opernInfos = new ArrayList<>();

    @Override
    protected int contentViewRes() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected void initView() {
        ToastUtil.showShort("长按可取消收藏");
        adapter = new GridViewAdapter(opernInfos);
        imgGv.setAdapter(adapter);
        imgGv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(context, ShowImageActivity.class);
            intent.putExtra("opernInfo", opernInfos.get(position));
            startActivity(intent);
        });
        imgGv.setOnItemLongClickListener((parent, view, position, id) -> {
            removeCollect(position);
            return true;
        });
        getCollectedOpernInfo();
    }

    private void getCollectedOpernInfo() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        if (weiBoUserInfo == null) {
            return;
        }
        showProgressDialog(true);
        GetCollectionReq request = new GetCollectionReq();
        request.setUserId(weiBoUserInfo.getId());
        HttpCore.getInstance().getApi()
                .getCollectionOpernInfo(request)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    opernInfos.addAll(arrayListBaseResponse.getData());
                    adapter.notifyDataSetChanged();
                    showProgressDialog(false);
                }, throwable -> {
                    throwable.printStackTrace();
                    ToastUtil.showError(throwable);
                    showProgressDialog(false);
                });
    }

    /**
     * 取消收藏
     */
    public void removeCollect(int position) {
        showProgressDialog(true);
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        RemoveCollectionReq request = new RemoveCollectionReq();
        request.setUserId(weiBoUserInfo.getId());
        request.setOpernId(opernInfos.get(position).getId());
        HttpCore.getInstance().getApi()
                .removeCollection(request)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                            if (baseResponse.isSuccess()) {
                                opernInfos.remove(position);
                                adapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.showShort(baseResponse.getMessage());
                            }
                            showProgressDialog(false);
                        }, throwable -> {
                    ToastUtil.showError(throwable);
                            showProgressDialog(false);
                        }
                );
    }

    public class GridViewAdapter extends BaseAdapter {
        private ArrayList<OpernInfo> opernInfos;
        private ViewHolder viewHolder;

        public GridViewAdapter(ArrayList<OpernInfo> opernInfos) {
            this.opernInfos = opernInfos;
        }

        @Override
        public int getCount() {
            emptyView.setVisibility(opernInfos.size() == 0 ? View.VISIBLE : View.GONE);
            return opernInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return opernInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_img_gv_layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final OpernInfo opernInfo = opernInfos.get(position);
            viewHolder.itemImgGvLayoutTv.setText(opernInfo.getOpernName());
            Glide.with(context).asBitmap().load(opernInfo.getOpernFirstPicUrl()).transition(withCrossFade()).into(viewHolder.itemImgGvLayoutImg);
            return convertView;
        }


        public class ViewHolder {
            @BindView(R.id.item_img_gv_layout_img)
            SquareImageView itemImgGvLayoutImg;
            @BindView(R.id.item_img_gv_layout_tv)
            TextView itemImgGvLayoutTv;
            @BindView(R.id.item_img_gv_delete_img)
            ImageView deleteImg;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
