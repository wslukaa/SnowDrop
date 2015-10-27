package com.ihs.message_2013011392.types;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.text.TextUtils;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.message_2013011392.utils.Utils;

/**
 * 位置消息类
 */
public class HSLocationMessage extends HSBaseMessage {

    private double latitude; // 纬度
    private double longitude; // 经度
    private String description; // 地理位置描述（如 “北京市海淀区优盛大厦 (u-center) iHandySoft 公司”）

    /**
     * 获得纬度
     * 
     * @return 纬度
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * 设置纬度
     * 
     * @param latitude 纬度
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * 获得经度
     * 
     * @return 经度
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * 设置经度
     * 
     * @param longitde 经度
     */
    public void setLongitude(double longitde) {
        this.longitude = longitde;
    }

    /**
     * 获得地理位置描述
     * 
     * @return 地理位置描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置地理位置描述
     * 
     * @param description 地理位置描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 位置消息的构造方法
     * 
     * @param to 消息接收者的 mid
     * @param latitude 纬度
     * @param longitude 经度
     * @param description 地理位置描述
     */
    public HSLocationMessage(String to, double latitude, double longitude, String description) {
        // @formatter:off
        super(HSMessageType.LOCATION,                                    // type
              null,                                                      // content
              0,                                                         // msgServerID
              null,                                                      // msgID
              HSBaseMessage.PUSH_TAG_LOCATION,                           // pushTag
              true,                                                      // isMessageOriginate
              HSAccountManager.getInstance().getMainAccount().getMID(),  // from
              to,                                                        // to
              new Date(HSAccountManager.getInstance().getServerTime()),  // timestamp
              HSBaseMessage.HSMessageStatus.SENDING,                     // status
              HSBaseMessage.HSMessageMediaStatus.DOWNLOADED,             // mediaStatus
              1);                                                        // downloadProgress
        // @formatter:on
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;

        JSONObject content = new JSONObject();
        try {
            content.put(Constants.LATITUDE, latitude);
            content.put(Constants.LONGITUDE, longitude);
            if (!TextUtils.isEmpty(description)) {
                content.put(Constants.DESCRIPTION, description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.setContent(content);
        setMsgID(Utils.getOneUUID());
    }

    public HSLocationMessage(JSONObject info) {
        super(info);
    }

    public HSLocationMessage(Cursor cursor) {
        super(cursor);
    }

    @Override
    public void initMessageSpecialProperties() {
        JSONObject ct = getContent();
        this.latitude = ct.optDouble(Constants.LATITUDE);
        this.longitude = ct.optDouble(Constants.LONGITUDE);
        this.description = ct.optString(Constants.DESCRIPTION);
        this.setGoHttp(true);
    }

    @Override
    public String toString() {
        return super.toString() + " latitude: " + latitude + " longitude: " + longitude + " description: " + description;
    }
}
