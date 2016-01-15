package com.yuri.dreamlinkcost.model;

/**
 * Created by Yuri on 2016/1/15.
 */
public interface CommitResultListener {
    void onCommitSuccess();
    void onCommitFail(int errorCode, String msg);
}
