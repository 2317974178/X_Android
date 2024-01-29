package com.qdreamer.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import androidx.annotation.Nullable;

import com.qdreamer.utils.inter.OnAudioTrackStateCallback;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author XJun
 * @date 2024/1/29 10:21
 **/
public class AudioTrackPresenter {
    /**
     * 采样率
     * 16000
     */
    private static final int SAMPLE_RATE = 16000;
    /**
     * 通道数
     * 单声道
     */
    private static final int CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    /**
     * 音频数据的格式
     * 16bit
     */
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private BufferedInputStream bufferedInputStream;

    private Thread playInfoThread;

    private AudioTrack audioTrack;

    private OnAudioTrackStateCallback audioTrackStateCallback;

    public void initPresenter(@Nullable OnAudioTrackStateCallback audioTrackStateCallback){
        this.audioTrackStateCallback = audioTrackStateCallback;
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,CHANNEL,AUDIO_FORMAT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,SAMPLE_RATE,CHANNEL,AUDIO_FORMAT,bufferSize,AudioTrack.MODE_STREAM);
        if(audioTrack.getState() == AudioTrack.STATE_INITIALIZED){
            if(audioTrackStateCallback!=null){
                audioTrackStateCallback.initFailed();
            }
            throw new IllegalStateException("Failed to initialize AudioTrack");
        }
    }

    /**
     * 初始化AudioTrack
     * @param streamType 音频流的类型
     * @param sampleRateInHz 采样率
     * @param channelConfig 通道数
     * @param audioFormat 音频数据的格式
     * @param mode 播放模式：流处理或者静态缓冲区
     */
    public void initPresenter(int streamType, int sampleRateInHz, int channelConfig, int audioFormat,int mode,@Nullable OnAudioTrackStateCallback audioTrackStateCallback){
        this.audioTrackStateCallback = audioTrackStateCallback;
        int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,channelConfig,audioFormat);
        audioTrack = new AudioTrack(streamType,sampleRateInHz,channelConfig,audioFormat,bufferSize,mode);
        if(audioTrack.getState() == AudioTrack.STATE_INITIALIZED){
            if(audioTrackStateCallback!=null){
                audioTrackStateCallback.initFailed();
            }
            throw new IllegalStateException("Failed to initialize AudioTrack");
        }
    }

    /**
     * 设置播放文件路径
     * @param context 上下文
     * @param filePath PCM音频文件的在assets目录或者Android本地目录的全路径
     * @param isAssetResource 是否为assets目录下的文件
     */
    public void setPlayFilePath(Context context, String filePath,boolean isAssetResource){
        InputStream inputStream = null;
        if(isAssetResource){
            AssetManager assetManager = context.getAssets();
            try {
                inputStream = assetManager.open(filePath);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else {
            try {
                inputStream =  new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        bufferedInputStream = new BufferedInputStream(inputStream);
    }

    /**
     * 开始播放音频
     * @param length 每次送入AudioTrack的音频长度
     * @param isPcmFile 是否为PCM格式的音频
     */
    public void startPresenter(int length,boolean isPcmFile){
        stopPresenter();
        audioTrack.play();
        playInfoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[length];
                int index = 0;
                while (!playInfoThread.isInterrupted()){
                    try {
                        while (bufferedInputStream.read(buffer) !=-1){
                            index++;
                            if(!isPcmFile && index == 1){
                                audioTrack.write(buffer,44,buffer.length);
                            }else {
                                audioTrack.write(buffer,0,buffer.length);
                            }
                        }
                        if(audioTrackStateCallback!=null){
                            audioTrackStateCallback.playFinished();
                        }
                        break;
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        playInfoThread.start();
    }

    /**
     * 停止播放
     */
    public void stopPresenter(){
        if (audioTrack != null) {
            if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop();
            }
        }
        if(playInfoThread!=null){
            playInfoThread.interrupt();
            playInfoThread = null;
        }
    }

    public void releasePresenter(){
        stopPresenter();
        audioTrack.release();
        audioTrack = null;
        audioTrackStateCallback = null;
    }

}
