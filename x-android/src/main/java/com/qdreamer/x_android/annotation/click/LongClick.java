package com.qdreamer.x_android.annotation.click;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author XJun
 * @date 2023/11/14 17:48
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventType(listenerType = View.OnLongClickListener.class,listenerSetter = "setOnLongClickListener")
public @interface LongClick {
}
