package test.contacts.demo.friends;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import test.contacts.demo.friends.api.HSContactFriendsMgr.IFriendSyncListener;
import test.contacts.demo.friends.dao.ContactFriendsDao;
import test.contacts.demo.friends.dao.FriendsDBHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.account.HSMainAccount;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.notificationcenter.INotificationObserver;
import com.ihs.commons.utils.HSBundle;
import com.ihs.commons.utils.HSLog;
import com.ihs.commons.utils.HSSynchronizer;
import com.ihs.commons.utils.HSSynchronizer.ISyncResultCallBack;
import com.ihs.commons.utils.HSWeakRefLinkedList;
import com.ihs.contacts.api.HSPhoneContactMgr;
import com.ihs.contacts.api.IContactBase;

public class ContactFriendsMgr implements ISyncResultCallBack {

    public enum AccountType {
        PHONE("phone"),
        GOOGLE("google");

        private String value;

        AccountType(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }
    }

    private static final int MSG_SYNC_FRIENDS = 3;
    private final static String SYNC_TAG = "contactfriends";
    private static final String FRIEND_KEY_LAST_MID = "friend_key_last_mid";
    private static ContactFriendsMgr instance;
    private HSWeakRefLinkedList<IFriendSyncListener> syncListeners;
    private HSSynchronizer friendSynchronizer;
    private Handler workingHandler;
    private HandlerThread handlerThread;
    private Context context;
    private String urlSync;
    private String urlAck;
    private HashSet<IContactBase> newFriendContacts;
    protected boolean shouldNotify;
    private int retryCount;

    private INotificationObserver accountObserver = new INotificationObserver() {
        @Override
        public void onReceive(String eventName, HSBundle notificaiton) {
            HSLog.d("event description is: " + eventName);
            if (HSAccountManager.HS_ACCOUNT_NOTIFICATION_LOGOUT_DID_FINISH.equals(eventName)) {
                HSLog.d("Logout succeedded, login again");
                workingHandler.removeCallbacksAndMessages(null);
                friendSynchronizer = null;
            }
        }
    };

    /**
     * init friends module
     * 
     * @param context
     * @param handler
     * @param urlSync
     * @param urlAck
     * @return
     */
    public static ContactFriendsMgr init(Context context, Handler handler, String urlSync, String urlAck) {
        if (instance == null) {
            synchronized (ContactFriendsMgr.class) {
                if (instance == null) {
                    instance = new ContactFriendsMgr(context, handler, urlSync, urlAck);
                }
            }
        }
        return instance;
    }

    /**
     * private constructor of the FriendsMgr class
     * 
     * @param context
     * 
     */
    protected ContactFriendsMgr(Context context, Handler handler, String urlSync, String urlAck) {
        this.context = context;
        this.urlAck = urlAck;
        this.urlSync = urlSync;
        this.shouldNotify = false;
        syncListeners = new HSWeakRefLinkedList<IFriendSyncListener>();
        newFriendContacts = new HashSet<IContactBase>();
        FriendsDBHelper.init(context);
        if (handler != null) {
            workingHandler = handler;
        } else {
            handlerThread = new HandlerThread("FriendsWorkerLooper");
            handlerThread.start();
            workingHandler = new Handler(handlerThread.getLooper());
        }
        HSGlobalNotificationCenter.addObserver(HSAccountManager.HS_ACCOUNT_NOTIFICATION_LOGOUT_DID_FINISH, accountObserver);
    }

    /**
     * get the singleton instance
     * 
     * @return FriendsMgr instance
     */
    public static ContactFriendsMgr getInstance() {
        return instance;
    }

    /**
     * start synchronization, download contact data from server
     */
    public void startSync(boolean needNotify) {

        if (TextUtils.isEmpty(urlSync)) {
            return;
        }

        HSMainAccount mainAccount = HSAccountManager.getInstance().getMainAccount();
        boolean isAccountSessionInvalidate = (HSAccountManager.getInstance().getSessionState() == HSAccountManager.HSAccountSessionState.INVALID);
        if (isAccountSessionInvalidate) {
            return;
        }

        if (friendSynchronizer != null) {
            HSLog.d("friendsynchronizer is not null, do nothing");
            return;
        }

        this.shouldNotify = needNotify;
        this.newFriendContacts.clear();
        HSLog.d("start synchronizing friends from remote server... shouldNotify is " + shouldNotify + ", mid = " + mainAccount.getMID() + ", session id = "
                + mainAccount.getSessionID() + ", app id = " + HSAccountManager.getInstance().getAppID());
        friendSynchronizer = new HSSynchronizer(context, HSAccountManager.getInstance().getAppID(), mainAccount.getMID(), mainAccount.getSessionID(), SYNC_TAG, this, urlSync,
                urlAck, workingHandler);
        friendSynchronizer.setKeepCenterEnabled(false);

        HSLog.d("friends", "sync url " + urlAck + " ackURL " + urlAck);
        Message msg = Message.obtain(workingHandler, new Runnable() {
            @Override
            public void run() {
                friendSynchronizer.sync();
            }
        });
        msg.what = MSG_SYNC_FRIENDS;
        if (!workingHandler.hasMessages(MSG_SYNC_FRIENDS)) {
            HSLog.d("friends", "calling module utils to sync with the server, send message MSG_SYNC_FRIENDS");
            workingHandler.sendMessage(msg);
        } else {
            HSLog.d("friends", "already has a message for import");
        }
    }


