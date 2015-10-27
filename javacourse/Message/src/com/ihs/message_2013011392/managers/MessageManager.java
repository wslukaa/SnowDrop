package com.ihs.message_2013011392.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.account.HSAccountManager.HSAccountSessionState;
import com.ihs.app.framework.HSApplication;
import com.ihs.app.framework.HSNotificationConstant;
import com.ihs.commons.config.HSConfig;
import com.ihs.commons.keepcenter.HSKeepCenter;
import com.ihs.commons.keepcenter.HSKeepCenter.HSKeepCenterListener;
import com.ihs.commons.keepcenter.HSKeepCenter.IMessageSendingCallback;
import com.ihs.commons.keepcenter.HSKeepCenterMessage;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.notificationcenter.HSNotificationCenter;
import com.ihs.commons.notificationcenter.INotificationObserver;
import com.ihs.commons.utils.HSBundle;
import com.ihs.commons.utils.HSError;
import com.ihs.commons.utils.HSLog;
import com.ihs.commons.utils.HSSynchronizer;
import com.ihs.commons.utils.HSSynchronizer.ISyncResultCallBack;
import com.ihs.message_2013011392.managers.HSMessageChangeListener.HSMessageChangeType;
import com.ihs.message_2013011392.managers.HSMessageManager.QueryResult;
import com.ihs.message_2013011392.managers.HSMessageManager.SendMessageCallback;
import com.ihs.message_2013011392.managers.MessageDBManager.MessageDBOperationResult;
import com.ihs.message_2013011392.types.Constants;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSImageMessage;
import com.ihs.message_2013011392.types.HSMessageType;
import com.ihs.message_2013011392.types.HSOnlineMessage;
import com.ihs.message_2013011392.types.IMediaProtocol;
import com.ihs.message_2013011392.types.MessageFactory;
import com.ihs.message_2013011392.types.HSBaseMessage.HSMessageStatus;
import com.ihs.message_2013011392.utils.Utils;

public class MessageManager implements INotificationObserver {

    private MessageNetworkOperator operator;
    private MessageDBManager dbManager;
    private Handler mHandler;
    private HSSynchronizer mSynchronizer;
    private final static String TAG = MessageManager.class.getName();
    private HSNotificationCenter notifyCenter;
    private HashMap<HSMessageChangeListener, INotificationObserver> observerMap;

    private static final String NOTIFICATION_NAME_MESSAGE_CHANGED = "NOTIFICATION_NAME_MESSAGE_CHANGED";
    private static final String NOTIFICATION_BUNDLE_KEY_CHANGE_TYPE = "NOTIFICATION_BUNDLE_KEY_CHANGE_TYPE";
    private static final String NOTIFICATION_BUNDLE_KEY_RELATED_MESSAGE = "NOTIFICATION_BUNDLE_KEY_RELATED_MESSAGE";

    private static final String NOTIFICATION_NAME_TYPING_RECEIVED = "NOTIFICATION_NAME_TYPING_RECEIVED";
    private static final String NOTIFICATION_BUNDLE_KEY_FROM_MID = "NOTIFICATION_BUNDLE_KEY_FROM_MID";

    private static final String NOTIFICATION_NAME_ONLINE_MSG_RECEIVED = "NOTIFICATION_NAME_ONLINE_MSG_RECEIVED";
    private static final String NOTIFICATION_BUNDLE_KEY_ONLINE_MSG = "NOTIFICATION_BUNDLE_KEY_ONLINE_MSG";

    private static final String NOTIFICATION_NAME_UNREAD_COUNT_CHANGED = "NOTIFICATION_NAME_UNREAD_COUNT_CHANGED";
    private static final String NOTIFICATION_BUNDLE_KEY_NEW_COUNT = "NOTIFICATION_BUNDLE_KEY_NEW_COUNT";

    private static final String NOTIFICATION_NAME_RECEIVE_REMOTE_NOTIFICATION = "NOTIFICATION_NAME_RECEIVE_REMOTE_NOTIFICATION";
    private static final String NOTIFICATION_BUNDLE_KEY_USER_INFO = "NOTIFICATION_BUNDLE_KEY_USER_INFO";

