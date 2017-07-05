package com.yinshua.sqlitedemo.permission;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by marc on 2017/6/30.
 */

public class PermissionHelper {
    private Object mObject;
    private int mRequestCode;//权限请求码
    private String[] mRequestPermissions;//申请权限

    private PermissionHelper(Object object) {
        this.mObject = object;
    }

    public static void requestPermission(Activity activity, int requestCode, String[] permissions) {
        PermissionHelper.with(activity).requestCode(requestCode).requestPermissions(permissions).request();
    }

    public static void requestPermission(Fragment fragment, int requestCode, String[] permissions) {
        PermissionHelper.with(fragment).requestCode(requestCode).requestPermissions(permissions).request();
    }

    public static PermissionHelper with(Activity activity) {
        return new PermissionHelper(activity);
    }

    public static PermissionHelper with(Fragment fragment) {
        return new PermissionHelper(fragment);
    }

    public PermissionHelper requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public PermissionHelper requestPermissions(String[] requestPermissions) {
        this.mRequestPermissions = requestPermissions;
        return this;
    }

    public void request() {
        //判断是否大于6.0
        if (!PermissonUtils.isOverMarshmallow()) {
            PermissonUtils.executeSucceedMethod(mObject, mRequestCode);
            return;
        }
        List<String> deniedPermissions = PermissonUtils.getDeniedPermissions(mObject, mRequestPermissions);
        if (deniedPermissions.size() == 0) {
            //所有权限都授予了
            PermissonUtils.executeSucceedMethod(mObject, mRequestCode);
        } else {
            //申请权限 requestPermission申请权限
            ActivityCompat.requestPermissions(PermissonUtils.getContext(mObject),
                    deniedPermissions.toArray(new String[deniedPermissions.size()]), mRequestCode);
        }
    }

    /**
     * 处理权限的回调
     *
     * @param object
     * @param requestCode
     * @param permissions
     */
    public static void requestPermissionResult(Object object, int requestCode, String[] permissions) {
        //再次拿到没有授权的权限
        List<String> deniedPermissions = PermissonUtils.getDeniedPermissions(object, permissions);
        if (deniedPermissions.size() == 0) {
            PermissonUtils.executeSucceedMethod(object, requestCode);
        } else {
            //申请的权限中，还有用户不同意的
            PermissonUtils.executeFailMethod(object, requestCode);
        }
    }
}
