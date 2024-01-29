package com.qdreamer.qvoice;

/**
 * @author XJun
 * @date 2023/11/16 17:36
 **/
public interface IQVoicePresenter {
    /**
     * 设置 QSession 和 QEngine 的监听器
     */
    void setOnQVoiceCallback(OnQVoiceCallback listener);

    /**
     * 初始化 QSession 和 相关的 QEngine
     */
    void initQEngine();

    /**
     * 启动 QEngine SDK
     */
    boolean startQEngine();

    /**
     * 启动之后往 QEngine 当中传输音频数据
     */
    void feedQEngine(byte[] audio);

    /**
     * 停止 QEngine SDK
     */
    boolean stopQEngine();

    /**
     * 销毁释放资源
     */
    void releaseQEngine();

}
