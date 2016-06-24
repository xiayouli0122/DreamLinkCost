package com.yuri.dreamlinkcost.utils.permission;

import java.util.Random;

/**
 * Created by Yuri on 2016/5/18.
 */
public class PermissionRequest {

    private static Random random;
    private int requestCode;
    private PermissionCallback permissionCallback;


    public PermissionRequest(int requestCode) {
        this.requestCode = requestCode;
    }

    public PermissionRequest(PermissionCallback permissionCallback) {
        this.permissionCallback = permissionCallback;
        if (random == null) {
            random = new Random();
        }
        this.requestCode = random.nextInt(32768);
    }

    public int getRequestCode() {
        return requestCode;
    }

    public PermissionCallback getPermissionCallback() {
        return permissionCallback;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof PermissionRequest) {
            return ((PermissionRequest) object).requestCode == this.requestCode;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return requestCode;
    }
}
