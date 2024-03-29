package com.qdreamer.utils;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.util.Log;

import com.qdreamer.utils.inter.IRecordOption;
import com.qdreamer.utils.inter.IRecordPresenter;
import com.qdreamer.utils.inter.OnAudioRecordListener;

import java.util.Arrays;

public class AudioRecordPresenter implements IRecordPresenter {

    private AudioRecord audioRecord = null;
    private int bufferSize = 0;

    private OnAudioRecordListener readListener = null;
    private Thread readThread = null;

    @SuppressLint("MissingPermission")
    @Override
    public void initRecorder(IRecordOption option) throws Exception {
        if (!(option instanceof AudioRecordOption)) {
            throw new RuntimeException("录音机配置参数异常");
        }
        if (audioRecord != null) {
            releaseRecorder();
        }
        AudioRecordOption recordOption = (AudioRecordOption) option;
        bufferSize = AudioRecord.getMinBufferSize(recordOption.getSample(), recordOption.getChannel(), recordOption.getFormat());
//        bufferSize = recordOption.getBufferSize();
        Log.d("qtk", "initRecorder: "+ bufferSize);
        audioRecord = new AudioRecord(
            recordOption.getSource(), recordOption.getSample(), recordOption.getChannel(), recordOption.getFormat(), bufferSize
        );
    }

    @Override
    public boolean startRecorder(OnAudioRecordListener listener) {
        if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord.startRecording();
            }
            readListener = listener;
            readAudioData();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stopRecorder() {
        readListener = null;
        if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED
        && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop();
        }
        stopReadAudio();
    }

    @Override
    public void releaseRecorder() {
        stopRecorder();
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    private void stopReadAudio() {
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }
    }

    private void readAudioData() {
        stopReadAudio();
        readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED
                    && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING && readListener != null) {
                        byte[] buffer = new byte[bufferSize];
                        int len = audioRecord.read(buffer, 0, bufferSize);
                        if (len == bufferSize) {
                            if (readListener != null) {
                                readListener.onRecordAudio(buffer);
                            }
                        } else if (len > 0) {
                            if (readListener != null) {
                                readListener.onRecordAudio(Arrays.copyOfRange(buffer, 0, len));
                            }
                        }
                        Thread.sleep(1);
                    }
                } catch (Exception ignore) {
                }
            }
        });
        readThread.start();
    }

}
