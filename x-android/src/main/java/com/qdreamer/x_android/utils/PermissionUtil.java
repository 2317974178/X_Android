package com.qdreamer.x_android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author XJun
 * @date 2023/12/6 11:29
 **/
public class PermissionUtil {

    /**
     * 检查是否有指定的权限
     *
     * @param context     上下文对象
     * @param permissions 需要检查的权限数组
     * @return 是否有指定的权限
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断是否需要向用户解释为什么需要该权限
     *
     * @param activity    Activity对象
     * @param permission  需要检查的权限
     * @return 是否需要解释权限
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }
        return false;
    }


    /**
     * 判断是否为永久拒绝的运行时权限
     *
     * @param activity   Activity对象
     * @param permissions 需要检查的权限
     * @return 是否为永久拒绝的运行时权限
     */
    public static boolean isPermissionPermanentlyDenied(Activity activity, String[] permissions) {
        for (String permission:permissions) {
            return !shouldShowRequestPermissionRationale(activity, permission) &&
                    !isPermissionGranted(activity, permission);
        }
        return false;
    }

    /**
     * 判断权限是否已经被授予
     *
     * @param context    上下文对象
     * @param permission 需要检查的权限
     * @return 权限是否已经被授予
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
