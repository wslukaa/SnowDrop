package com.ihs.message_2013011392.managers;

import java.util.List;

import org.json.JSONObject;

import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSOnlineMessage;

/**
 * 消息变化监听接口
 */
public interface HSMessageChangeListener {
    /**
     * 消息变化类型
     */
    public static enum HSMessageChangeType {
        ADDED, // 有新消息到达
        DELETED, // 有消息被删除
        UPDATED // 有消息状态发生变化：正在发送、已发送、发送失败、正在下载、下载失败、下载成功这些状态变更时
    }

    /**
     * 有消息发生变化时被调用
     * 
     * @param changeType 变化种类，消息增加 / 消息删除 / 消息状态变化
     * @param messages 变化涉及的消息对象
     */
    void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages);

    /**
     * 收到 “正在输入” 消息时被调用
     * 
     * @param fromMid “正在输入” 消息发送者的 mid
     */
    void onTypingMessageReceived(String fromMid);

    /**
     * 收到在线消息时被调用
     * 
     * @param message 收到的在线消息，message.getMid() 返回在线消息的发送者的 mid，content 值由用户定制，可实现自己的通讯协议和交互逻辑
     */
    void onOnlineMessageReceived(HSOnlineMessage message);

    /**
     * 当来自某人的消息中，未读消息数量发生变化时被调用
     * 
     * @param mid 对应人的 mid
     * @param newCount 变化后的未读消息数量
     */
    void onUnreadMessageCountChanged(String mid, int newCount);

    /**
     * 当收到服务器通过长连接发送过来的推送通知时被调用，用途是进行新消息在通知窗口的通知，通知格式如下： alert 项为提示文字，fmid 代表是哪个 mid 发来的消息
     * {"act":"msg","aps":{"alert":"@: sent to a message","sound":"push_audio_1.wav","badge":1},"fmid":"23"}
     * 
     * @param pushInfo 收到通知的信息
     */
    void onReceivingRemoteNotification(JSONObject pushInfo);
}
