package com.qdreamer.utils;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * 实现录音
 *
 * @author chenmy0709
 * @version V001R001C01B001
 */
public class AudioRecorder {

    //录音对象
    private AudioRecord audioRecord;
    private boolean isRecording = false;
    private RecordStreamListener listener;
    private Thread recorderThread;
    private int recorderBufSize;

    @SuppressLint("MissingPermission")
    public void initRecorder(){
        recorderBufSize = AudioRecord
                .getMinBufferSize(44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
        Log.i("audioRecordTest", "size->" + recorderBufSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                recorderBufSize);
    }

    public void setListener(RecordStreamListener listener){
        this.listener = listener;
    }

    public void startRecord(){
        if(isRecording){
            return;
        }
        isRecording = true;
        recorderThread = new Thread(() -> {
            byte[] data = new byte[recorderBufSize];
            Log.i("audioRecordTest", "开始录音");
            while (isRecording){
                audioRecord.read(data,0,recorderBufSize);
                listener.getData(data);
            }
        });
        recorderThread.start();
    }

    public void stopRecord(){
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            Log.i("audioRecordTest", "停止录音");
        }
    }

    public void releaseRecord(){
        if(audioRecord != null){
            audioRecord.release();
            audioRecord = null;
        }
        recorderThread.interrupt();
        recorderThread = null;
    }


    public interface RecordStreamListener{
        void getData(byte[] data);
    }
}
