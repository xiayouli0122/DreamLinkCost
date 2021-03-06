package com.yuri.dreamlinkcost.model.impl;

import android.content.Context;

import com.yuri.dreamlinkcost.model.Main;

/**
 * Created by Yuri on 2016/1/16.
 */
public interface IMain extends IBaseMain{

    void initTitles(Context context);

    /**
     * 检查更新
     * @param byUser 自动触发检查，还是用户手动点击检查更新
     */
    void checkUpdate(Context context, boolean byUser, Main.OnCheckUpdateListener listener);
    void uploadNewApk(Context context, String filePath, Main.OnUploadListener listener);
}
