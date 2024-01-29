package com.qdreamer.websocket_library;

import org.java_websocket.handshake.ServerHandshake;

/**
 * @author XJun
 * @date 2024/1/29 17:56
 **/
public interface WebSocketCallback {
    /**
     * WebSocket连接成功
     * @param handshakedata 实例的握手
     */
    public void onOpen(ServerHandshake handshakedata);

    /**
     * 接受服务器的消息
     * @param message 消息
     */
    public void onMessage(String message);

    /**
     * 关闭连接
     * @param code 代码可以在此处查找
     * @param reason 附加信息
     * @param remote 返回连接关闭是否由远程主机启动。
     */
    public void onClose(int code, String reason, boolean remote);

    /**
     * 连接错误
     * @param ex 错误信息
     */
    public void onError(Exception ex);
}
