package com.ihs.message_2013011392.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.text.TextUtils;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.commons.connection.HSHttpMultiPart;
import com.ihs.commons.connection.HSServerAPIConnection;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.managers.DownloadManager;
import com.ihs.message_2013011392.managers.DownloadManager.DownloadOperationType;
import com.ihs.message_2013011392.utils.Utils;

/**
 * 语音消息类
 */
public class HSAudioMessage extends HSBaseMessage implements IMediaProtocol {

    private double duration;
    private static final String TAG = HSAudioMessage.class.getName();

    /**
     * 获得语音消息的时长
     * 
     * @return 语音消息的时长
     */
    public double getDuration() {
        return duration;
    }

    /**
     * 设置语音消息的时长
     * 
     * @param duration 语音消息的时长
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * 获得语音文件的本地路径
     * 
     * 路径的管理逻辑：对于己方发送的消息，本地路径存储在消息的 localFileInfo 成员变量中，直接读取；对于从他人处接收到的消息，localFileInfo 中不存储路径，仅在 content
     * 中存储文件在服务器上的远程路径，本地路径通过该远程路径和消息的 Message ID 计算得到
     * 
     * @return 语音文件在客户端文件系统中的路径
     */
    public String getAudioFilePath() {
        JSONObject localFileInfo = getLocalFileInfo();
        String localFileName = null;
        if (localFileInfo != null) {
            localFileName = localFileInfo.optString(Constants.AUDIO);
        }
        HSLog.d(TAG, "localFileName = " + localFileName);
        if (TextUtils.isEmpty(localFileName)) {
            HSLog.d(TAG, "localFileName is empty = " + localFileName);
            return Utils.getLocalFilePathOfFileRemotePath(getAudioRemotePath(), this.getMsgID());
        } else {
            return Utils.getPath(localFileName);
        }
    }

    /**
     * 获得语音文件的远程路径
     * 
     * @return 语音文件在服务器端的路径，路径不存在时返回 null
     */
    public String getAudioRemotePath() {
        JSONObject files = getContent().optJSONObject(Constants.FILES);
        if (files != null) {
            return files.optString(Constants.AUDIO);
        }
        return null;
    }

    /**
     * 语音消息的构造方法
     * 
     * @param to 消息接收者的 mid
     * @param filePath 语音文件的本地路径，调用构造方法前。应当在 filePath 路径处准备好发送的语音文件，构造方法会将文件重命名后，复制到统一的媒体文件路径下进行管理
     * @param duration 语音文件的时长，该时长由用户给出，单位制自定，用于接受方客户端在下载语音文件之前的 UI 显示，libMessage 不对该时长与文件的一致性做检查
     */
    public HSAudioMessage(String to, String filePath, double duration) {
        super(HSMessageType.AUDIO, null, 0, null, HSBaseMessage.PUSH_TAG_AUDIO, true, HSAccountManager.getInstance().getMainAccount().getMID(), to, new Date(HSAccountManager
                .getInstance().getServerTime()), HSBaseMessage.HSMessageStatus.SENDING, HSBaseMessage.HSMessageMediaStatus.DOWNLOADED, 1);
        JSONObject content = new JSONObject();
        try {
            content.put(Constants.DURATION, duration);
            content.put(Constants.FILE, Constants.AUDIO);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setContent(content);
        String msgID = Utils.getOneUUID();
        this.duration = duration;

        JSONObject localFileInfo = new JSONObject();
        String audioFileName = msgID + Utils.getFileExtention(filePath);
        try {
            localFileInfo.put(Constants.AUDIO, audioFileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setLocalFileInfo(localFileInfo);
        HSLog.d(TAG, "localFileInfo" + getLocalFileInfo());
        setMsgID(msgID);
        setGoHttp(true);
        Utils.copy(filePath, this.getAudioFilePath());
    }

    @Override
    public HSServerAPIConnection getServerAPIRequest() {
        ArrayList<HSHttpMultiPart> bodyParts = new ArrayList<HSHttpMultiPart>();
        HSLog.d(TAG, "file exists " + (new File(getAudioFilePath())).exists());
        bodyParts.add(new HSHttpMultiPart(Constants.AUDIO, Constants.AUDIO, "wav", new File(getAudioFilePath())));
        return new HSServerAPIConnection(Utils.getMessageSendingURL(), getDataBody(), bodyParts);
    }

    public HSAudioMessage(Cursor c) {
        super(c);
    }

    public HSAudioMessage(JSONObject info) {
        super(info);
    }

    @Override
    public void initMessageSpecialProperties() {
        JSONObject ct = getContent();
        this.duration = ct.optDouble(Constants.DURATION);
        this.setGoHttp(true);
    }

    @Override
    public void download() {
        DownloadManager.getInstance().downloadMessage(this, getAudioRemotePath(), getAudioFilePath(), DownloadOperationType.NOT_IMAGE);
    }

    @Override
    public void cancelDownlad() {
        DownloadManager.getInstance().cancelDownloading(this.getMsgID(), getAudioRemotePath(), false);
    }
}
