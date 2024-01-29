package com.qdreamer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.qdreamer.utils.inter.OnMediaPlayerStateCallback;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author XJun
 * @date 2023/9/20 11:03
 **/
public class MediaPlayerHelper implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    @SuppressLint("StaticFieldLeak")
    private static final MediaPlayerHelper mediaPlayerHelper = new MediaPlayerHelper();
    private MediaPlayer mediaPlayer = null;
    private Context context;
    private ExecutorService playInfoThread;
    private OnMediaPlayerStateCallback onMediaPlayerStateCallback;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private boolean isOccupied = false;


    public static MediaPlayerHelper getInstance(){
        return mediaPlayerHelper;
    }

    private MediaPlayerHelper(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        playInfoThread = Executors.newCachedThreadPool();
        playInfoThread.execute(() -> {
            while (true){
                if(onMediaPlayerStateCallback!=null&& mediaPlayer.isPlaying()){
                    onMediaPlayerStateCallback.playInfo(getDuration(),getCurrentPosition());
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("ServiceCast")
    public void setOnMediaPlayerStateCallback(OnMediaPlayerStateCallback onMediaPlayerStateCallback, Context context){
        this.context = context;
        this.onMediaPlayerStateCallback = onMediaPlayerStateCallback;
        //监听音频播放焦点
        if(audioManager==null){
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        if(onAudioFocusChangeListener == null){
            onAudioFocusChangeListener = focusChange -> {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        //播放操作
                        if(isOccupied){
                            startPlay();
                            isOccupied = false;
                        }
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        //暂停操作
                        if(mediaPlayer.isPlaying()){
                            pausePlay();
                            isOccupied = true;
                        }
                        break;
                    default:
                        break;
                }
            };
        }
    }

    /**
     * 获取Assets目录下的文件和文件夹，并将其转换为list  context.getAssets().list("");
     * @param path 本地文件路径 或者 Assets目录下的文件
     * @param isAssetResource 是否为Assets目录下的文件
     */
    public void setMusicPath(String path, boolean isAssetResource){
        if(mediaPlayer!=null){
            mediaPlayer.reset();
            try {
                if(!isAssetResource){
                    mediaPlayer.setDataSource(path);
                }else {
                    AssetFileDescriptor fileDescriptor = context.getAssets().openFd(path);
                    mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
                }
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startPlay(){
        if(mediaPlayer!=null){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }
    }

    /**
     * @param isLooping 是否循环播放
     */
    public void setLooping(boolean isLooping){
        if(mediaPlayer!=null){
            mediaPlayer.setLooping(isLooping);
        }
    }

    /**
     * @return 播放状态
     */
    public boolean isPlaying(){
        if(mediaPlayer!=null){
            return mediaPlayer.isPlaying();
        }else {
            return false;
        }
    }

    /**
     * 设置声音
     */
    public void setVolume(float leftVolume,float rightVolume){
        if(mediaPlayer != null){
            mediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    /**
     * 跳转到指定的位置
     * @param msec 毫米
     */
    public void seekTo(int msec){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(msec);
        }
    }

    /**
     * @return 播放文件总时间
     */
    public int getDuration(){
        if(mediaPlayer!=null){
         return  mediaPlayer.getDuration();
        }else {
            return 0;
        }
    }

    /**
     * @return 当前播放位置
     */
    public int getCurrentPosition(){
        if(mediaPlayer!=null){
            return  mediaPlayer.getCurrentPosition();
        }else {
            return 0;
        }
    }

    public void pausePlay(){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void stopPlay(){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseMediaPlayer(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if(playInfoThread !=null && !playInfoThread.isShutdown()){
            playInfoThread.shutdownNow();
            playInfoThread = null;
        }
        releaseTheAudioFocus();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完成回调，当isLooping = false 和 播放完成时回调
        if(onMediaPlayerStateCallback!=null){
            onMediaPlayerStateCallback.onCompletion(mp);
        }
        releaseTheAudioFocus();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //播放错误回调
        if(onMediaPlayerStateCallback!=null){
            onMediaPlayerStateCallback.onError(mp, what, extra);
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //prepareAsync() 异步回调
    }

    //暂停、播放完成或退到后台释放音频焦点
    public void releaseTheAudioFocus() {
        if (audioManager != null && onAudioFocusChangeListener != null) {
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }
}
