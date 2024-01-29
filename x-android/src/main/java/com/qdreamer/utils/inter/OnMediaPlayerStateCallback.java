package com.qdreamer.utils.inter;

import android.media.MediaPlayer;

/**
 * @author XJun
 * @date 2023/9/20 16:54
 **/
public interface OnMediaPlayerStateCallback {
    /**
     * 播放完成回调
     * @param mp
     */
    public void onCompletion(MediaPlayer mp);

    /**
     * 播放错误回调
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    public void onError(MediaPlayer mp, int what, int extra);

    /**
     * 播放时长回调
     * @param totalTime 播放文件的总时长
     * @param currentTime 当前的播放位置
     */
    public void playInfo(int totalTime,int currentTime);
}
