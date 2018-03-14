package com.yun.opernv2.net;

import com.yun.opernv2.utils.LogUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yun on 2017/8/10 0010.
 */

public class HttpCore {
    private static final String TAG = HttpCore.class.getSimpleName();
    public static final String BaseUrl = "http://60.205.182.130:8080/OpernServer/";
    //public static final String BaseUrl = "http://192.168.0.109:8080/OpernServer/";
    private static HttpCore httpCore;
    private static OkHttpClient okHttpClient;
    private static ApiService apiService;

    private HttpCore() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.0.109", 8888)))
                .addNetworkInterceptor(new HeaderInterceptor())
                //.addNetworkInterceptor(new LogInterceptor())
                .addNetworkInterceptor(new SleepInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build();
            return chain.proceed(request);
        }
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

    /**
     * 平滑请求时间拦截器
     */
    private class SleepInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            long start = System.currentTimeMillis();
            Response response = chain.proceed(chain.request());
            if (System.currentTimeMillis() - start < 1000) {
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
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

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public ApiService getApi() {
        return apiService;
    }
}
