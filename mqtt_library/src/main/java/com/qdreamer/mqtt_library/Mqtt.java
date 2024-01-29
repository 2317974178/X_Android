package com.qdreamer.mqtt_library;

import android.content.Context;

import androidx.annotation.Nullable;

import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * @author XJun
 * @date 2024/1/29 14:26
 * 抽象类，定义抽象方法
 **/
public abstract class Mqtt {
    /**
     * 初始化MqttAndroidClient
     * @param context 上下文
     * @param serverUrl 连接url
     * @param clientId 客户端Id
     */
    public abstract void initMqttClient(Context context, String serverUrl, String clientId);

    /**
     * 设置Mqtt状态监听回调
     * @param mqttCallback callback回调
     */
    public abstract void setMqttCallback(MqttStateCallback mqttCallback);

    /**
     * 初始化连接设置
     * @param automaticReconnect 设置是否自动重连
     * @param clearSession 是否清空客户端的连接记录。若为true，则断开后，broker将自动清除该客户端连接信息
     * @param connectionTimeout 设置超时时间，单位为秒
     * @param userName 设置用户名。跟Client ID不同。用户名可以看做权限等级
     * @param password 设置登录密码
     * @param keepAliveInterval 心跳时间，单位为秒。即多长时间确认一次Client端是否在线
     * @param maxInflight 允许同时发送几条消息（未收到broker确认信息）
     * @param version 选择MQTT版本
     * @return 连接配置MqttConnectOptions
     */
    public abstract MqttConnectOptions initOptions(
            boolean automaticReconnect,
            boolean clearSession,
            int connectionTimeout,
            @Nullable String userName,
            @Nullable char[] password,
            int keepAliveInterval,
            int maxInflight,
            int version);

    /**
     * 连接服务器
     */
    public abstract void connectService();

    /**
     * 订阅主题
     * @param topic 需要订阅的主题
     * @param qos 质量
     */
    public abstract void subscribeTopic(String topic,int qos);

    /**
     * 发布消息
     * @param topic 需要发布消息的主题
     * @param messageContent 发布消息的内容
     */
    public abstract void sendMessage(String topic,byte[] messageContent);

}
