package com.ihs.message_2013011392.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.account.HSAccountManager.HSAccountSessionState;
import com.ihs.app.framework.HSApplication;
import com.ihs.commons.connection.HSHttpConnection;
import com.ihs.commons.connection.HSHttpConnection.OnConnectionFinishedListener;
import com.ihs.commons.connection.HSServerAPIConnection;
import com.ihs.commons.connection.httplib.HttpRequest.Method;
import com.ihs.commons.utils.HSError;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.managers.HSMessageChangeListener.HSMessageChangeType;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSImageMessage;
import com.ihs.message_2013011392.types.HSMessageType;
import com.ihs.message_2013011392.types.HSOnlineMessage;
import com.ihs.message_2013011392.types.HSBaseMessage.HSMessageMediaStatus;
import com.ihs.message_2013011392.utils.Utils;

@SuppressLint("Assert")
public class DownloadManager {

    private static final String TAG = DownloadManager.class.getName();

    public static enum DownloadOperationType {
        NOT_IMAGE("not_image"),
        THUMBNAIL("thumbnail"),
        NORMAL_IMAGE("normal_image"),
        ORIGINAL_IMAGE("original_image");

        private String value = "";

        private DownloadOperationType(String name) {
            this.value = name;
        }

        public String toString() {
            return this.value;
        }
    }

    static class DownloadOperation extends HSServerAPIConnection {

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public DownloadOperationType getType() {
            return type;
        }

        public void setType(DownloadOperationType type) {
            this.type = type;
        }

        public long getExpectedSize() {
            return expectedSize;
        }

        public void setExpectedSize(long expectedSize) {
            this.expectedSize = expectedSize;
        }

        public long getReceivedSize() {
            return receivedSize;
        }

        public void setReceivedSize(long receivedSize) {
            this.receivedSize = receivedSize;
        }

        private String tag;
        private DownloadOperationType type;
        private long expectedSize;
        private long receivedSize;

        public DownloadOperation(String url, Method method, JSONObject contentBody) {
            super(url, method, contentBody);
        }

    }

    private static DownloadManager mgr = null;
    private HashMap<String, HSBaseMessage> downloadingMessages;
    private HashMap<String, HashMap<String, DownloadOperation>> downloadTasks;
    private Handler handler;
    private HandlerThread thread;

    public static DownloadManager getInstance() {
        if (mgr == null) {
            mgr = new DownloadManager();
        }
        return mgr;
    }

