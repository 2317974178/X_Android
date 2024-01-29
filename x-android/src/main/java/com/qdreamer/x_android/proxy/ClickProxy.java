package com.qdreamer.x_android.proxy;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.qdreamer.x_android.annotation.click.BindView;
import com.qdreamer.x_android.annotation.click.EventType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XJun
 * @date 2023/11/14 16:41
 **/
public class ClickProxy {

    private static List<Integer> resIdList = new ArrayList();

    public static void injectEvent(Activity activity){
        Class<? extends Activity> aClass = activity.getClass();
        //获取Activity中所有的field
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field:declaredFields) {
            //获取字段上所有的注解
            Annotation[] annotations = field.getAnnotations();

            for (Annotation annotation:annotations) {
                //判断注解是否为BindView.class
                if(annotation instanceof BindView){
                    BindView bindView = field.getAnnotation(BindView.class);
                    //获取到注解中的控件Id
                    int resId = bindView.value();
                    View view = activity.findViewById(resId);
                    if(bindView.isSetClick()){
                        resIdList.add(resId);
                    }
                    field.setAccessible(true);
                    try {
                        field.set(activity,view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        Log.d("qtk", "injectView: " + e);
                    }
                }
            }
        }

        //获取到Activity中所有的Method
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method:declaredMethods) {
            //获得方法上所有的注解
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation:annotations) {
                //注解类型 anotation方法上面的注解 Click 或者 LongClick
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if(annotationType.isAnnotationPresent(EventType.class)){
                    EventType eventType = annotationType.getAnnotation(EventType.class);
                    //OnClickListener.class
                    Class listenerType = eventType.listenerType();
                    //setOnClickListener
                    String listenerSetter = eventType.listenerSetter();
                    try {
                        //不需要关心到底是Click还是LongClick
                        //此时valueMethod就是annotation.value()
//                        Method valueMethod = annotationType.getDeclaredMethod("value");
                        //通过注解的反射方法来拿到viewIds
//                        int[] viewIds = (int[]) valueMethod.invoke(annotation);
                        //如果通过这种方式去拿到viewIds就无法区分Click注解和LongClick注解
                        //method.getAnnotation(Click.class).value();
                        method.setAccessible(true);
                        ListenerInvocationHandler<Activity> handler = new ListenerInvocationHandler(activity, method);
                        Object listenerProxy = Proxy.newProxyInstance(listenerType.getClassLoader(),
                                new Class[]{listenerType}, handler);
                        // 遍历注解的值
                        for (int viewId : resIdList) {
                            // 获得当前activity的view（赋值）
                            View view = activity.findViewById(viewId);
                            // 获取指定的方法(不需要判断是Click还是LongClick)
                            // 如获得：setOnClickLisnter方法，参数为OnClickListener
                            // 获得 setOnLongClickLisnter，则参数为OnLongClickLisnter
                            Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                            // 执行方法
                            //执行setOnclickListener里面的回调 onclick方法
                            //相当于 view.setOnClickListener((View.OnClickListener)listenerProxy);
                            //也就是相当于在这个地方对activity中的view设置了监听
                            setter.invoke(view, listenerProxy);
                        }
                        //清楚列表中残留的id，防止影响下一次的注册监听
                        resIdList.clear();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 还可能在自定义View注入，所以是泛型： T = Activity/View
     * @param <T>
     */
    static class ListenerInvocationHandler<T> implements InvocationHandler{

        private Method method;
        private T target;
        //传进来的target为activity，method为带有Click或者LongClick的方法
        public ListenerInvocationHandler(T target,Method method){
            this.target = target;
            this.method = method;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //相当于activity.clickEvent(view)  args就是view
            return this.method.invoke(target,args);
        }
    }
}
