package com.qdreamer.x_android.listener;

/**
 * @author XJun
 * @date 2023/12/6 11:19
 **/
public interface PermissionResultCallback {
    /**
     * 同意权限
     */
    void onPermissionGranted();

    /**
     * 拒绝权限
     */
    void onPermissionReject();

    /**
     * 拒绝权限并不再询问
     */
    void onPermissionDenied();
}
