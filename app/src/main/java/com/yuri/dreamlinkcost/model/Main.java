package com.yuri.dreamlinkcost.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.activeandroid.query.Select;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.bmob.btp.callback.UploadListener;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.SharedPreferencesManager;
import com.yuri.dreamlinkcost.Utils;
import com.yuri.dreamlinkcost.bean.Bmob.BmobCost;
import com.yuri.dreamlinkcost.bean.Bmob.Version;
import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.bean.table.Title;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.impl.IMain;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Yuri on 2016/1/16.
 */
public class Main extends BaseMain implements IMain {

    @Override
    public void initTitles(Context context) {
        List<Title> titles = new Select().from(Title.class).execute();
        if (titles == null || titles.size() == 0) {
            String[] titleArrays = context.getResources().getStringArray(R.array.title_arrays);
            Title title;
            for (String titleStr : titleArrays) {
                title = new Title();
                title.mTitle = titleStr;
                title.save();
            }
        }
    }

    @Override
    public void checkUpdate(final Context context, final boolean byUser, final OnCheckUpdateListener listener) {
        if (listener == null) {
            throw new NullPointerException("OnCheckUpdateListener cannot be null");
        }

        //第一步检查本地新版本
        int versionCode = SharedPreferencesManager.get(context, "latest_version_code", -1);
        int currentVersionCode = Utils.getVersionCode(context);
        if (versionCode > currentVersionCode) {
            //有新版本已经下载好了，可以直接安装
            String apkPath = SharedPreferencesManager.get(context, "apkPath", null);
            if (apkPath != null && new File(apkPath).exists()) {
                //显示安装Dialog
                listener.onApkDownloaded(null, apkPath, false);
                return;
            }
        }
        BmobQuery<Version> bmobQuery = new BmobQuery();
        bmobQuery.findObjects(context, new FindListener<Version>() {
            @Override
            public void onSuccess(List<Version> list) {
                if (list != null && list.size() > 0) {
                    int serverVersionCode = list.get(0).version_code;
                    int currentVersionCode = Utils.getVersionCode(context);
                    Log.d("serverVersionCode:" + serverVersionCode + ",currentVersionCode:"
                            + currentVersionCode);
                    if (serverVersionCode > currentVersionCode) {
                        //有新版本了
                        Log.d("Need to update");
                        String url = list.get(0).apkUrl;
                        String serverVersion = list.get(0).version;
                        //has new version
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("version", serverVersion);
                        bundle.putInt("version_code", serverVersionCode);
                        bundle.putString("url", url);
                        message.setData(bundle);

                        if (byUser) {
                            listener.showUpdateNotification(serverVersion, url);
                        } else {
                            if (Utils.isWifiConnected(context)) {
                                doDownloadNewVersionAPk(context, serverVersion, serverVersionCode,
                                        url, listener);
                            } else {
                                listener.showUpdateNotification(serverVersion, url);
                            }
                        }
                    } else {
                        if (byUser) {
                            listener.noVersionNeedToUpdate();
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.e(s);
            }
        });
    }

    @Override
    public void uploadNewApk(final Context context, String filePath, final OnUploadListener listener) {
        BmobProFile.getInstance(context).upload(filePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1) {
                Log.d("success.name:" + s + ",url:" + s1);
                if (listener != null) {
                    listener.onUploadSuccess();
                }
                Version version =new Version();
                version.version = Utils.getAppVersion(context);
                version.version_code = Utils.getVersionCode(context);
                version.apkUrl = s;
                version.update(context, "692ZQQQp", new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.d();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.d();
                    }
                });
            }

            @Override
            public void onProgress(int i) {
                Log.d("progress：" + i);
                if (listener != null) {
                    listener.onUploadProgress(i);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d("error:" + s);
                if (listener != null) {
                    listener.onUploadFail(s);
                }
            }
        });
    }

    //自动后台下载新版本apk，下载完毕后直接提醒用户
    private void doDownloadNewVersionAPk(final Context context, final String version, final int versionCode,
                                        final String fileName, final OnCheckUpdateListener listener) {
        BmobProFile.getInstance(context).download(fileName, new DownloadListener() {
            @Override
            public void onSuccess(String s) {
                Log.d("Download Apk Success.localPath:" + s);
                SharedPreferencesManager.put(context, "latest_version_code", versionCode);
                SharedPreferencesManager.put(context, "apkPath", s);
                listener.onApkDownloaded(version, s, true);
            }

            @Override
            public void onProgress(String s, int i) {

            }

            @Override
            public void onError(int i, String s) {
                Log.e("Download apk faile:" + s);
            }
        });
    }

    @Override
    public void commitLocalData(Context context, List<Cost> costList, CommitResultListener listener) {
        BmobCost bmobCost;
        for (final Cost cost : costList) {
            bmobCost = cost.getCostBean();
            bmobCost.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.d("upload success:" + cost.title);
                    //上传成功后，删除本地数据
                    cost.delete();
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.d("upload failure:" + cost.title);
                }
            });
        }

        if (listener != null) {
            listener.onCommitSuccess();
        }
    }

    public static class UpdateStatus{
        public static final int DOWNLOADED = 0;//安装包已下载完成
        public static final int NEW = 1;//有新版本
        public static final int NONE = 2;//已经是最新版本了
        public static final int ERROR = 3;//检查更新失败，可能是网络问题
    }

    public interface OnCheckUpdateListener{
        void onApkDownloaded(String version, String path, boolean server);
        void showUpdateNotification(String latestVersion, String url);
        void noVersionNeedToUpdate();
    }

    public interface OnUploadListener{
        void onUploadSuccess();
        void onUploadProgress(int progress);
        void onUploadFail(String msg);
    }
}
