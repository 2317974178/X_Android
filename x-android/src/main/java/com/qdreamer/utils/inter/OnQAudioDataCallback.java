package com.qdreamer.utils.inter;

import android.annotation.SuppressLint;

/**
 * @author XJun
 * @date 2023/12/4 10:08
 **/
public interface OnQAudioDataCallback {
    /**
     * QAudio 录音数据回调
     * @param data 音频数据
     */
    void OnQAudioDataCallback(byte[] data);
}
