package com.yun.opernv2.net;


import com.yun.opernv2.model.BaseResponse;
import com.yun.opernv2.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yun on 2017/8/30 0030.
 */

public class CommonCallback<V> implements Callback<V> {

    @SuppressWarnings("all")
    @Override
    public void onResponse(Call<V> call, Response<V> response) {
        if (response.body() instanceof BaseResponse) {
            if (((BaseResponse) response.body()).getData() == null) {
                ((BaseResponse) response.body()).setData((V) new Object());
            }
        }
    }

    @Override
    public void onFailure(Call<V> call, Throwable t) {
        t.printStackTrace();
        ToastUtil.showError(t);
    }
}
