package com.ihs.message_2013011392.types;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.commons.connection.HSServerAPIConnection;
import com.ihs.message_2013011392.managers.HSMessageManager;
import com.ihs.message_2013011392.managers.MessageDBManager;
import com.ihs.message_2013011392.utils.Utils;

/**
 * 消息的基类
 * 
 * 文本、语音、图片、位置消息均继承此类（在线消息 HSOnlineMessage 除外）
 */
/**
 * @author songliu
 *
 */
/**
 * @author songliu
 *
 */
public class HSBaseMessage implements Comparable<HSBaseMessage> {

    /*
     * 消息状态
     */
    public static enum HSMessageStatus {
        /**
         * 正在发送
         */
        SENDING(0),
        /**
         * 已发送
         */
        SENT(1),
        /**
         * 发送失败
         */
        FAILED(2),
        /**
         * 消息自己未读
         */
        UNREAD(3),
        /**
         * 消息已被对方收到（尚未实现）
         */
        RECEIVED(4),
        /**
         * 消息自己已读
         */
        READ(5);

        private int value = -1;

        private HSMessageStatus(int value) {
            this.value = value;
        }

        static public HSMessageStatus valueOf(int value) {
            switch (value) {
                case 0:
                    return SENDING;
                case 1:
                    return SENT;
                case 2:
                    return FAILED;
                case 3:
                    return UNREAD;
                case 4:
                    return RECEIVED;
                case 5:
                    return READ;
            }
            return null;
        }

        public int getValue() {
            return this.value;
        }
    }

    /*
     * 消息媒体文件状态
     * 
     * 媒体文件是指音频、图片消息所附带的文件
     */
    /**
     * @author songliu
     *
     */
    public static enum HSMessageMediaStatus {
        /**
         * 尚未下载
         */
        TO_DOWNLOAD(0),
        /**
         * 正在下载
         */
        DOWNLOADING(1),
        /**
         * 已经下载
         */
        DOWNLOADED(2),
        /**
         * 下载失败
         */
        FAILED(3);

        private int value = -1;

        private HSMessageMediaStatus(int value) {
            this.value = value;
        }

