package com.ihs.message_2013011392.types;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.message_2013011392.utils.Utils;

/**
 * 语音消息类
 */
public class HSTextMessage extends HSBaseMessage {

    private String text;

    /**
     * 获得消息内容
     * 
     * @return 消息内容
     */
    public String getText() {
        return text;
    }

    /**
     * 设置消息内容
     * 
     * @param text 消息内容
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 文本消息的构造方法
     * 
     * @param to 消息接收者的 mid
     * @param text 消息内容
     */
    public HSTextMessage(String to, String text) {
        super(HSMessageType.TEXT, null, 0, null, HSBaseMessage.PUSH_TAG_TEXT, true, HSAccountManager.getInstance().getMainAccount().getMID(), to, new Date(HSAccountManager
                .getInstance().getServerTime()), HSBaseMessage.HSMessageStatus.SENDING, HSBaseMessage.HSMessageMediaStatus.DOWNLOADED, 1);
        JSONObject content = new JSONObject();
        try {
            content.put(Constants.BODY, text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.setContent(content);
        this.text = text;
        setMsgID(Utils.getOneUUID());
    }

    public HSTextMessage(Cursor c) {
        super(c);
    }

    public HSTextMessage(JSONObject info) {
        super(info);
    }

    @Override
    public void initMessageSpecialProperties() {
        this.text = getContent().optString(Constants.BODY, null);
    }

    @Override
    public String toString() {
        return super.toString() + " text: = " + text;
    }
}
