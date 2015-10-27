package com.ihs.message_2013011392.types;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.text.TextUtils;

import com.ihs.message_2013011392.managers.MessageDBManager;
import com.ihs.message_2013011392.utils.Utils;

/*
 * 消息工厂类
 */
public class MessageFactory {

    public MessageFactory() {
        // TODO Auto-generated constructor stub
    }

    public static HSBaseMessage messageWithCusor(Cursor c) {
        HSMessageType type = Utils.getMessageType(c.getString(c.getColumnIndex(MessageDBManager.COLUMN_TYPE)));

        // Check for unsupported new version
        int version = 0;
        String contentStr = c.getString(c.getColumnIndex(MessageDBManager.COLUMN_CONTENT));
        try {
            JSONObject content = TextUtils.isEmpty(contentStr) ? null : new JSONObject(contentStr);
            String versionStr = content.optString(Constants.VERSION);
            version = TextUtils.isEmpty(versionStr) ? 0 : Integer.parseInt(versionStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (version > Integer.parseInt(Constants.MESSAGE_VERSION)) {
            //            return new HSUnknownMessage(c);
            return null;
        }

        switch (type) {
            case TEXT: {
                return new HSTextMessage(c);
            }
            case AUDIO: {
                return new HSAudioMessage(c);
            }
            case ACTION:
                break;
            case FILE:
                break;
            case IMAGE:
                return new HSImageMessage(c);
            case INTERNAL_LINK:
                break;
            case LIKE_PLUS:
                break;
            case LINK:
                break;
            case LOCATION:
                return new HSLocationMessage(c);
            case RECEIPT:
                break;
            case STICKER:
                break;
            case TYPING:
                break;
            case VIDEO:
                break;
            case UNKNOWN:
                //                return new HSUnknownMessage(c);
            default:
                break;
        }
        return null;
    }

    public static HSBaseMessage messageWithInfo(JSONObject info) {
        HSMessageType type = Utils.getMessageType(info.optString(Constants.TYPE));
        switch (type) {
            case TEXT: {
                return new HSTextMessage(info);
            }
            case AUDIO: {
                return new HSAudioMessage(info);
            }
            case ACTION:
                break;
            case FILE:
                break;
            case IMAGE:
                return new HSImageMessage(info);
            case INTERNAL_LINK:
                break;
            case LIKE_PLUS:
                break;
            case LINK:
                break;
            case LOCATION:
                return new HSLocationMessage(info);
            case RECEIPT:
                break;
            case STICKER:
                break;
            case TYPING:
                break;
            case VIDEO:
                break;
            case UNKNOWN:
                //                return new HSUnknownMessage(info);
            default:
                break;
        }
        return null;
    }

}
