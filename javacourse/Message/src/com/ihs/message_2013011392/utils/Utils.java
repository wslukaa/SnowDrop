package com.ihs.message_2013011392.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.ihs.app.framework.HSApplication;
import com.ihs.commons.config.HSConfig;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.types.Constants;
import com.ihs.message_2013011392.types.HSMessageType;

public class Utils {

    private static final String TAG = Utils.class.getName();

    public static String getOneUUID() {
        return UUID.randomUUID().toString();
    }

    @SuppressLint("Assert")
    public static String getMessageTypeString(HSMessageType type) {
        switch (type) {
            case TEXT:
                return Constants.TEXT;
            case AUDIO:
                return Constants.AUDIO;
            case IMAGE:
                return Constants.IMAGE;
            case STICKER:
                return Constants.STICKER;
            case LOCATION:
                return Constants.LOCATION;
            case VIDEO:
                return Constants.VIDEO;
            case LINK:
                return Constants.LINK;
            case INTERNAL_LINK:
                return Constants.INTERNAL_LINK;
            case ACTION:
                return Constants.ACTION;
            case FILE:
                return Constants.FILE;
            case TYPING:
                return Constants.TYPING;
            case RECEIPT:
                return Constants.RECEIPT;
            case LIKE_PLUS:
                return Constants.LIKEPLUS;
            case UNKNOWN:
                assert false : "This should never happen.";
                return Constants.UNKNOWN;
            default:
                return null;
        }
    }

    private static HashMap<String, HSMessageType> msgTypeMap = null;

    public static HSMessageType getMessageType(String type) {
        if (msgTypeMap == null) {
            msgTypeMap = new HashMap<String, HSMessageType>();
            msgTypeMap.put(Constants.TEXT, HSMessageType.TEXT);
            msgTypeMap.put(Constants.AUDIO, HSMessageType.AUDIO);
            msgTypeMap.put(Constants.IMAGE, HSMessageType.IMAGE);
            msgTypeMap.put(Constants.STICKER, HSMessageType.STICKER);
            msgTypeMap.put(Constants.LOCATION, HSMessageType.LOCATION);
            msgTypeMap.put(Constants.VIDEO, HSMessageType.VIDEO);
            msgTypeMap.put(Constants.LINK, HSMessageType.LINK);
            msgTypeMap.put(Constants.INTERNAL_LINK, HSMessageType.INTERNAL_LINK);
            msgTypeMap.put(Constants.ACTION, HSMessageType.ACTION);
            msgTypeMap.put(Constants.FILE, HSMessageType.FILE);
            msgTypeMap.put(Constants.TYPING, HSMessageType.TYPING);
            msgTypeMap.put(Constants.RECEIPT, HSMessageType.RECEIPT);
            msgTypeMap.put(Constants.LIKEPLUS, HSMessageType.LIKE_PLUS);
        }
        HSMessageType knownType = msgTypeMap.get(type);
        if (knownType == null) {
            return HSMessageType.UNKNOWN;
        }
        return knownType;
    }

    public static boolean supportType(HSMessageType type) {
        return HSMessageType.TEXT.getValue() <= type.getValue() && type.getValue() <= HSMessageType.LIKE_PLUS.getValue();
    }

    public static String getMediaPath() {
        return HSApplication.getContext().getFilesDir().getPath() + "/Medias";
    }

    public static String getPath(String filename) {
        return getMediaPath() + "/" + filename;
    }

    public static String getLocalFileNameOfFileRemotePath(String remotePath, String msgID) {
        return md5(remotePath + msgID) + getFileExtention(remotePath);
    }

    public static String getLocalFilePathOfFileRemotePath(String remotePath, String msgID) {
        return getPath(getLocalFileNameOfFileRemotePath(remotePath, msgID));
    }

    public static String getFileExtention(String uri) {
        HSLog.d(TAG, "uri:" + uri);
        String components[] = TextUtils.split(uri, "/");
        if (components.length > 0) {
            String fileName = components[components.length - 1];
            HSLog.d(TAG, "filename:" + fileName);
            String segments[] = TextUtils.split(fileName, "\\.");
            for (String seg : segments) {
                HSLog.d(TAG, "seg:" + seg);
            }
            if (segments.length >= 2) {
                return "." + segments[segments.length - 1];
            }
        }
        return "";
    }

    /**
     * @param 对一个字符串做 md5
     * @return 返回
     */
    static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void move(String srcFile, String dstFile) {
        File src = new File(srcFile);
        src.renameTo(new File(dstFile));
    }

    public static void copy(String scrFile, String dstFile) {
        try {
            FileInputStream in = new FileInputStream(scrFile);
            FileOutputStream out = new FileOutputStream(dstFile);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String file) {
        (new File(file)).delete();
    }

    public static String getMessageSendingURL() {
        return HSConfig.getString("libMessage", "HttpServer") + "/message/send";
    }

    public static String getMessageDeleteAllURL() {
        return HSConfig.getString("libMessage", "HttpServer") + "/message/delete";
    }

    public static String getMessageDownloadingURL() {
        return HSConfig.getString("libMessage", "HttpServer") + "/message/file/get";
    }

}