        public static HSMessageMediaStatus valueOf(int value) {
            switch (value) {
                case 0:
                    return TO_DOWNLOAD;
                case 1:
                    return DOWNLOADING;
                case 2:
                    return DOWNLOADED;
                case 3:
                    return FAILED;
            }
            return FAILED;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static final String PUSH_TAG_TEXT = "text";
    public static final String PUSH_TAG_NEARBY = "nearby";
    public static final String PUSH_TAG_MINICHAT = "mini-chat";
    public static final String PUSH_TAG_AUDIO = "audio";
    public static final String PUSH_TAG_IMAGE = "image";
    public static final String PUSH_TAG_PRIVATENOTE = "private-note";
    public static final String PUSH_TAG_STICKER = "sticker";
    public static final String PUSH_TAG_LIKEPLUS = "like-plus";
    public static final String PUSH_TAG_LOCATION = "location";
    public static final String PUSH_TAG_LINK_WEB = "link-web";
    public static final String PUSH_TAG_LINK_ACTIVITY = "link-activity";
    public static final String PUSH_TAG_VIDEO = "video";
    public static final String PUSH_TAG_VCARD = "vcard";
    public static final String PUSH_TAG_SHAKE = "shake";
    public static final String PUSH_TAG_FILE = "file";

    private HSMessageType type;
    private HSMessageStatus status;
    private int intMediaStatus;
    private String scenario;
    private JSONObject userInfo;
    private JSONObject content;
    private JSONObject extra;
    private JSONObject localFileInfo;
    private long msgServerID;
    private String msgID;
    private String pushTag;
    private String from;
    private String to;
    private Date timestamp;
    private boolean isMediaRead;
    private boolean isMessageOriginate;
    private boolean isRejected;
    private double downloadProgress;
    private boolean goHttp;

    public HSBaseMessage(HSMessageType type, JSONObject content, long msgServerID, String msgID, String pushTag, boolean isMessageOriginate, String from, String to,
            Date timestamp, HSMessageStatus status, HSMessageMediaStatus mediaStatus, double downloadProgress) {
        super();
        this.type = type;
        this.content = content;
        this.msgServerID = msgServerID;
        this.pushTag = pushTag;
        this.isMessageOriginate = isMessageOriginate;
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.status = status;
        this.intMediaStatus = mediaStatus.getValue();
        this.downloadProgress = downloadProgress;
    }

    public HSBaseMessage(JSONObject info) {
        super();
        String typeString = info.optString(Constants.TYPE);
        JSONObject con = info.optJSONObject(Constants.CONTENT);
        this.content = info.optJSONObject(Constants.CONTENT);
        this.type = Utils.getMessageType(typeString);
        this.pushTag = info.optString(Constants.PUSH_TAG);
        this.to = info.optString(Constants.TO_MID);
        this.from = info.optString(Constants.FROM_MID);
        this.msgServerID = info.optLong(Constants.MSG_SERVER_ID, 0);
        this.msgID = con == null ? null : con.optString("msg_c_id"); // this is a special case
        this.extra = con == null ? null : con.optJSONObject(Constants.EXTRA);
        this.isMessageOriginate = false;
        this.status = HSMessageStatus.UNREAD;
        this.intMediaStatus = HSMessageMediaStatus.TO_DOWNLOAD.getValue();
        this.downloadProgress = 0;
        this.isRejected = info.optBoolean(Constants.REJECTED, false);
        this.timestamp = new Date(info.optLong(Constants.TIMESTAMP));
        this.isMediaRead = false; // TODO: get configuration
        this.scenario = info.optString(Constants.SCENARIO);
        this.initMessageSpecialProperties();
    }

    public HSBaseMessage(Cursor cursor) {
        super();
        try {
            String contentStr = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_CONTENT));
            this.content = TextUtils.isEmpty(contentStr) ? null : new JSONObject(contentStr);
            String localFileInfoStr = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_LOCAL_FILE_INFO));
            this.from = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_FROM));
            this.to = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_TO));
            this.localFileInfo = TextUtils.isEmpty(localFileInfoStr) ? null : new JSONObject(localFileInfoStr);
            this.msgID = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_CID));
            this.type = Utils.getMessageType(cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_TYPE)));
            this.msgServerID = cursor.getLong(cursor.getColumnIndex(MessageDBManager.COLUMN_SID));
            this.pushTag = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_PUSH_TAG));
            this.isMessageOriginate = cursor.getInt(cursor.getColumnIndex(MessageDBManager.COLUMN_MO)) == 1;
            this.timestamp = new Date(cursor.getLong(cursor.getColumnIndex(MessageDBManager.COLUMN_TIMESTAMP)));
            this.isMediaRead = cursor.getInt(cursor.getColumnIndex(MessageDBManager.COLUMN_IS_MEDIA_READ)) == 1;
            this.intMediaStatus = cursor.getInt(cursor.getColumnIndex(MessageDBManager.COLUMN_MEDIA_STATUS));
            this.downloadProgress = getMediaStatus() == HSMessageMediaStatus.DOWNLOADED ? 1 : 0;
            this.status = HSMessageStatus.valueOf(cursor.getInt(cursor.getColumnIndex(MessageDBManager.COLUMN_STATUS)));
            JSONObject ex = content.optJSONObject(Constants.EXTRA);
            this.extra = ex == null ? new JSONObject() : ex;
            this.isRejected = cursor.getInt(cursor.getColumnIndex(MessageDBManager.COLUMN_REJECTED)) == 1;
            this.scenario = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_SCENARIO));
            this.to = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_TO));
            this.from = cursor.getString(cursor.getColumnIndex(MessageDBManager.COLUMN_FROM));
            this.initMessageSpecialProperties();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initMessageSpecialProperties() {
        // TODO: Invoke it
    }

    /**
     * 获取消息的 extra 信息
     * 
     * @return 消息的 extra 信息，为用户自定义的任意格式 JSONObject，可以用来传递附加信息（如互相对话时消息气泡的样式等）
     */
    public JSONObject getExtra() {
        return extra;
    }

    /**
     * 设置消息的 extra 信息
     * 
     * @param extras 设置的 extra 信息，为自定义的任意格式 JSONObject，可以用来传递附加信息（如互相对话时消息气泡的样式等）
     */
    public void setExtra(JSONObject extras) {
        this.extra = extras;
    }

    public long getMsgServerID() {
        return msgServerID;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public JSONObject getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(JSONObject userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 获取消息的类型：文本、语音、图片、位置
     * 
     * @return 消息类型
     */
    public HSMessageType getType() {
        return type;
    }

    public void setType(HSMessageType type) {
        this.type = type;
    }

    public void setGoHttp(boolean goHttp) {
        this.goHttp = goHttp;
    }

    public boolean getGoHttp() {
        return this.goHttp;
    }

    public HSMessageMediaStatus getMediaStatus() {
        return HSMessageMediaStatus.valueOf(intMediaStatus & 0x03);
    }

    public void setMediaStatus(HSMessageMediaStatus mediaStatus) {
        this.intMediaStatus = mediaStatus.value;
    }

    public void setMediaStatusBackend(int intMediaStatus) {
        this.intMediaStatus = intMediaStatus;
    }

    public int getMediaStatusBackend() {
        return this.intMediaStatus;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public void setMsgServerID(long msgServerID) {
        this.msgServerID = msgServerID;
    }

    /**
     * 获取消息的唯一 ID，在消息进行比较的时候可以用这个来判断两个消息是否为同一个条消息
     * 
     * @return 消息唯一 ID
     */
    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getPushTag() {
        return pushTag;
    }

    public void setPushTag(String pushTag) {
        this.pushTag = pushTag;
    }

    /**
     * 获取消息发送者的 mid
     * 
     * @return
     */
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * 获取消息的接收者的 mid
     * 
     * @return
     */
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    /**
     * 消息的时间戳，消息显示的使用这个时间戳进行新老排序
     * 
     * @return
     */
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMediaRead() {
        return isMediaRead;
    }

    public void setMediaRead(boolean isMediaRead) {
        this.isMediaRead = isMediaRead;
    }

    public boolean isMessageOriginate() {
        return isMessageOriginate;
    }

    public void setMessageOriginate(boolean isMessageOriginate) {
        this.isMessageOriginate = isMessageOriginate;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean isRejected) {
        this.isRejected = isRejected;
    }

    public double getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(double downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public void setStatus(HSMessageStatus status) {
        this.status = status;
    }

    /**
     * 获取消息的状态：正在发送、发送成功、发送失败等
     * 
     * @return
     */
    public HSMessageStatus getStatus() {
        if (this.status == HSMessageStatus.FAILED) {
            return HSMessageManager.getInstance().isSendingMessage(msgID) ? HSMessageStatus.SENDING : this.status;
        } else
            return this.status;
    }

    public JSONObject getLocalFileInfo() {
        return this.localFileInfo;
    }

    public void setLocalFileInfo(JSONObject localFileInfo) {
        this.localFileInfo = localFileInfo;
    }

    /**
     * 获取消息的聊天者，根据自己是否为消息的发起者来判断
     * 
     * @return
     */
    public String getChatterMid() {
        return isMessageOriginate ? this.to : this.from;
    }

    public String getTypeString() {
        return Utils.getMessageTypeString(this.type);
    }

    public JSONObject getDataBody() {
        JSONObject json = new JSONObject();
        String mid = HSAccountManager.getInstance().getMainAccount().getMID();
        String sessionID = HSAccountManager.getInstance().getMainAccount().getSessionID();
        int appID = HSAccountManager.getInstance().getAppID();

        try {
            json.put("mid", mid);
            json.put("app_id", appID);
            json.put("sesn_id", sessionID);
            json.put(Constants.TO_MID, this.to);
            json.put(Constants.TYPE, getTypeString());
            if (TextUtils.isEmpty(this.pushTag) == false) {
                json.put(Constants.PUSH_TAG, this.pushTag);
            }
            if (TextUtils.isEmpty(this.scenario) == false) {
                json.put(Constants.SCENARIO, this.scenario);
            }
            if (this.extra != null) {
                content.put(Constants.EXTRA, this.extra);
            }
            content.put(Constants.MSG_CLIENT_ID, this.msgID);
            if (content != null && content.length() > 0) {
                json.put(Constants.CONTENT, content);
            }
            if (this.userInfo != null) {
                json.put(Constants.USER_INFO, this.userInfo);
            }
        } catch (JSONException e) {
        }
        return json;
    }

    public ContentValues getDBInfo() {
        ContentValues cv = new ContentValues();
        if (this.msgServerID != 0) {
            cv.put(MessageDBManager.COLUMN_SID, msgServerID);
        }
        cv.put(MessageDBManager.COLUMN_CID, msgID);
        cv.put(MessageDBManager.COLUMN_TYPE, Utils.getMessageTypeString(this.type));
        if (TextUtils.isEmpty(pushTag) == false) {
            cv.put(MessageDBManager.COLUMN_PUSH_TAG, pushTag);
        }
        if (TextUtils.isEmpty(scenario) == false) {
            cv.put(MessageDBManager.COLUMN_SCENARIO, scenario);
        }
        cv.put(MessageDBManager.COLUMN_MO, this.isMessageOriginate);
        cv.put(MessageDBManager.COLUMN_FROM, this.from);
        cv.put(MessageDBManager.COLUMN_TO, this.to);
        if (this.extra != null) {
            try {
                content.put(Constants.EXTRA, extra);
            } catch (JSONException e) {
            }
        }
        cv.put(MessageDBManager.COLUMN_CONTENT, this.content.toString());
        cv.put(MessageDBManager.COLUMN_TIMESTAMP, this.timestamp.getTime());
        cv.put(MessageDBManager.COLUMN_STATUS, this.status.getValue());
        cv.put(MessageDBManager.COLUMN_MEDIA_STATUS, this.intMediaStatus);
        cv.put(MessageDBManager.COLUMN_IS_MEDIA_READ, this.isMediaRead);
        cv.put(MessageDBManager.COLUMN_REJECTED, this.isRejected);
        if (this.getLocalFileInfo() != null) {
            cv.put(MessageDBManager.COLUMN_LOCAL_FILE_INFO, this.getLocalFileInfo().toString());
        }
        return cv;
    }

    @Override
    public String toString() {
        return "type: " + this.getType() + " from: " + this.getFrom() + " to: " + getTo() + " msgID: " + this.msgID;
    }

    public HSServerAPIConnection getServerAPIRequest() {
        return null;
    }

    public void deleteAllMediaFiles() {
    }

    @Override
    public int compareTo(HSBaseMessage another) {
        return (int) (this.getTimestamp().getTime() - another.getTimestamp().getTime());
    }
}
