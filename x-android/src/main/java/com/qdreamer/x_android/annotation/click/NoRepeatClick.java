package com.qdreamer.x_android.annotation.click;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author XJun
 * @date 2023/12/6 16:37
 * 防止重复点击
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoRepeatClick {
    /**
     * 点击间隔时间
     */
    long value() default 300;
}
