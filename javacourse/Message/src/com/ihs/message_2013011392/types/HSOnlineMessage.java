package com.ihs.message_2013011392.types;

import org.json.JSONObject;

/**
 * 在线消息类：在线消息是一类特殊的消息，只有收发双方都在线才能发送成功
 */
public class HSOnlineMessage {

    private String mid;
    private JSONObject content;
    
    /**
     * 在线消息的构造方法
     * 
     * @param mid 消息接收者的 mid
     * @param content 自定义 JSONObject，用于传递任意信息
     */
    public HSOnlineMessage(String mid, JSONObject content) {
        this.mid = mid;
        this.content = content;
    }

    /**
     * 获得 mid
     * 
     * @return 对于发送的消息，在构造方法中将 mid 设置为为消息接收者的 mid；对于接收到的消息，此方法返回消息发送者的 mid
     */
    public String getMid() {
        return mid;
    }

    public JSONObject getContent() {
        return content;
    }
}
