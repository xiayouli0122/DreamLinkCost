package com.yuri.dreamlinkcost.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.activeandroid.query.Select;
import com.yuri.dreamlinkcost.BuildConfig;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.bean.Bmob.Version;
import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.bean.table.Title;
import com.yuri.dreamlinkcost.model.impl.IMain;
import com.yuri.dreamlinkcost.utils.NetUtil;
import com.yuri.dreamlinkcost.utils.SharedPreferencesUtil;
import com.yuri.xlog.Log;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

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
        int versionCode = SharedPreferencesUtil.get(context, "latest_version_code", -1);
        int currentVersionCode = BuildConfig.VERSION_CODE;
        if (versionCode > currentVersionCode) {
            //有新版本已经下载好了，可以直接安装
            String apkPath = SharedPreferencesUtil.get(context, "apkPath", null);
            String changeLog = SharedPreferencesUtil.get(context, "changeLog", "");
            if (apkPath != null && new File(apkPath).exists()) {
                //显示安装Dialog
                listener.onApkDownloaded(null, apkPath, changeLog, false);
                return;
            }
        }
        BmobQuery<Version> bmobQuery = new BmobQuery();
        bmobQuery.findObjects(new FindListener<Version>() {
            @Override
            public void done(List<Version> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        int serverVersionCode = list.get(0).version_code;
                        int currentVersionCode = BuildConfig.VERSION_CODE;
                        Log.d("serverVersionCode:" + serverVersionCode + ",currentVersionCode:"
                                + currentVersionCode);
                        if (serverVersionCode > currentVersionCode) {
                            //有新版本了
                            Log.d("Need to update");
                            String url = list.get(0).apkUrl;
                            String serverVersion = list.get(0).version;
                            String changeLog = list.get(0).changeLog;
                            //has new version
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("version", serverVersion);
                            bundle.putInt("version_code", serverVersionCode);
                            bundle.putString("url", url);
                            bundle.putString("changeLog", changeLog);
                            message.setData(bundle);

                            if (byUser) {
                                listener.showUpdateNotification(serverVersion, url);
                            } else {
                                if (NetUtil.isWifiConnected(context)) {
                                    doDownloadNewVersionAPk(context, serverVersion, serverVersionCode,
                                            changeLog,
                                            url, listener);
                                } else {
                                    //非wifi下，什么都不做了，浪费流量
//                                listener.showUpdateNotification(serverVersion, url);
                                }
                            }
                        } else {
                            if (byUser) {
                                listener.noVersionNeedToUpdate();
                            }
                        }
                    }
                } else {
                    Log.e(e.getMessage());
                }
            }
        });
    }

    @Override
    public void uploadNewApk(final Context context, String filePath, final OnUploadListener listener) {

        final BmobFile bmobFile = new BmobFile(new File(filePath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d("success.name:" + bmobFile.getFilename() + ",url:" + bmobFile.getFileUrl());
                    Version version =new Version();
                    version.version = BuildConfig.VERSION_NAME;
                    version.version_code = BuildConfig.VERSION_CODE;
                    version.apkUrl = bmobFile.getFileUrl();
                    version.changeLog = context.getString(R.string.change_log);
                    version.update("692ZQQQp", new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.d();
                                if (listener != null) {
                                    listener.onUploadSuccess();
                                }
                            } else {
                                Log.d(e.getMessage());
                                if (listener != null) {
                                    listener.onUploadFail(e.getMessage());
                                }
                            }
                        }
                    });
                } else {
                    Log.d("error:" + e.getMessage());
                    if (listener != null) {
                        listener.onUploadFail(e.getMessage());
                    }
                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                Log.d("progress：" + value);
                if (listener != null) {
                    listener.onUploadProgress(value);
                }
            }
        });
    }

    //自动后台下载新版本apk，下载完毕后直接提醒用户
    private void doDownloadNewVersionAPk(final Context context, final String version, final int versionCode,
                                         final String chageLog,
                                         final String fileName, final OnCheckUpdateListener listener) {
        BmobFile bmobFile = new BmobFile("test.apk", "", fileName);
        bmobFile.download(new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.d("Download Apk Success.localPath:" + s);
                    SharedPreferencesUtil.put(context, "latest_version_code", versionCode);
                    SharedPreferencesUtil.put(context, "apkPath", s);
                    SharedPreferencesUtil.put(context, "changeLog", chageLog);
                    listener.onApkDownloaded(version, s, chageLog, true);
                } else {
                    Log.e("Download apk faile:" + s);
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }

    @Override
    public void commitLocalData(Context context, List<Cost> costList, CommitResultListener listener) {
        BmobCostYuri bmobCost;
        for (final Cost cost : costList) {
            bmobCost = cost.getCostBean();
            bmobCost.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        Log.d("upload success:" + cost.title);
                        //上传成功后，删除本地数据
                        cost.delete();
                    } else {
                        Log.d("upload failure:" + cost.title);
                    }
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
        void onApkDownloaded(String version, String path, String changeLog, boolean server);
        void showUpdateNotification(String latestVersion, String url);
        void noVersionNeedToUpdate();
    }

    public interface OnUploadListener{
        void onUploadSuccess();
        void onUploadProgress(int progress);
        void onUploadFail(String msg);
    }
}
