package com.qdreamer.x_android.utils;

import android.util.Log;
import android.view.View;

import com.qdreamer.x_android.annotation.click.NoRepeatClick;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author XJun
 * @date 2023/12/6 16:38
 **/
@Aspect
public class AspectClickUtil {

    private static Long sLastClick = 0L;

    /**
     * 定义切点，标记切点为所有被@SingleClick注解的方法
     * 注意：这里com.qdreamer.annotation.click.SingleClick需要替换成
     * 你自己项目中SingleClick这个类的全路径哦
     * pointcut指明了什么情况下执行切面方法，还有比如某些方法，某个路径的指定，可以自行学习
     */
    @Pointcut("execution(@com.qdreamer.x_android.annotation.click.NoRepeatClick * *(..))")
    public void method() {}

    @Around("method()")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint){
        //取出方法的参数
        View view = null;
        for (Object arg: joinPoint.getArgs()) {
            if(arg instanceof View){
                view = (View) arg;
                break;
            }
        }
        if(view == null){
            return;
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method =  methodSignature.getMethod();
        if(method.getAnnotation(NoRepeatClick.class) == null){
            return;
        }
        NoRepeatClick noRepeatClick = method.getAnnotation(NoRepeatClick.class);
        //判断是否快速点击
        if(System.currentTimeMillis()- sLastClick >=noRepeatClick.value()){
            sLastClick = System.currentTimeMillis();
            try {
                joinPoint.proceed();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            Log.e("qtk", "aroundJoinPoint: Click Invalid");
        }
    }
}
