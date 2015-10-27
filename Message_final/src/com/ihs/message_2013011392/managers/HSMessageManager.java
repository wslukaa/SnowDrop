package com.ihs.message_2013011392.managers;

import java.util.List;

import android.os.Handler;

import com.ihs.commons.utils.HSError;
import com.ihs.message_2013011392.managers.HSMessageChangeListener.HSMessageChangeType;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSOnlineMessage;

public class HSMessageManager {
    private static HSMessageManager sInstance = null;
    private MessageManager mgr = null;

    /**
     * 数据库查询的结果类，其目的是为了支持分段查询，例如先查询和一个人最近的 10 条消息显示在界面上，当用户在聊天界面滑至顶端家在消息时再根据结果中的 cursor 查询这 10 条前的消息
     */
    public static class QueryResult {
        private List<HSBaseMessage> messages;
        private long cursor;

        public QueryResult(List<HSBaseMessage> messages, long cursor) {
            super();
            this.messages = messages;
            this.cursor = cursor;
        }

        /**
         * 返回查询到的消息
         * 
         * @return 查询到的消息，可能有多条，放在 List 中
         */
        public List<HSBaseMessage> getMessages() {
            return messages;
        }

        /**
         * 返回查询游标
         * 
         * @return 查询结束时的游标位置，下次查询时传入上次查询获得的游标，可以接着上次的结果继续查询
         */
        public long getCursor() {
            return cursor;
        }
    }

    /**
     * 获取 HSMessageManager 的实例，单例模式，每次返回的都是同一个实例
     * 
     * @return HSMessageManager 的一个实例
     */
    public static HSMessageManager getInstance() {
        if (sInstance == null) {
            sInstance = new HSMessageManager();
        }
        return sInstance;
    }

    /**
     * 发送消息
     * 
     * @param message 发送的消息，可调用各个类型消息 HSXXXMessage 的构造方法获得
     * @param callback 发送消息后的回调接口，请实现其 onMessageSentFinished(message, success, error) 方法来处理消息发送完毕的事件
     * @param handler 处理回调所用的 Java 消息队列，传入 new Handler() 得到的 Handler 实例即可
     */
    public void send(HSBaseMessage message, SendMessageCallback callback, Handler handler) {
        mgr.send(message, callback, handler);
    }

