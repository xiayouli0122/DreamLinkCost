package com.yuri.dreamlinkcost.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Admin on 2016/4/28.
 */
public class FileUtils {

    /**
     * SD卡是否可用.
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else
            return false;
    }


    /**
     *  Music存储地址
     * @return
     */
    public static String getMusicDirPath(){
        return  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "Dilemu";
    }


}
