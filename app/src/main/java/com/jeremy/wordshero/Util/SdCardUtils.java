package com.jeremy.wordshero.Util;

import android.os.Environment;

import java.io.File;

/**
 * sd卡的工具类，用于创建文件目录，存储pdf等文件
 * @author cyd
 * @data 2019/1/19
 */
public class SdCardUtils {

    public static final String FILE_DIR = "/CrossWords";
    public static final String PDF_DIR = "/pdf";


    public static boolean checkSdCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sd卡可用
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取sd卡的文件路径
     * @return
     */
    public static String getSdPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    }

    /**
     * 创建目录
     */
    public static void createFileDir(String fileDir) {
        String path = getSdPath() + fileDir;
        File filePath = new File(path);
        if(!filePath.exists()) {
            filePath.mkdirs();
        }
        LogUtil.d("dir is create","i am run"+path );
    }


}
