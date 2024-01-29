package com.qdreamer.qvoice;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author XJun
 * @date 2023/11/16 16:07
 **/
public class AecnspickupPresenter implements IQVoicePresenter,OnQVoiceCallback{
    private final long qSession;
    private final String aecnspickupConfig;
    private QEngine aecnspickupEngine;
    private OnQVoiceCallback qVoiceCallback;

    public AecnspickupPresenter(long qSession, String aecnspickupConfig) {
        this.qSession = qSession;
        this.aecnspickupConfig = aecnspickupConfig;
    }

    @Override
    public void setOnQVoiceCallback(@Nullable OnQVoiceCallback listener) {
        Log.d("qtk", "setOnQVoiceCallback: " + listener);
        qVoiceCallback = listener;
    }

    @Override
    public void initQEngine() {
        aecnspickupEngine = new QEngine(qSession, aecnspickupConfig, this);
    }

    @Override
    public boolean startQEngine() {
        return aecnspickupEngine.startEngine();
    }

    @Override
    public void feedQEngine(@NonNull byte[] audio) {
        aecnspickupEngine.feedEngine(audio);
    }


    @Override
    public boolean stopQEngine() {
        return aecnspickupEngine.stopEngine();
    }

    @Override
    public void releaseQEngine() {
        qVoiceCallback = null;
        aecnspickupEngine.releaseEngine();
        aecnspickupEngine = null;
    }

    @Override
    public void onQVoiceCallback(byte type, @Nullable byte[] data) {
        Log.d("qtk", "onQVoiceCallback: " + qVoiceCallback);
        if (qVoiceCallback != null) {
            qVoiceCallback.onQVoiceCallback(type, data);
        }
    }
}
