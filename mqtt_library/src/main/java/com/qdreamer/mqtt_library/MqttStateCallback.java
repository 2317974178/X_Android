package com.qdreamer.mqtt_library;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author XJun
 * @date 2024/1/29 14:42
 **/
public interface MqttStateCallback {
    /**
     * 连接成功
     * @param reconnect 如果为 true，则连接是自动重新连接的结果。
     * @param serverURI 建立连接的服务器 URI。
     */
    void connectComplete(boolean reconnect, String serverURI);

    /**
     * 连接丢失
     * @param cause 连接丢失的原因
     */
    void connectionLost(Throwable cause);

    /**
     * 消息到达
     * @param topic 主题
     * @param message 实际的消息
     */
    void messageArrived(String topic, MqttMessage message);

    /**
     * 在邮件传递完成且已收到所有确认时调用。
     * 对于 QoS 0 消息，一旦消息被传递到网络进行传递，就会调用它。
     * 对于 QoS 1，当收到 PUBACK 时调用它，对于 QoS 2，
     * 当收到 PUBCOMP 时调用它。令牌将与发布消息时返回的令牌相同。
     * @param token 与消息关联的传递令牌。
     */
    void deliveryComplete(IMqttDeliveryToken token);

    /**
     * 服务器连接成功
     * @param asyncActionToken 与已完成的操作相关联
     */
    public void onSuccess(IMqttToken asyncActionToken);

    /**
     * 服务器连接失败
     * @param asyncActionToken 与失败的操作相关联
     * @param exception 由失败的操作引发
     */
    public void onFailure(IMqttToken asyncActionToken, Throwable exception);
}
