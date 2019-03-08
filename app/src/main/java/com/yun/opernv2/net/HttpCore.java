package com.yun.opernv2.net;

import com.yun.opernv2.utils.LogUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yun on 2017/8/10 0010.
 */

public class HttpCore {
    private static final String TAG = HttpCore.class.getSimpleName();
    public static final String BaseUrl = "http://soupu.yuner.fun/";
    private static HttpCore httpCore;
    private static OkHttpClient okHttpClient;
    private static ApiService apiService;

    private HttpCore() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(new LogInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 日志打印拦截器
     */
    private class LogInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            LogUtil.i(TAG, "request:" + request.toString());
            long t1 = System.nanoTime();
            okhttp3.Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            LogUtil.i(TAG, String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            LogUtil.i(TAG, "response body:" + content);
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
    }

    public static HttpCore getInstance() {
        if (httpCore == null) {
            synchronized (HttpCore.class) {
                if (httpCore == null) {
                    httpCore = new HttpCore();
                }
            }
        }
        return httpCore;
    }

    public ApiService getApi() {
        return apiService;
    }
}
