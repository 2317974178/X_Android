package com.qdreamer.qvoice;

import android.util.Log;

import java.util.Arrays;

public class QEngine {

    private static final long ENGINE_VALUE = 0L;
    private static final int FEED_DATA = 0;
    private static final int FEED_END = 1;

    private OnQVoiceCallback qVoiceCallback;
    private long engineValue = ENGINE_VALUE;
    private boolean isEngineStarted = false;
    private Thread engineThread = null;

    public QEngine(long session, String config, OnQVoiceCallback callback) {
        qVoiceCallback = callback;
        initEngine(session, config);
    }

    private native long newEngine(long session, String config);
    private native int startEngine(long engineValue);
    private native int feedEngine(long engineValue, byte[] audioData, int len, int end);
    private native int cancelEngine(long engineValue);
    private native int setEngine(long engineValue, String param);
    private native int resetEngine(long engineValue);
    private native byte[] readEngine(long engineValue);
    private native float getProbEngine(long engineValue);
    private native byte[] getFnEngine(long engineValue);
    private native int deleteEngine(long engineValue);

    private void initEngine(long session, String config) {
        engineValue = newEngine(session, config);
        engineThread = new Thread(() -> {
            try {
                while (engineValue != ENGINE_VALUE) {
                    byte[] bytes = readEngine(engineValue);
                    if (qVoiceCallback != null && engineValue != ENGINE_VALUE && bytes != null && bytes.length > 0) {
                        if (bytes.length > 1) {
                            qVoiceCallback.onQVoiceCallback(bytes[0], Arrays.copyOfRange(bytes, 1, bytes.length));
                        } else {
                            qVoiceCallback.onQVoiceCallback(bytes[0], null);
                        }
                    }
                    Thread.sleep(1);
                }
            } catch (Exception ignore) {
            }
        });
        engineThread.start();
    }

    public boolean startEngine() {
        synchronized (this) {
            if (engineValue != ENGINE_VALUE && !isEngineStarted) {
                startEngine(engineValue);
                isEngineStarted = true;
            }
            return isEngineStarted;
        }
    }

    public void setEngine(String param){
        if (engineValue != ENGINE_VALUE) {
            setEngine(engineValue,param);
        }
    }

    public float getProbEngine(){
        if(engineValue != ENGINE_VALUE){
            Log.d("qtk", "getProbEngine: "+ 1111111);
            float data = getProbEngine(engineValue);
            Log.d("qtk", "getProbEngine: "+ data);
            return data;
        }

        return 0f;
    }

    public byte[] getFnEngine(){
        if(engineValue != ENGINE_VALUE){
            return getFnEngine(engineValue);
        }
        return new byte[0];
    }

    public void feedEngine(byte[] audio) {
        if (engineValue != ENGINE_VALUE && isEngineStarted) {
            if (audio == null) {
                feedEngine(engineValue, new byte[0], 0, FEED_DATA);
            } else {
                feedEngine(engineValue, audio, audio.length, FEED_DATA);
            }
        }
    }

    public boolean stopEngine() {
        synchronized (this) {
            if (engineValue != ENGINE_VALUE && isEngineStarted) {
                isEngineStarted = false;
                feedEngine(engineValue, new byte[0], 0, FEED_END);
                resetEngine(engineValue);
            }
            return !isEngineStarted;
        }
    }

    public void releaseEngine() {
        stopEngine();
        qVoiceCallback = null;
        long temp = engineValue;
        engineValue = ENGINE_VALUE;
        deleteEngine(temp);
        if (engineThread != null) {
            engineThread.interrupt();
            engineThread = null;
        }
    }
}
