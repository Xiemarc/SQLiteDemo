package com.yinshua.sqlitedemo.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marc on 2017/6/30.
 */

public class PermissonUtils {
    private PermissonUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 是否大于6.0
     *
     * @return
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 权限通过后执行方法
     *
     * @param reflectClass
     * @param requestCode
     */
    public static void executeSucceedMethod(Object reflectClass, int requestCode) {
        Method[] methods = reflectClass.getClass().getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            //拿到该reflectClass中注解成功的方法
            PermissionSuccess successMethod = method.getAnnotation(PermissionSuccess.class);
            if (null != successMethod) {
                int methodRequestCode = successMethod.requestCode();
                if (requestCode == methodRequestCode) {
                    //执行该标记的方法
                    executeMethod(reflectClass, method);
                }
            }
        }
    }

    /**
     * 执行失败方法
     *
     * @param reflectClass
     * @param requestCode
     */
    public static void executeFailMethod(Object reflectClass, int requestCode) {
        Method[] methods = reflectClass.getClass().getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            PermissionFail failMethod = method.getAnnotation(PermissionFail.class);
            if (null != failMethod) {
                int methodRequestCode = failMethod.requestCode();
                if (methodRequestCode == requestCode) {
                    executeMethod(reflectClass, method);
                    break;
                }
            }
        }
    }

    /**
     * 获取没有授予的权限
     *
     * @param object             activity或者fragment
     * @param requestPermissions
     * @return 没有授予过的权限
     */
    public static List<String> getDeniedPermissions(Object object, String[] requestPermissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String requestPermission : requestPermissions) {
            //把没有授予过的权限加入到集合 ,checkSelfPermission检查权限是否给予
            if (ContextCompat.checkSelfPermission(getContext(object), requestPermission)
                    == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(requestPermission);
            }
        }
        return deniedPermissions;
    }


    /**
     * 执行方法
     *
     * @param reflectClass
     * @param method
     */
    private static void executeMethod(Object reflectClass, Method method) {
        method.setAccessible(true);
        try {
            method.invoke(reflectClass, new Object[]{});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得上下文
     *
     * @param object
     * @return
     */
    public static Activity getContext(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        }
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        return null;
    }
}
