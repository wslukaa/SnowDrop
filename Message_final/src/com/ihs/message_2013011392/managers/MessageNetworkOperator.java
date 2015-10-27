package com.ihs.message_2013011392.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.os.Handler;
import android.os.Looper;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.commons.config.HSConfig;
import com.ihs.commons.connection.HSHttpConnection;
import com.ihs.commons.connection.HSHttpConnection.OnConnectionFinishedListener;
import com.ihs.commons.connection.HSServerAPIConnection;
import com.ihs.commons.keepcenter.HSKeepCenter;
import com.ihs.commons.keepcenter.HSKeepCenter.IMessageSendingCallback;
import com.ihs.commons.keepcenter.HSKeepCenterMessage;
import com.ihs.commons.utils.HSError;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.managers.HSMessageManager.SendMessageCallback;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSMessageType;
import com.ihs.message_2013011392.utils.Utils;

public class MessageNetworkOperator {

    private final String TAG = this.getClass().getSimpleName();
    private Set<String> inSendingMessagesIDs;

    public MessageNetworkOperator() {
        inSendingMessagesIDs = new HashSet<String>();
    }

    synchronized void addMessageID(String msgID) {
        inSendingMessagesIDs.add(msgID);
    }

    public synchronized boolean isSendingMessage(String msgID) {
        return inSendingMessagesIDs.contains(msgID);
    }

    synchronized void remove(String msgID) {
        inSendingMessagesIDs.remove(msgID);
    }

    public void send(final HSBaseMessage message, final SendMessageCallback callback, final Handler handler) {
        final String msgID = message.getMsgID();
        addMessageID(msgID);
        if (message.getGoHttp()) {
            HSLog.d(TAG, "go through http");
            final HSServerAPIConnection req = message.getServerAPIRequest();
            req.setConnectionFinishedListener(new OnConnectionFinishedListener() {

                @Override
                public void onConnectionSucceeded(HSHttpConnection arg0) {
                    HSLog.d(TAG, "http succeeded");
                    remove(msgID);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            callback.onMessageSentFinished(message, true, null);
                        }
                    });
                }

                @Override
                public void onConnectionFailed(HSHttpConnection arg0, HSError arg1) {
                    HSLog.d(TAG, "http failed" + arg1.getMessage());
                    remove(msgID);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            callback.onMessageSentFinished(message, false, null);
                        }
                    });
                }
            });
            req.startAsync(handler);
        } else {
            String host = HSConfig.getString("libMessage", "Host");
            String path = HSConfig.getString("libMessage", "MessageSendPath");
            boolean needResponse = message.getType() != HSMessageType.TYPING;
            HSKeepCenterMessage kMsg = null;
            if (message.getType() == HSMessageType.TYPING) {
                kMsg = new HSKeepCenterMessage(HSKeepCenterMessage.COMMNAND_INSTACT, host, "/user/" + message.getTo() + "." + HSAccountManager.getInstance().getAppID(),
                        Utils.getOneUUID(), HSKeepCenterMessage.kHSKeepCenterMessageType_ApplicationJSON, null, new HashMap<String, String>(), false, needResponse, "TYPING-START",
                        null);
            } else {
                kMsg = new HSKeepCenterMessage(HSKeepCenterMessage.COMMNAND_CALL, host, path, message.getDataBody(), new HashMap<String, String>(), true, needResponse);
            }
            HSLog.d(TAG, "" + message.getDataBody());

            HSKeepCenter.getInstance().send(kMsg, new IMessageSendingCallback() {

                @Override
                public void onMessageSendingFinished(final boolean arg0, HSKeepCenterMessage arg1, HSKeepCenterMessage arg2, HSError arg3) {
                    HSLog.d(TAG, "finished = " + arg0);
                    remove(msgID);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            callback.onMessageSentFinished(message, arg0, null);
                        }
                    });
                }
            }, new Handler(Looper.getMainLooper()));
        }
    }
}
