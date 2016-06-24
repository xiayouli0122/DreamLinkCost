package com.yuri.dreamlinkcost.utils.permission;


import android.Manifest;

/**
 * 需要使用到的权限集合
 * Created by Yuri on 2016/5/11.
 */
public class PermissionConstant {

    //权限列表
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String NET_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String GPS_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    //请求权限request code
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    public static final int CALL_PHONE_REQUEST_CODE = 1;
    public static final int LOCATION_REQUEST_CODE = 2;



}
