package com.qdreamer.qvoice;

import java.util.Arrays;

public class QSession {

    private static final long SESSION_VALUE = 0L;

    private OnQVoiceCallback qVoiceCallback;
    private long session;
    private Thread sessionThread = null;

    public long getSession() {
        return session;
    }

    public QSession(String config, OnQVoiceCallback callback) {
        qVoiceCallback = callback;
        session = init(config);
        sessionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (session != SESSION_VALUE) {
                        byte[] bytes = sessionRead(session);
                        if (qVoiceCallback != null && session != SESSION_VALUE && bytes != null && bytes.length > 0) {
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
            }
        });
        sessionThread.start();
    }

    private native long init(String config);
    private native byte[] sessionRead(long session);
    private native void exit(long session);

    public void releaseSession() {
        qVoiceCallback = null;
        if (session != SESSION_VALUE) {
            long temp = session;
            session = SESSION_VALUE;
            exit(temp);
        }
        if (sessionThread != null) {
            sessionThread.interrupt();
            sessionThread = null;
        }
    }

    private static String UserIDGET() {
        return "VoicePrintRecognition";
    }

}
