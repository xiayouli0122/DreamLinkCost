package com.yuri.dreamlinkcost.presenter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.model.CommitResultListener;
import com.yuri.dreamlinkcost.model.Main;
import com.yuri.dreamlinkcost.model.impl.IMain;
import com.yuri.dreamlinkcost.view.impl.IMainView;

import java.util.List;

/**
 * Created by Yuri on 2016/1/16.
 */
public class MainPresenter extends BasePresenter<IMainView>{
    public static final int MSG_UPDATE_VERSION_VIEW = 0;
    public static final int MSG_NO_VERSION_UPDATE = 1;
    public static final int MSG_SHOW_UPDATE_NOTIFICATION = 2;
    public static final int MSG_SHOW_INSTALL_NOTIFICATION = 3;
    public static final int MSG_SHOW_INSTALL_DIALOG = 4;

    private IMain iMain;
    public MainPresenter(Context context, IMainView view) {
        super(context, view);
        iMain = new Main();
    }

    public int getUserId() {
        return iMain.getUserId(mContext);
    }

    public String[] getTitles() {
        return iMain.getTitles(mContext);
    }

    public void initTitles() {
        iMain.initTitles(mContext);
    }

    public void checkUpdate(boolean byUser, final Handler handler) {
        iMain.checkUpdate(mContext, byUser, new Main.OnCheckUpdateListener() {
            @Override
            public void onApkDownloaded(String version, String path, boolean server) {
                if (server) {
                    handler.sendMessage(handler.obtainMessage(MSG_SHOW_INSTALL_NOTIFICATION, version));
                } else {
                    handler.sendMessage(handler.obtainMessage(MSG_SHOW_INSTALL_DIALOG, path));
                }
            }

            @Override
            public void showUpdateNotification(String latestVersion, String url) {
                Bundle bundle = new Bundle();
                bundle.putString("version", latestVersion);
                bundle.putString("url", url);
                Message message = new Message();
                message.what = MSG_SHOW_UPDATE_NOTIFICATION;
                handler.sendMessage(message);
            }

            @Override
            public void noVersionNeedToUpdate() {
                handler.sendEmptyMessage(MSG_NO_VERSION_UPDATE);
            }
        });
    }

    public void doUpload(String filePath, Main.OnUploadListener listener) {
        iMain.uploadNewApk(mContext, filePath, listener);
    }

    public void doCommit(List<Cost> costList, CommitResultListener listener) {
        iMain.commitLocalData(mContext, costList, listener);
    }
}
