package com.qdreamer.qvoice;

/**
 * @author XJun
 * @date 2023/11/16 17:36
 **/
public interface OnQVoiceCallback {
    /**
     * [type] 回调消息类型
     * [data] 对应消息类型的真实数据
     */
    void onQVoiceCallback(byte type, byte[] data);
}
