package com.qdreamer.x_android.annotation.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author XJun
 * @date 2023/12/5 14:50
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionRequest {
    /**
     * 需要申请的权限
     * @return
     */
    String[] value();

    /**
     * 请求码
     * @return
     */
    int requestCode();
}
