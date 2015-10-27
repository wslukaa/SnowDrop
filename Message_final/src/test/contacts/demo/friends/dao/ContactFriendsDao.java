package test.contacts.demo.friends.dao;

import test.contacts.demo.friends.FriendInfo;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.account.HSMainAccount;
import com.ihs.commons.utils.HSLog;

public class ContactFriendsDao {
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MID = "mid";
    private static final String COLUMN_FRIEND_MID = "friend_mid";
    private static final String COLUMN_SID = "sid";
    private static final String COLUMN_ACCOUNT_TYPE = "account_type";
    private static final String COLUMN_VERIFY_TYPE = "verify_type";

    private static final String TABLE_NAME = "contactFriends";
    private static final String TAG = ContactFriendsDao.class.getName();

    private SQLiteDatabase database;

    public static void createTable(SQLiteDatabase db) {
        HSLog.d("creating db, name is" + TABLE_NAME);
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY ," + COLUMN_MID + " TEXT," + COLUMN_FRIEND_MID + " TEXT," + COLUMN_SID
                + " TEXT," + COLUMN_ACCOUNT_TYPE + " TEXT," + COLUMN_VERIFY_TYPE + " TEXT," + FriendsDBHelper.EXTENDED_DATA1 + " TEXT," + FriendsDBHelper.EXTENDED_DATA2 + " TEXT,"
                + FriendsDBHelper.EXTENDED_DATA3 + " TEXT" + ");";
        db.execSQL(sql);
    }

    private static class ContactFriendsDaoHolder {
        private static final ContactFriendsDao INSTANCE = new ContactFriendsDao();
    }

    public static ContactFriendsDao getInstance() {
        return ContactFriendsDaoHolder.INSTANCE;
    }

    /**
     * 存储和更新friend传联系人
     * 
     * @param friendInfo
     */
    public void save(FriendInfo friendInfo) {
        database = FriendsDBHelper.getInstance().getWritableDatabase();
        String tableName = TABLE_NAME;
        HSMainAccount mainAccount = HSAccountManager.getInstance().getMainAccount();
        if (mainAccount == null) {
            return;
        }
        String mid = mainAccount.getMID();
        database.beginTransaction();
        Cursor cursor = database.query(tableName, null, COLUMN_SID + "=?", new String[] { friendInfo.sid }, null, null, null);
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_TYPE, friendInfo.accountType);
        values.put(COLUMN_SID, friendInfo.sid);
        values.put(COLUMN_VERIFY_TYPE, friendInfo.verifyType);
        values.put(COLUMN_FRIEND_MID, friendInfo.mid);
        values.put(COLUMN_SID, friendInfo.sid);
        values.put(COLUMN_MID, mid);
        try {
            if (cursor.getCount() <= 0) {
                database.insert(tableName, null, values);
            } else {
                database.update(tableName, values, COLUMN_SID + "=?", new String[] { friendInfo.sid });
            }
            database.setTransactionSuccessful();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            database.endTransaction();
        }
    }

    public boolean isFriend(String sid) {
        database = FriendsDBHelper.getInstance().getWritableDatabase();
        HSMainAccount mainAccount = HSAccountManager.getInstance().getMainAccount();
        if (mainAccount == null) {
            return false;
        }
        String mid = mainAccount.getMID();
        Cursor cursor = null;
        try {
            cursor = database.query(TABLE_NAME, null, COLUMN_SID + "=? AND " + COLUMN_MID + " =? ", new String[] { sid, mid }, null, null, null);
            if (cursor.moveToFirst()) {
                return true;
            }

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return false;
    }

    //    public void delete(String sid) {
    //        database = FriendsDBHelper.getInstance().getWritableDatabase();
    //        HSMainAccount mainAccount = HSAccountManager.getInstance().getMainAccount();
    //        if (mainAccount == null) {
    //            return;
    //        }
    //        String mid = mainAccount.getMID();
    //        Cursor cursor = null;
    //        try {
    //            cursor = database.query(TABLE_NAME, null, COLUMN_SID + "=? AND " + COLUMN_MID + " =? ", new String[] { sid, mid }, null, null, null);
    //            if (cursor.getCount() <= 0) {
    //                return;
    //            }
    //        } finally {
    //            if (cursor != null) {
    //                cursor.close();
    //                cursor = null;
    //            }
    //        }
    //        database.delete(TABLE_NAME, COLUMN_SID + "=? AND " + COLUMN_MID + " =? ", new String[] { sid, mid });
    //    }
    //    
    //    public String getAccountType(String sid) {
    //        database = FriendsDBHelper.getInstance().getWritableDatabase();
    //        HSMainAccount mainAccount = HSAccountManager.getInstance().getMainAccount();
    //        if (mainAccount == null) {
    //            return null;
    //        }
    //        String mid = mainAccount.getMID();
    //        Cursor cursor = null;
    //        try {
    //            cursor = database.query(TABLE_NAME, null, COLUMN_SID + "=? AND " + COLUMN_MID + " =? ", new String[] { sid, mid }, null, null, null);
    //            if (cursor.moveToFirst()) {
    //                return cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNT_TYPE));
    //            }
    //
    //        } finally {
    //            if (cursor != null) {
    //                cursor.close();
    //                cursor = null;
    //            }
    //        }
    //        return null;
    //    }
    //
    public String getFriendMid(String sid) {
        database = FriendsDBHelper.getInstance().getWritableDatabase();
        HSMainAccount mainAccount = HSAccountManager.getInstance().getMainAccount();
        if (mainAccount == null) {
            return null;
        }
        HSLog.d(TAG, "getting mid of sid: " + sid);
        String mid = mainAccount.getMID();
        Cursor cursor = null;
        try {
            cursor = database.query(TABLE_NAME, null, COLUMN_SID + "=? AND " + COLUMN_MID + " =? ", new String[] { sid, mid }, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(COLUMN_FRIEND_MID));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return null;
    }
    //
    //    /**
    //     * 得到所有friend sid
    //     * 
    //     * @return
    //     */
    //
    //    public ArrayList<String> getFriendSids() {
    //        database = FriendsDBHelper.getInstance().getWritableDatabase();
    //        ArrayList<String> friends = new ArrayList<String>();
    //        HSMainAccount mainAccount = HSAccountManager.getInstance().getMainAccount();
    //        if (mainAccount == null) {
    //            return friends;
    //        }
    //        String mid = mainAccount.getMID();
    //        Cursor cursor = null;
    //        try {
    //            cursor = database.query(TABLE_NAME, null, COLUMN_MID + " =? ", new String[] { mid }, null, null, null);
    //            if (cursor == null || cursor.getCount() == 0) {
    //                return friends;
    //            }
    //            while (cursor.moveToNext()) {
    //                String sid = cursor.getString(cursor.getColumnIndex(COLUMN_SID));
    //                friends.add(sid);
    //            }
    //            return friends;
    //        } finally {
    //            if (cursor != null) {
    //                cursor.close();
    //                cursor = null;
    //            }
    //        }
    //    }
}
