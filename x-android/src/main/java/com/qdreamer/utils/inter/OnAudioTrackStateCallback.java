package com.qdreamer.utils.inter;

/**
 * @author XJun
 * @date 2024/1/29 11:55
 **/
public interface OnAudioTrackStateCallback {
    /**
     * AudioTrack初始化失败回调
     */
    void initFailed();

    /**
     * AudioTrack 播放完成回调
     */
    void playFinished();

}
