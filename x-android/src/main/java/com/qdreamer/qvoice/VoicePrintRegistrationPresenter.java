package com.qdreamer.qvoice;

/**
 * @author XJun
 * @date 2023/12/7 11:13
 **/
public class VoicePrintRegistrationPresenter implements VoicePrintPresenter, OnQVoiceCallback {

    private final long qSession;
    private final String voicePrintRegistrationConfig;
    private QEngine voicePrintRegistrationEngine;
    private OnQVoiceCallback qVoiceCallback;

    public VoicePrintRegistrationPresenter(long qSession,String voicePrintRegistrationConfig){
        this.qSession = qSession;
        this.voicePrintRegistrationConfig = voicePrintRegistrationConfig;
    }

    @Override
    public void setOnQVoiceCallback(OnQVoiceCallback listener) {
        qVoiceCallback = listener;
    }

    @Override
    public void initQEngine() {
        voicePrintRegistrationEngine = new QEngine(qSession,voicePrintRegistrationConfig,this);
    }

    @Override
    public boolean startQEngine() {
        return voicePrintRegistrationEngine.startEngine();
    }

    @Override
    public void feedQEngine(byte[] audio) {
        voicePrintRegistrationEngine.feedEngine(audio);
    }

    @Override
    public boolean stopQEngine() {
        return voicePrintRegistrationEngine.stopEngine();
    }

    @Override
    public void releaseQEngine() {
        qVoiceCallback = null;
        voicePrintRegistrationEngine.releaseEngine();
        voicePrintRegistrationEngine = null;
    }

    @Override
    public void setQEngine(String param) {
        voicePrintRegistrationEngine.setEngine(param);
    }

    @Override
    public float getProbQEngine() {
        return voicePrintRegistrationEngine.getProbEngine();
    }

    @Override
    public void onQVoiceCallback(byte type, byte[] data) {
        if(qVoiceCallback!=null){
            qVoiceCallback.onQVoiceCallback(type, data);
        }
    }


}
