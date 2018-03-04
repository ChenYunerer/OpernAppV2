package com.yun.opern.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yun.opern.model.OpernPicInfo;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Yun on 2017/8/12 0012.
 */

public class ImageDownloadUtil extends Thread {
    private boolean cancel = false;
    private OpernInfo opernInfo;
    private CountDownLatch countDownLatch;
    private CallBack callBack;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (cancel) {
                return;
            }
            if (callBack == null) {
                return;
            }
            if (msg.what == 0) {
                callBack.success();
            } else {
                callBack.fail();
            }
        }
    };

    public void cancel() {
        cancel = true;
        callBack = null;
    }

    public interface CallBack {
        void success();

        void fail();
    }

    public ImageDownloadUtil(OpernInfo opernInfo, CallBack callBack) {
        this.opernInfo = opernInfo;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        super.run();
        File dir = new File(CacheFileUtil.cacheFilePath + "/" + opernInfo.getOpernName());
        if (!dir.exists()) {
            dir.mkdir();
        }
        countDownLatch = new CountDownLatch(opernInfo.getOpernPicInfoList().size());
        for (OpernPicInfo opernPicInfo : opernInfo.getOpernPicInfoList()) {
            new DownloadThread(opernPicInfo).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(1);
        }
        handler.sendEmptyMessage(0);
    }

    private class DownloadThread extends Thread {
        private OpernPicInfo opernPicInfo;

        public DownloadThread(OpernPicInfo opernPicInfo) {
            this.opernPicInfo = opernPicInfo;
        }

        @Override
        public void run() {
            super.run();
            Request request = new Request.Builder()
                    .get()
                    .url(opernPicInfo.getOpernPicUrl())
                    .build();
            try {
                Response response = HttpCore.getInstance().getOkHttpClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    InputStream inputStream = response.body().byteStream();
                    File file = new File(CacheFileUtil.cacheFilePath + "/" + opernInfo.getOpernName() + "/" + opernPicInfo.getOpernId() + ".jpg");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(FileUtil.readInputStream(inputStream));
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}
