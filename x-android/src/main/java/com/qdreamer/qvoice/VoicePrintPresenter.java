package com.qdreamer.qvoice;

import com.qdreamer.qvoice.IQVoicePresenter;

/**
 * @author XJun
 * @date 2023/12/7 11:16
 **/
public interface VoicePrintPresenter extends IQVoicePresenter {

    /**
     * 设置声纹参数
     * @param param
     */
    void setQEngine(String param);

    /**
     * 获取识别得分
     */
    float getProbQEngine();
}