    /**
     * 发送消息后的回调接口
     */
    public static interface SendMessageCallback {
        /**
         * 发送消息完毕时被调用的回调方法
         * 
         * @param message 传回所发送的消息
         * @param success 发送是否成功，发送成功为 true，失败为 false
         * @param error 发送失败时的错误信息
         */
        void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error);
    }

    /**
     * 发送在线消息，在线消息与普通消息不同，采用 “发出即不管” 策略，可用于制作即时小游戏等附加应用，接收方实现 HSMessageChangeListener 的 onOnlineMessageReceived()
     * 方法处理接收到的在线消息
     * 
     * (1) 不通过消息服务器，直接通过长连接服务器发送，双方必须同时在线才能发送成功 (2) 不存入数据库，发送完直接丢弃，无发送回调方法，不保证消息发送成功
     * 
     * @param message 发送的在线消息，可调用 HSOnlineMessage 的构造方法获得，其 content 为 JSONObject，可以任意定制，实现双方之间有趣的交互逻辑
     */
    public void sendOnlineMessage(HSOnlineMessage message) {
        mgr.sendOnlineMessage(message);
    }

    /**
     * 在数据库中查询和一个人相关的所有消息，即此人为发送者或接收者的消息
     * 
     * @param relatedTo 被查询的人的 mid，mid 用于唯一地确定一个帐户
     * @param count 希望获取的消息数量限制，传入 0 会返回与被查询的 mid 相关的全部消息
     * @param cursor 上次查询返回的游标，用于连续地批量查询消息；首次查询时传入 -1 即可
     * @return 查询结果，调用返回的结果类的 getMessages() 和 getCursor() 方法，可以获得查询到的消息（无消息时返回空列表），及用于下次继续查询的游标
     */
    public QueryResult queryMessages(String relatedTo, int count, long cursor) {
        return mgr.queryMessages(relatedTo, count, cursor);
    }

    /**
     * 在数据库中查询指定 Message ID 的消息
     * 
     * @param msgID 要查询的消息的 Message ID
     * @return 查询结果，直接以 HSBaseMessage 的形式返回，被查询的消息不存在时返回 null
     */
    public HSBaseMessage queryMessage(String msgID) {
        return mgr.queryMessage(msgID);
    }

    /**
     * 查询来自一个人的消息中未读消息的数量
     * 
     * @param mid 要查询的人的 mid
     * @return 该人发来的消息中，未读消息的数量
     */
    public int queryUnreadCount(String mid) {
        return mgr.queryUnreadCount(mid);
    }

    /**
     * 删除和一个人相关的所有消息，即此人为发送者或接收者的消息
     * 
     * @param mid 要删除消息的人的 mid
     */
    public void deleteMessages(String mid) {
        mgr.deleteMessages(mid);
    }

    /**
     * 删除指定的消息
     * 
     * @param messages 由待删除的消息对象组成的列表
     */
    public void deleteMessages(List<HSBaseMessage> messages) {
        mgr.deleteMessages(messages);
    }

    /**
     * 标记一个人发来的所有消息为已读
     * 
     * @param mid 要标记的人的 mid
     */
    public void markRead(String mid) {
        mgr.markRead(mid);
    }

    /**
     * 标记指定的消息为已读
     * 
     * @param messages 由待标记的消息对象组成的列表
     */
    public void markRead(List<HSBaseMessage> messages) {
        mgr.markRead(messages);
    }

    /**
     * 标记指定的多媒体消息附带的媒体文件为已读
     * 
     * @param messages 由待标记的多媒体消息对象组成的列表，多媒体消息包括图片消息和音频消息
     */
    public void markMediaRead(List<HSBaseMessage> messages) {
        mgr.markMediaRead(messages);
    }

    /**
     * 触发去服务器获取消息，当收到来自长连接的推送消息（act = msg）时调用即可，其他情况不需要调用
     */
    public void pullMessages() {
        mgr.pullMessages();
    }

    /**
     * 添加消息变化监听者，可在 Activity 的 onCreate() 方法中调用
     * 
     * @param listener 监听者，该对象的类应当实现了 HSMessageChangeListener 接口
     * @param handler 处理变化事件所用的 Java 消息队列，传入 new Handler() 得到的 Handler 实例即可
     */
    public void addListener(HSMessageChangeListener listener, Handler handler) {
        mgr.addListener(listener, handler);
    }

    /**
     * 移除消息变化监听者，可在 Activity 的 onDestroy() 方法中调用
     * 
     * @param listener 监听者，如果该监听者目前不处在监听列表中，将什么也不做
     */
    public void removeListener(HSMessageChangeListener listener) {
        mgr.removeListener(listener);
    }

    /**
     * 删除数据库中当前登录用户的所有消息
     */
    public void deleteAllMessages() {
        mgr.deleteAllMessages();
    }

    /**
     * 内部方法，查询指定 Message ID 的消息是否正在发送过程中
     * 
     * @param msgID 查询的消息的 Message ID
     * @return 正在发送返回 true，否则返回 false
     */
    public boolean isSendingMessage(String msgID) {
        return mgr.isSendingMessage(msgID);
    }

    /**
     * 私有构造方法，仅由 getInstance() 调用，获取 HSMessageManager 的实例请调用 getInstance() 方法
     */
    private HSMessageManager() {
        mgr = new MessageManager();
    }

    MessageManager getManager() {
        return mgr;
    }

    /**
     * 内部方法，用于通知消息变化，用户不应调用此方法
     */
    void notifyMessageChange(HSMessageChangeType type, List<HSBaseMessage> messages) {
        mgr.notifyMessageChange(type, messages);
    }
}
