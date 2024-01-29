package com.qdreamer.utils;

import android.util.Log;

import com.qdreamer.qvoice.QAudio;
import com.qdreamer.utils.inter.OnQAudioDataCallback;

/**
 * @author XJun
 * @date 2023/12/4 9:58
 **/
public class QAudioPresenter {

    private static QAudio qAudio  = new QAudio();
    private OnQAudioDataCallback listener;
    private Thread thread;


    public QAudioPresenter(long session, String res){
        qAudio = new QAudio();
        boolean isInit = qAudio.newAudio(session, res);
        Log.d("qtk", "QAudio init : " + isInit);
    }

    public void setCallback(OnQAudioDataCallback listener){
        this.listener = listener;
    }

    public  void startRecord(){
        if(qAudio != null){
            qAudio.startRecord();
            readRecordData();
        }
    }

    public void stopRecord(){
        if(qAudio!=null){
            qAudio.stopRecord();
        }
        if(thread!=null){
            thread.interrupt();
        }
    }

    public void releaseQAudioPresenter(){
        if(qAudio!=null){
            qAudio.stopRecord();
            qAudio.deleteAudio();
        }
        if (thread != null) {
            if(!thread.isAlive()){
                thread = null;
            }else {
                thread.interrupt();
                thread = null;
            }
        }
    }

    public void readRecordData(){
        thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!thread.isInterrupted()){
                    if(qAudio!=null && listener!=null){
                        byte[] data = qAudio.readRecordData();
                        if(data!=null){
                            listener.OnQAudioDataCallback(data);
                        }
                    }
                }
            }
        });
        thread.start();
    }
}