    public MessageManager() {
        operator = new MessageNetworkOperator();
        mHandler = new Handler();
        notifyCenter = new HSNotificationCenter();
        observerMap = new HashMap<HSMessageChangeListener, INotificationObserver>();
        File mediaFolder = new File(Utils.getMediaPath());
        if (!mediaFolder.exists()) {
            mediaFolder.mkdir();
        }
        HSGlobalNotificationCenter.addObserver(HSNotificationConstant.HS_SESSION_START, this);
        HSKeepCenter.getInstance().addListener(new HSKeepCenterListener() {

            @Override
            public void onKeepCenterReceivedMessage(HSKeepCenterMessage message) {
                HSLog.e(TAG, "listener received new message." + message.getBody());
                if (TextUtils.equals(message.getCommand(), HSKeepCenterMessage.COMMNAND_INSTACT) && TextUtils.equals(message.getActType(), "TYPING-START")) {
                    Map<String, String> headers = message.getHeaders();
                    String uidPart = headers.get("uid");
                    String components[] = TextUtils.split(uidPart, "\\.");
                    if (components.length == 2) {
                        HSBundle bundle = new HSBundle();
                        bundle.putString(NOTIFICATION_BUNDLE_KEY_FROM_MID, components[0]);
                        notifyCenter.sendNotification(NOTIFICATION_NAME_TYPING_RECEIVED, bundle);
                        HSLog.d(TAG, "typing received from: " + components[0]);
                    }
                } else if (TextUtils.equals(message.getCommand(), HSKeepCenterMessage.COMMNAND_INSTACT) && TextUtils.equals(message.getActType(), "ONLINE-MSG")) {
                    Map<String, String> headers = message.getHeaders();
                    String uidPart = headers.get("uid");
                    String components[] = TextUtils.split(uidPart, "\\.");
                    JSONObject content = null;
                    try {
                        content = new JSONObject(message.getBody());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (components.length == 2) {
                        HSOnlineMessage msg = new HSOnlineMessage(components[0], content);
                        HSBundle bundle = new HSBundle();
                        bundle.putObject(NOTIFICATION_BUNDLE_KEY_ONLINE_MSG, msg);
                        notifyCenter.sendNotification(NOTIFICATION_NAME_ONLINE_MSG_RECEIVED, bundle);
                        HSLog.d(TAG, "Online message received from: " + components[0]);
                    }

                } else if (TextUtils.equals(message.getCommand(), HSKeepCenterMessage.COMMNAND_NOTIFY)) {
                    String body = message.getBody();
                    if (TextUtils.isEmpty(body) == false) {
                        try {
                            JSONObject payload = new JSONObject(body);
                            if (payload != null) {
                                JSONObject data = payload.optJSONObject("data");
                                if (data != null) {
                                    String act = data.optString("act");
                                    if (TextUtils.equals(act, "msg")) {
                                        pullMessages();
                                        JSONObject userInfo = data;
                                        HSBundle bundle = new HSBundle();
                                        bundle.putObject(NOTIFICATION_BUNDLE_KEY_USER_INFO, userInfo);
                                        notifyCenter.sendNotification(NOTIFICATION_NAME_RECEIVE_REMOTE_NOTIFICATION, bundle);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onKeepCenterDisconnected() {
            }

            @Override
            public void onKeepCenterConnected() {
                pullMessages();
            }
        }, mHandler);
        pullMessages();
    }

    public void send(final HSBaseMessage message, final SendMessageCallback callback, final Handler handler) {
        message.setStatus(HSMessageStatus.FAILED);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MessageInsertResult result = getDBManager().insertMessage(message);
                List<HSBaseMessage> insertedMessages = result.getMessages();
                for (HSBaseMessage msg : insertedMessages) {
                    msg.setStatus(HSMessageStatus.SENDING);
                }
                notifyMessageChange(HSMessageChangeType.ADDED, insertedMessages);
                operator.send(message, new SendMessageCallback() {
                    @Override
                    public void onMessageSentFinished(final HSBaseMessage message, boolean success, HSError error) {
                        if (success) {
                            message.setStatus(HSMessageStatus.SENT);
                        } else {
                            message.setStatus(HSMessageStatus.FAILED);
                        }

                        Thread updateThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getDBManager().updateMessage(message);
                                if (callback != null) {
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            callback.onMessageSentFinished(message, true, null);
                                        }
                                    });
                                }
                                ArrayList<HSBaseMessage> updatedMessages = new ArrayList<HSBaseMessage>();
                                updatedMessages.add(message);
                                notifyMessageChange(HSMessageChangeType.UPDATED, updatedMessages);
                                HSLog.d(TAG, "Ringtone sent");
                            }
                        });
                        updateThread.start();
                    }

                }, MessageManager.this.mHandler);
            }
        });
        thread.start();
    }

    public void sendOnlineMessage(final HSOnlineMessage message) {

        // @formatter:off
        HSKeepCenterMessage request = new HSKeepCenterMessage(
                HSKeepCenterMessage.COMMNAND_INSTACT,                                          // command
                HSConfig.getString("libMessage", "Host"),                                      // host
                "/user/" + message.getMid() + "." + HSAccountManager.getInstance().getAppID(), // path
                Utils.getOneUUID(),                                                            // requestID
                HSKeepCenterMessage.kHSKeepCenterMessageType_ApplicationJSON,                  // contentType
                message.getContent().toString(),                                               // body
                new HashMap<String, String>(),                                                 // headers
                false,                                                                         // isAckRequired
                false,                                                                         // needResponse
                "ONLINE-MSG",                                                                  // actType
                null);                                                                         // reason
        
        HSKeepCenter.getInstance().send(request, new IMessageSendingCallback() {

            @Override
            public void onMessageSendingFinished(final boolean arg0, HSKeepCenterMessage arg1, HSKeepCenterMessage arg2, HSError arg3) {
                HSLog.d(TAG, "finished = " + arg0);
            }
        }, new Handler(Looper.getMainLooper()));
    }

    public QueryResult queryMessages(String relatedTo, int count, long cursor) {
        String[] args = null;
        StringBuffer buf = new StringBuffer();
        if (cursor < 0) {
            buf.append(" where (from_mid = ? or to_mid = ?) ");
            args = new String[] { relatedTo, relatedTo };
        } else {
            buf.append(" where (from_mid = ? or to_mid = ?) and timestamp < ? ");
            args = new String[] { relatedTo, relatedTo, "" + cursor };
        }
        buf.append(" order by timestamp DESC ");
        if (count > 0) {
            buf.append(" limit " + count);
        }
        List<HSBaseMessage> list = getDBManager().queryMessages(buf.toString(), args);
        HSLog.d(TAG, "list message: " + list);
        long timestampCursor = list.size() == 0 ? -1 : list.get(list.size() - 1).getTimestamp().getTime();
        return new QueryResult(list, timestampCursor);
    }

    public boolean isSendingMessage(String msgID) {
        return operator.isSendingMessage(msgID);
    }

    HSBaseMessage queryMessage(String msgID) {
        List<HSBaseMessage> list = getDBManager().queryMessages(" where msg_c_id = ?", new String[] { msgID });
        return list.size() > 0 ? list.get(0) : null;
    }

    int queryUnreadCount(String to) {
        return getDBManager().getMessageUnreadCount(to);
    }

    void deleteMessages(String to) {
        MessageDBOperationResult result = getDBManager().deleteMessages(to);
        notifyMessageChange(HSMessageChangeType.DELETED, result.getAffectedMessages());
        for (UnreadCountChange change : result.getUnreadCountChanges())
            notifyUnreadCountChange(change.getMid(), change.getUnreadCount());
    }

    void deleteMessages(List<HSBaseMessage> messages) {
        MessageDBOperationResult result = getDBManager().deleteMessages(messages);
        notifyMessageChange(HSMessageChangeType.DELETED, result.getAffectedMessages());
        for (UnreadCountChange change : result.getUnreadCountChanges())
            notifyUnreadCountChange(change.getMid(), change.getUnreadCount());
    }

    void markRead(String mid) {
        MessageDBOperationResult result = getDBManager().markRead(mid);
        notifyMessageChange(HSMessageChangeType.UPDATED, result.getAffectedMessages());
        for (UnreadCountChange change : result.getUnreadCountChanges())
            notifyUnreadCountChange(change.getMid(), change.getUnreadCount());
    }

    void markRead(List<HSBaseMessage> messages) {
        MessageDBOperationResult result = getDBManager().markRead(messages);
        notifyMessageChange(HSMessageChangeType.UPDATED, result.getAffectedMessages());
        for (UnreadCountChange change : result.getUnreadCountChanges())
            notifyUnreadCountChange(change.getMid(), change.getUnreadCount());
    }

    void markMediaRead(List<HSBaseMessage> messages) {
        MessageDBOperationResult result = getDBManager().markMediaRead(messages);
        notifyMessageChange(HSMessageChangeType.UPDATED, result.getAffectedMessages());
    }

    MessageDBManager getDBManager() {
        String mid = HSAccountManager.getInstance().getMainAccount().getMID();
        if (TextUtils.isEmpty(mid)) {
            dbManager = null;
        } else if (dbManager == null || TextUtils.equals(mid, dbManager.getMid()) == false) {
            dbManager = new MessageDBManager(mid);
            dbManager.doCreateTables();
        }
        return dbManager;
    }

    private HSSynchronizer getSynchronizer() {
        if (HSAccountManager.getInstance().getSessionState() == HSAccountSessionState.INVALID)
            return null;
        String mid = HSAccountManager.getInstance().getMainAccount().getMID();
        String sessionID = HSAccountManager.getInstance().getMainAccount().getSessionID();
        int appID = HSAccountManager.getInstance().getAppID();
        String host = HSConfig.getString("libMessage", "Host");
        String syncPath = HSConfig.getString("libMessage", "MessageGetPath");
        String ackPath = HSConfig.getString("libMessage", "MessageAckPath");
        if (mSynchronizer == null || TextUtils.equals(mSynchronizer.getMid(), mid) == false || TextUtils.equals(mSynchronizer.getSessionID(), sessionID) == false) {
            mSynchronizer = new HSSynchronizer(HSApplication.getContext(), appID, mid, sessionID, "", new ISyncResultCallBack() {

                @Override
                public void onSyncFinished(HSSynchronizer arg0, int arg1) {
                    HSLog.d(TAG, "sync finished: " + arg1);
                }

                @Override
                public void onSyncFailed(HSSynchronizer arg0, int arg1) {
                    HSLog.d(TAG, "sync failed: " + arg1);
                }

                @Override
                public boolean onNewMessageReceived(HSSynchronizer arg0, JSONArray messages) {
                    HSLog.d(TAG, "json mesages received: " + messages);

                    if (HSAccountManager.getInstance().getSessionState() == HSAccountSessionState.INVALID) {
                        return false;
                    }

                    if (messages == null)
                        return true;
                    ArrayList<HSBaseMessage> realMessages = new ArrayList<HSBaseMessage>();
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            JSONObject info = messages.getJSONObject(i);
                            HSBaseMessage msg = MessageFactory.messageWithInfo(info);
                            if (msg == null) {
                                continue;
                            }
                            if (msg.getType() == HSMessageType.TYPING) {
                                HSLog.d(TAG, "typing received: " + info);
                            } else if (msg.getType() == HSMessageType.RECEIPT) {
                                HSLog.d(TAG, "receipt received: " + info);
                            } else {
                                String versionStr = msg.getContent().optString(Constants.VERSION);
                                int version = TextUtils.isEmpty(versionStr) ? 0 : Integer.parseInt(versionStr);
                                HSMessageType type = msg.getType();
                                if (version > Integer.parseInt(Constants.MESSAGE_VERSION) || !Utils.supportType(type)) {
//                                    realMessages.add(new HSUnknownMessage(msg, msg.getTypeString(), Integer.toString(version)));
                                    return false;
                                } else {
                                    realMessages.add(msg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    MessageInsertResult result = getDBManager().insertMessages(realMessages);
                    notifyMessageChange(HSMessageChangeType.ADDED, result.getMessages());

                    List<UnreadCountChange> countChanges = result.getChanges();
                    for (UnreadCountChange change : countChanges) {
                        HSBundle countBundle = new HSBundle();
                        countBundle.putInt(NOTIFICATION_BUNDLE_KEY_NEW_COUNT, change.getUnreadCount());
                        countBundle.putString(NOTIFICATION_BUNDLE_KEY_FROM_MID, change.getMid());
                        notifyCenter.sendNotification(NOTIFICATION_NAME_UNREAD_COUNT_CHANGED, countBundle);
                    }
                    for (HSBaseMessage msg : result.getMessages()) {
                        if (msg instanceof IMediaProtocol) {
                            IMediaProtocol m = (IMediaProtocol) msg;
                            m.download();
                            if (msg instanceof HSImageMessage) {
                                ((HSImageMessage) m).downloadNormalImage();
                            }
                        }
                    }
                    return true;
                }
            }, host, syncPath, ackPath, mHandler);
        }
        return mSynchronizer;
    }

    void pullMessages() {
        HSLog.d(TAG, "pull message");

        HSSynchronizer synchronizer = getSynchronizer();
        if (synchronizer != null) {
            synchronizer.sync();
        }
    }

    @Override
    public void onReceive(String arg0, HSBundle arg1) {
        HSLog.d(TAG, "HS_SESSION_START notification");
        if (TextUtils.equals(HSNotificationConstant.HS_SESSION_START, arg0)) {
            pullMessages();
        }
    }

    void deleteAllMessages() {
        getDBManager().deleteAllMessages();
    }

    synchronized void addListener(final HSMessageChangeListener listener, final Handler handler) {
        INotificationObserver observer = new INotificationObserver() {

            @Override
            public void onReceive(String name, HSBundle bundle) {
                if (TextUtils.equals(NOTIFICATION_NAME_MESSAGE_CHANGED, name)) {
                    final HSMessageChangeType changeType = (HSMessageChangeType) bundle.getObject(NOTIFICATION_BUNDLE_KEY_CHANGE_TYPE);
                    @SuppressWarnings("unchecked")
                    final List<HSBaseMessage> messages = (List<HSBaseMessage>) bundle.getObjectList(NOTIFICATION_BUNDLE_KEY_RELATED_MESSAGE);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            listener.onMessageChanged(changeType, messages);
                        }
                    });
                } else if (TextUtils.equals(NOTIFICATION_NAME_UNREAD_COUNT_CHANGED, name)) {
                    final String fromMid = bundle.getString(NOTIFICATION_BUNDLE_KEY_FROM_MID);
                    final int newCount = bundle.getInt(NOTIFICATION_BUNDLE_KEY_NEW_COUNT);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            listener.onUnreadMessageCountChanged(fromMid, newCount);
                        }
                    });
                } else if (TextUtils.equals(name, NOTIFICATION_NAME_RECEIVE_REMOTE_NOTIFICATION)) {
                    final JSONObject userInfo = (JSONObject) bundle.getObject(NOTIFICATION_BUNDLE_KEY_USER_INFO);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            listener.onReceivingRemoteNotification(userInfo);
                        }
                    });
                } else if (TextUtils.equals(name, NOTIFICATION_NAME_TYPING_RECEIVED)) {
                    final String fromMid = bundle.getString(NOTIFICATION_BUNDLE_KEY_FROM_MID);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            listener.onTypingMessageReceived(fromMid);
                        }
                    });
                } else if (TextUtils.equals(name, NOTIFICATION_NAME_ONLINE_MSG_RECEIVED)) {
                    final HSOnlineMessage msg = (HSOnlineMessage) bundle.getObject(NOTIFICATION_BUNDLE_KEY_ONLINE_MSG);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            listener.onOnlineMessageReceived(msg);
                        }
                    });
                }
            }
        };
        observerMap.put(listener, observer);
        notifyCenter.addObserver(NOTIFICATION_NAME_MESSAGE_CHANGED, observer);
        notifyCenter.addObserver(NOTIFICATION_NAME_UNREAD_COUNT_CHANGED, observer);
        notifyCenter.addObserver(NOTIFICATION_NAME_RECEIVE_REMOTE_NOTIFICATION, observer);
        notifyCenter.addObserver(NOTIFICATION_NAME_TYPING_RECEIVED, observer);
        notifyCenter.addObserver(NOTIFICATION_NAME_ONLINE_MSG_RECEIVED, observer);
    }

    synchronized void removeListener(HSMessageChangeListener listener) {
        INotificationObserver observer = observerMap.get(listener);
        if (observer != null) {
            notifyCenter.removeObserver(observer);
        }
    }

    void notifyMessageChange(HSMessageChangeType type, List<HSBaseMessage> messages) {
        HSBundle messageChangeBundle = new HSBundle();
        messageChangeBundle.putObjectList(NOTIFICATION_BUNDLE_KEY_RELATED_MESSAGE, messages);
        messageChangeBundle.putObject(NOTIFICATION_BUNDLE_KEY_CHANGE_TYPE, type);
        notifyCenter.sendNotification(NOTIFICATION_NAME_MESSAGE_CHANGED, messageChangeBundle);
    }

    void notifyUnreadCountChange(String mid, int newCount) {
        HSBundle messageChangeBundle = new HSBundle();
        messageChangeBundle.putString(NOTIFICATION_BUNDLE_KEY_FROM_MID, mid);
        messageChangeBundle.putInt(NOTIFICATION_BUNDLE_KEY_NEW_COUNT, newCount);
        notifyCenter.sendNotification(NOTIFICATION_NAME_UNREAD_COUNT_CHANGED, messageChangeBundle);
    }
}
