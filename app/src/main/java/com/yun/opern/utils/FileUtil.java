package com.yun.opern.utils;

import com.yun.opern.model.OpernInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Yun on 2017/8/12 0012.
 */

public class FileUtil {

    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    /**
     * 判断曲谱是否存在本地
     *
     * @param opernInfo
     * @return
     */
    public static boolean isOpernImgsExist(OpernInfo opernInfo) {
        File file = new File(CacheFileUtil.cacheFilePath + "/" + opernInfo.getOpernName());
        return file.exists();
    }

    /**
     * 删除本地曲谱图片
     *
     * @param opernInfo
     * @return
     */
    public static boolean deleteLocalOpernImgs(OpernInfo opernInfo) {
        try {
            File file = new File(CacheFileUtil.cacheFilePath + "/" + opernInfo.getOpernName());
            if (file.exists()) {
                for (File childFile : file.listFiles()) {
                    childFile.delete();
                }
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
