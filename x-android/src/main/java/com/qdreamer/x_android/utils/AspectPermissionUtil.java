package com.qdreamer.x_android.utils;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;


import com.qdreamer.x_android.activity.PermissionActivity;
import com.qdreamer.x_android.annotation.permission.PermissionDenied;
import com.qdreamer.x_android.annotation.permission.PermissionGranted;
import com.qdreamer.x_android.annotation.permission.PermissionReject;
import com.qdreamer.x_android.annotation.permission.PermissionRequest;
import com.qdreamer.x_android.listener.PermissionResultCallback;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XJun
 * @date 2023/12/5 14:48
 * @description 定义切面类
 **/
@Aspect
public class AspectPermissionUtil {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * 1、应用中用到了哪些注解，放到当前的切入点进行处理（找到需要处理的切入点）
     *      execution，以方法执行时作为切点，触发Aspect类
     *      * *(..)) 可以处理带有Permission注解的所有方法
     * 2、@annotation(permission)代表注解的赋值在切入点传入接收，那么在permission方法中就可以接收，同样，在Advice中也可以接收这个permission参数
     */
    @Pointcut("execution(@com.qdreamer.x_android.annotation.permission.PermissionRequest * *(..)) && @annotation(permission)")
    public void permission(PermissionRequest permission){

    }

    /**
     * 2、对切入点如何处理
     */
    @Around("permission(permission)")
    public void joinPoint(ProceedingJoinPoint joinPoint,PermissionRequest permission) {
        //这里去申请权限
        //获取上下文
        Object object = joinPoint.getThis();
        Context context = null;
        if(object instanceof Context){
            context = (Context) object;
        }else if(object instanceof Fragment){
            context = ((Fragment) object).getContext();
        }

        List<Method> permissionDeniedMethodList = new ArrayList<>();
        List<Method> permissionGrantedMethodList = new ArrayList<>();
        List<Method> permissionRejectMethodList = new ArrayList<>();

        if(context != null){
            Class<? extends Context> aClass = context.getClass();
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method method:declaredMethods) {
                if(method.getAnnotation(PermissionDenied.class) != null){
                    permissionDeniedMethodList.add(method);
                }else if(method.getAnnotation(PermissionGranted.class) != null){
                    permissionGrantedMethodList.add(method);
                }else if(method.getAnnotation(PermissionReject.class) != null){
                    permissionRejectMethodList.add(method);
                }
            }
        }

        //获取注解参数
        if(context == null || permission == null || permission.value().length == 0){
            return;
        }
        String[] value = permission.value();
        int requestCode = permission.requestCode();
        Log.e(this.getClass().getSimpleName(),"value -"+value+"requestCode = "+requestCode );
        PermissionActivity.openActivity(context, value, requestCode, new PermissionResultCallback() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted: 获取了权限");
                try {
                    joinPoint.proceed();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if(permissionGrantedMethodList.size()>0){
                    for (Method method:permissionGrantedMethodList) {
                        try {
                            method.setAccessible(true);
                            method.invoke(joinPoint.getThis());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    permissionGrantedMethodList.clear();
                }
            }

            @Override
            public void onPermissionReject() {
                Log.d(TAG, "onPermissionReject: 拒绝了权限");
                if(permissionRejectMethodList.size()>0){
                    for (Method method:permissionRejectMethodList) {
                        try {
                            method.setAccessible(true);
                            method.invoke(joinPoint.getThis());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    permissionRejectMethodList.clear();
                }
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "onPermissionDenied: 拒绝了权限并不再询问");
                if(permissionDeniedMethodList.size()>0){
                    for (Method method:permissionDeniedMethodList) {
                        try {
                            method.setAccessible(true);
                            method.invoke(joinPoint.getThis());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    permissionDeniedMethodList.clear();
                }
            }
        });

    }
}
