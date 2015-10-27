package test.contacts.demo.friends.api;

import java.util.ArrayList;

import test.contacts.demo.friends.ContactFriendsMgr;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.ihs.contacts.api.IContactBase;

public class HSContactFriendsMgr {
    public interface IFriendSyncListener {
        /**
         * triggered when the process of fetching friends from remote server has finished
         * 
         * @param success of fail
         */
        void onFriendsSyncFinished(boolean result, ArrayList<IContactBase> friendList);
    }

    /**
     * initiate the HSFriendsMgr
     * 
     * @param context
     * @param handler the working thread looper handler
     */
    public static void init(Context context, Handler handler, String urlSync, String urlAck) {
        ContactFriendsMgr.init(context, handler, urlSync, urlAck);
    }

    /**
     * process friends update push notification.
     * 
     * @param pushInfo
     */
    public static void handlePushInfo(Intent intent) {
        //ContactFriendsMgr.getInstance().handlePushInfo(intent);
    }

    /**
     * start downloading friends info from remote server
     * 
     * @param needNotify
     */
    public static void startSync(boolean needNotify) {
        ContactFriendsMgr.getInstance().startSync(needNotify);
    }

    /**
     * add a observer for contact changes
     * 
     * @param observer
     */
    public static void addSyncFinishListener(IFriendSyncListener listener) {
        ContactFriendsMgr.getInstance().addSyncFinishListener(listener);
    }

    /**
     * remove a observer for contact changes
     * 
     * @param observer
     */
    public static void removeSyncFinishListener(IFriendSyncListener listener) {
        ContactFriendsMgr.getInstance().removeSyncFinishListener(listener);
    }

    /**
     * return whether a number or email is friend or not
     * 
     * @param content
     */
    public static boolean isFriend(String content) {
        return ContactFriendsMgr.getInstance().isFriend(content);
    }
}
