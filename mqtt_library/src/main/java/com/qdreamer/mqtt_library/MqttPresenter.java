package com.qdreamer.mqtt_library;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author XJun
 * @date 2024/1/29 14:19
 **/
public class MqttPresenter extends Mqtt{

    private final static String TAG = MqttPresenter.class.getSimpleName();

    private MqttAndroidClient mClient;
    private MqttStateCallback mqttCallback;
    private MqttConnectOptions mOptions;
    private Context context;

    @Override
    public void initMqttClient(Context context,String serverUrl,String clientId) {
        this.context = context;
        mClient = new MqttAndroidClient(context,serverUrl,clientId);
        mClient.setCallback(mqttCallbackExtended);
    }

    @Override
    public void setMqttCallback(MqttStateCallback mqttCallback) {
        this.mqttCallback = mqttCallback;
    }

    @Override
    public MqttConnectOptions initOptions(boolean automaticReconnect, boolean clearSession, int connectionTimeout, @Nullable String userName, @Nullable char[] password, int keepAliveInterval, int maxInflight, int version) {
        mOptions = new MqttConnectOptions();
        mOptions.setAutomaticReconnect(automaticReconnect);
        mOptions.setCleanSession(clearSession);
        mOptions.setConnectionTimeout(connectionTimeout);
        if(userName != null){
            mOptions.setUserName(userName);
        }
        if(password != null){
            mOptions.setPassword(password);
        }
        mOptions.setKeepAliveInterval(keepAliveInterval);
        mOptions.setMaxInflight(maxInflight);
        mOptions.setMqttVersion(version);
        return mOptions;
    }

    @Override
    public void connectService() {
        if(mOptions != null && mClient != null){
            try {
                mClient.connect(mOptions, context, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if(mqttCallback!=null){
                            mqttCallback.onSuccess(asyncActionToken);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if(mqttCallback!=null){
                            mqttCallback.onFailure(asyncActionToken, exception);
                        }
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else {
            Log.e(TAG, "connectService: 未配置MqttConnectOptions");
        }
    }

    @Override
    public void subscribeTopic(String topic, int qos) {
        if(mClient != null){
            try {
                mClient.subscribe(topic,qos);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else {
            Log.e(TAG, "subscribeTopic: 未初始化MqttAndroidClient");
        }
    }

    @Override
    public void sendMessage(String topic, byte[] messageContent) {
        if(mClient != null){
            MqttMessage message = new MqttMessage();
            message.setPayload(messageContent);
            try {
                mClient.publish(topic,message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else {
            Log.e(TAG, "subscribeTopic: 未初始化MqttAndroidClient");
        }
    }

    private MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            if(mqttCallback!=null){
                mqttCallback.connectComplete(reconnect, serverURI);
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            if(mqttCallback!=null){
                mqttCallback.connectionLost(cause);
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            if(mqttCallback!=null){
                mqttCallback.messageArrived(topic, message);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            if(mqttCallback!=null){
                mqttCallback.deliveryComplete(token);
            }
        }
    };
}
