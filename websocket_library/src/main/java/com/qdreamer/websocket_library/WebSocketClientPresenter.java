package com.qdreamer.websocket_library;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author XJun
 * @date 2024/1/29 17:53
 **/
public class WebSocketClientPresenter extends WebSocketClient {

    private WebSocketCallback webSocketCallback;

    public WebSocketClientPresenter(URI serverUri) {
        super(serverUri);
    }

    public WebSocketClientPresenter(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    public WebSocketClientPresenter(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public WebSocketClientPresenter(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
        super(serverUri, protocolDraft, httpHeaders);
    }

    public WebSocketClientPresenter(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    public void setWebSocketCallback(WebSocketCallback webSocketCallback){
        this.webSocketCallback = webSocketCallback;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if(webSocketCallback != null){
            webSocketCallback.onOpen(handshakedata);
        }
    }

    @Override
    public void onMessage(String message) {
        if(webSocketCallback != null){
            webSocketCallback.onMessage(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(webSocketCallback != null){
            webSocketCallback.onClose(code, reason, remote);
        }
    }

    @Override
    public void onError(Exception ex) {
        if(webSocketCallback != null){
            webSocketCallback.onError(ex);
        }
    }

    /**
     * 发送消息
     * @param message 字符串
     */
    public void sendMessage(String message){
        this.send(message);
    }

    /**
     * 发送消息
     * @param message byte数组
     */
    public void senMessage(byte[] message){
        this.send(message);
    }

    /**
     * 发送消息
     * @param message ByteBuffer
     */
    public void senMessage(ByteBuffer message){
        this.send(message);
    }

    public void connectToService(){
        this.connect();
    }

    public void disconnect(){
        this.close();
    }

}