    private DownloadManager() {
        downloadTasks = new HashMap<String, HashMap<String, DownloadOperation>>();
        downloadingMessages = new HashMap<String, HSBaseMessage>();
        thread = new HandlerThread("download_manager");
        thread.start();
        handler = new Handler(thread.getLooper());
        HSMessageManager.getInstance().addListener(new HSMessageChangeListener() {

            @Override
            public void onUnreadMessageCountChanged(String mid, int newCount) {
            }

            @Override
            public void onTypingMessageReceived(String fromMid) {
            }

            @Override
            public void onOnlineMessageReceived(HSOnlineMessage message) {
                HSLog.d(TAG, "onOnlineMessageReceived");
            }

            @Override
            public void onReceivingRemoteNotification(JSONObject userInfo) {
            }

            @Override
            public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages) {
                if (downloadingMessages.size() == 0)
                    return;
                if (changeType == HSMessageChangeType.UPDATED) {
                    for (HSBaseMessage changedMessage : messages) {
                        synchronized (DownloadManager.this) {
                            for (String key : downloadingMessages.keySet()) {
                                HSBaseMessage downloadingMessage = downloadingMessages.get(key);
                                if (TextUtils.equals(downloadingMessage.getMsgID(), changedMessage.getMsgID())) {
                                    downloadingMessage.setMediaRead(changedMessage.isMediaRead());
                                    downloadingMessage.setStatus(changedMessage.getStatus());
                                }
                            }
                        }
                    }
                }
            }
        }, handler);
    }

    void download(HSBaseMessage message, String remotePath, String localPath) {
    }

    void cancelDownloadTask(String msgID, String remotePath, boolean isForDeleting) {
    }

    boolean isDownloading(String msgID, String remotePath) {
        return false;
    }

    double getDownloadProgress(String msgID, String url) {
        return 0;
    }

    void downloadImageMessage(HSImageMessage imageMessage, String remotePath, String localPath) {
    }

    void downloadOriginalImage(HSImageMessage imageMessage, String remotePath, String localPath) {
    }

    public synchronized void downloadMessage(final HSBaseMessage message, final String remotePath, final String localPath, final DownloadOperationType operationType) {
        final String msgID = message.getMsgID();
        HashMap<String, DownloadOperation> tasks = this.downloadTasks.get(msgID);
        if (tasks != null && tasks.get(remotePath) != null) {
            return;
        }

        if (HSAccountManager.getInstance().getSessionState() == HSAccountSessionState.INVALID)
            return;
        this.downloadingMessages.put(msgID + remotePath, message);

        if (message.getType() == HSMessageType.IMAGE) {
            HSImageMessage msg = (HSImageMessage) message;
            switch (operationType) {
                case THUMBNAIL:
                    msg.setThumbnailMediaStatus(HSMessageMediaStatus.DOWNLOADING);
                    break;
                case NORMAL_IMAGE:
                    msg.setNormalImageMediaStatus(HSMessageMediaStatus.DOWNLOADING);
                    break;
                case ORIGINAL_IMAGE:
                    msg.setOriginalImageMediaStatus(HSMessageMediaStatus.DOWNLOADING);
                    break;
                case NOT_IMAGE:
                    assert false : "This is not possible.";
                    break;
            }
        } else {
            message.setMediaStatus(HSMessageMediaStatus.DOWNLOADING);
        }

        final String tmpFilePath = HSApplication.getContext().getCacheDir().getAbsolutePath() + "/" + Utils.getOneUUID();
        File tmpFile = new File(tmpFilePath);
        JSONObject contentBody = new JSONObject();

        try {
            contentBody.put("file", remotePath);
            contentBody.put("mid", HSAccountManager.getInstance().getMainAccount().getMID());
            contentBody.put("sesn_id", HSAccountManager.getInstance().getMainAccount().getSessionID());
            contentBody.put("app_id", HSAccountManager.getInstance().getAppID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final List<HSBaseMessage> messagesToNotify = new ArrayList<HSBaseMessage>();
        messagesToNotify.add(message);
        DownloadOperation operation = new DownloadOperation(Utils.getMessageDownloadingURL(), Method.GET, contentBody);
        operation.setTag(msgID + remotePath);
        operation.setDownloadFile(tmpFile);
        operation.setConnectionFinishedListener(new OnConnectionFinishedListener() {

            @Override
            public void onConnectionSucceeded(HSHttpConnection arg0) {
                Utils.move(tmpFilePath, localPath);
                HSLog.d(TAG, "moving downloaded file to:" + localPath);
                switch (operationType) {
                    case NOT_IMAGE:
                        message.setMediaStatus(HSMessageMediaStatus.DOWNLOADED);
                        break;
                    case THUMBNAIL:
                        ((HSImageMessage) message).setThumbnailMediaStatus(HSMessageMediaStatus.DOWNLOADED);
                        break;
                    case NORMAL_IMAGE:
                        ((HSImageMessage) message).setNormalImageMediaStatus(HSMessageMediaStatus.DOWNLOADED);
                        break;
                    case ORIGINAL_IMAGE:
                        ((HSImageMessage) message).setOriginalImageMediaStatus(HSMessageMediaStatus.DOWNLOADED);
                        break;
                    default:
                        break;
                }
                HSMessageManager.getInstance().getManager().getDBManager().updateMessageMediaStatus(msgID, message.getMediaStatusBackend());
                HSLog.d(TAG, "download succeeded");

                HSMessageManager.getInstance().notifyMessageChange(HSMessageChangeType.UPDATED, messagesToNotify);
            }

            @Override
            public void onConnectionFailed(HSHttpConnection arg0, HSError arg1) {
                HSLog.d(TAG, "download failed");
                switch (operationType) {
                    case NOT_IMAGE:
                        message.setMediaStatus(HSMessageMediaStatus.FAILED);
                        break;
                    case THUMBNAIL:
                        ((HSImageMessage) message).setThumbnailMediaStatus(HSMessageMediaStatus.FAILED);
                        break;
                    case NORMAL_IMAGE:
                        ((HSImageMessage) message).setNormalImageMediaStatus(HSMessageMediaStatus.FAILED);
                        break;
                    case ORIGINAL_IMAGE:
                        ((HSImageMessage) message).setOriginalImageMediaStatus(HSMessageMediaStatus.FAILED);
                        break;
                    default:
                        break;
                }
                HSMessageManager.getInstance().getManager().getDBManager().updateMessageMediaStatus(msgID, message.getMediaStatusBackend());
                HSMessageManager.getInstance().notifyMessageChange(HSMessageChangeType.UPDATED, messagesToNotify);
            }
        });
        operation.startAsync(handler);
    }

    public synchronized void cancelDownloading(String msgID, String remotePath, boolean forDeleting) {
        if (TextUtils.isEmpty(remotePath))
            return;
    }
}
