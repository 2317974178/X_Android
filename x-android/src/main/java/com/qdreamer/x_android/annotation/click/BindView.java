package com.qdreamer.x_android.annotation.click;

import androidx.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author XJun
 * @date 2023/11/30 14:28
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindView {
   @IdRes int value();

   /**
    * 是否设置监听
    * @return
    */
   boolean isSetClick();
}