    /**
     * add listener
     * 
     * @param listener
     */
    public void addSyncFinishListener(IFriendSyncListener listener) {
        syncListeners.add(listener);
    }

    /**
     * remove listener
     * 
     * @param listener
     */
    public void removeSyncFinishListener(IFriendSyncListener listener) {
        syncListeners.remove(listener);
    }

    /**
     * mark friends in all tables
     * 
     * @param jsonFriends
     */
    protected boolean markFriends(JSONArray jsonFriends) {
        if (jsonFriends.length() == 0) {
            return true;
        }

        ArrayList<String> contentsToAdd = new ArrayList<String>();

        try {
            for (int i = 0; i < jsonFriends.length(); i++) {
                JSONObject friend = (JSONObject) jsonFriends.get(i);
                String mid = friend.getString("mid");
                JSONObject persona = friend.getJSONObject("persona");
                JSONArray subAccounts = persona.getJSONArray("sub_accounts");
                for (int j = 0; j < subAccounts.length(); j++) {
                    String sid = ((JSONObject) subAccounts.get(j)).getString("sid");
                    String accountType = ((JSONObject) subAccounts.get(j)).getString("acnt_typ");
                    String verifyType = ((JSONObject) subAccounts.get(j)).getString("vrfy_typ");
                    FriendInfo friendInfo = new FriendInfo();
                    friendInfo.sid = sid;
                    friendInfo.accountType = accountType;
                    friendInfo.verifyType = verifyType;
                    friendInfo.mid = mid;
                    ContactFriendsDao.getInstance().save(friendInfo);
                    contentsToAdd.add(sid);
                }
            }
            HSPhoneContactMgr.markFriendsByContent(contentsToAdd, true);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * notify the listeners
     * 
     * @param success
     */
    protected void notifyFriendSyncFinished(boolean success, ArrayList<IContactBase> friendList) {
        for (IFriendSyncListener listener : syncListeners) {
            if (null != listener) {
                listener.onFriendsSyncFinished(success, friendList);
            }
        }
    }

    @Override
    public boolean onNewMessageReceived(HSSynchronizer synchronizer, final JSONArray messages) {
        HSLog.d("friends jsonArray is: " + messages.toString());
        return markFriends(messages); // mark fetched friends in friends table
                                      // and contactFriends table
    }

    @Override
    public void onSyncFailed(HSSynchronizer synchronizer, int result) {
        HSLog.d("get friends failed, statuscode is: " + result);
        notifyFriendSyncFinished(false, new ArrayList<IContactBase>());
        newFriendContacts.clear();
        shouldNotify = false;
        friendSynchronizer = null;
    }

    @Override
    public void onSyncFinished(HSSynchronizer synchronizer, int result) {
        HSLog.d("friends", "sync finished");
        boolean isAccountSessionInvalidate = (HSAccountManager.getInstance().getSessionState() == HSAccountManager.HSAccountSessionState.INVALID);
        HSMainAccount mainAccount = HSAccountManager.getInstance().getMainAccount();
        if (isAccountSessionInvalidate) {
            friendSynchronizer = null;
            return;
        }

        boolean isFirstSync = !mainAccount.getMID().equals(getLastMid());

        if (isFirstSync) {
            shouldNotify = false;
        }

        if (isFirstSync && newFriendContacts.isEmpty() && retryCount < 3) {
            retryCount += 1;
            workingHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    HSLog.d("retry get friends, current retry count is: " + retryCount);
                    startSync(false);
                }
            }, 10000 * retryCount);
        } else {
            HSLog.d("get friends success, statuscode is: " + result + ", should notify is: " + shouldNotify);
            if (shouldNotify) {
                ArrayList<IContactBase> friendList = new ArrayList<IContactBase>();
                friendList.addAll(newFriendContacts);
                notifyFriendSyncFinished(true, friendList);
            }
            newFriendContacts.clear();
            shouldNotify = false;
            retryCount = 0;
            saveLastMid(mainAccount.getMID());
        }

        friendSynchronizer = null;
    }

    public boolean isFriend(String content) {
        return ContactFriendsDao.getInstance().isFriend(content);
    }

    private void saveLastMid(String mid) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(FRIEND_KEY_LAST_MID, mid).commit();
    }

    private String getLastMid() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(FRIEND_KEY_LAST_MID, "");
    }
}
