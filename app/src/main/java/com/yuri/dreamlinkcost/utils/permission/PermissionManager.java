package com.yuri.dreamlinkcost.utils.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;

/**
 * 权限管理
 * Created by Yuri on 2016/5/18.
 */
public class PermissionManager {

    private static SparseArrayCompat mPermissionRequests = new SparseArrayCompat();

    /**
     * 判断当前系统是否是6.0以上系统
     * @return true 6.0以上系统，false，反之
     */
    public static boolean isAndroidM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查权限是否被授予
     * @param context context
     * @param permission 需要检查的全新啊
     * @return true，授予，false，未授予
     */
    public static boolean hasPermission(Context context, String permission) {
        //只有6.0以上系统才需要运行时检查权限，否则直接返回ture
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 权限是否被授予
     */
    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 权限是否被授予
     * @param result 请求权限结果
     * @return true,授予，false，反之
     */
    public static boolean verifyPermissions(int result) {
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void askPermission(Activity activity, String permission, PermissionCallback callBack) {
        askPermission(activity, new String[]{permission}, callBack);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void askPermission(Activity activity, String[] permissions, PermissionCallback callBack) {
        PermissionRequest permissionRequest = new PermissionRequest(callBack);
        mPermissionRequests.put(permissionRequest.getRequestCode(), permissionRequest);
        ActivityCompat.requestPermissions(activity, permissions, permissionRequest.getRequestCode());
    }

    public static void askPermission(Fragment fragment, String permission, PermissionCallback callback) {
        askPermissions(fragment, new String[]{permission}, callback);
    }

    public static void askPermissions(Fragment fragment, String[] permissions, PermissionCallback callback) {
        PermissionRequest permissionRequest = new PermissionRequest(callback);
        mPermissionRequests.put(permissionRequest.getRequestCode(), permissionRequest);
        fragment.requestPermissions(permissions, permissionRequest.getRequestCode());
    }

    public static void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionRequest permissionRequest = (PermissionRequest) mPermissionRequests.get(requestCode);
        if (permissionRequest != null) {
            if (verifyPermissions(grantResults)) {
                permissionRequest.getPermissionCallback().onPermissionGranted();
            } else {
                permissionRequest.getPermissionCallback().onPermissionRefused();
            }
            mPermissionRequests.delete(requestCode);
        }
    }

}
