package com.qdreamer.x_android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.qdreamer.x_android.annotation.click.RunTimePermission;

import java.util.ArrayList;

/**
 * @author XJun
 * @date 2023/12/4 14:34
 **/
@SuppressLint("RestrictedApi")
public class RuntimePermission {
    /**
     * 申请权限
     * @param activity
     */
    public static void requirePermission(Activity activity){
        Class<? extends Activity> aClass = activity.getClass();
        RunTimePermission annotation = aClass.getAnnotation(RunTimePermission.class);
        String[] value = annotation.value();
        ArrayList<String> toApplyList = new ArrayList<>();
        for (String perm : value) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), 1);
        }
    }
}
