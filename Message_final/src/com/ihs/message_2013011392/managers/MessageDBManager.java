package com.ihs.message_2013011392.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.ihs.app.framework.HSApplication;
import com.ihs.commons.config.HSConfig;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.IMediaProtocol;
import com.ihs.message_2013011392.types.MessageFactory;
import com.ihs.message_2013011392.types.HSBaseMessage.HSMessageMediaStatus;
import com.ihs.message_2013011392.types.HSBaseMessage.HSMessageStatus;

public class MessageDBManager extends SQLiteOpenHelper {
    public static final String COLUMN_SID = "msg_s_id";
    public static final String COLUMN_CID = "msg_c_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_PUSH_TAG = "push_tag";
    public static final String COLUMN_SCENARIO = "scenario";
    public static final String COLUMN_MO = "mo";
    public static final String COLUMN_FROM = "from_mid";
    public static final String COLUMN_TO = "to_mid";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_MEDIA_STATUS = "media_status";
    public static final String COLUMN_LOCAL_FILE_INFO = "local_file_info";
    public static final String COLUMN_IS_MEDIA_READ = "is_media_read";
    public static final String COLUMN_REJECTED = "rejected";

    private static final String COLUMN_P1 = "p1";
    private static final String COLUMN_P2 = "p2";
    private static final String COLUMN_P3 = "p3";
    private static final String COLUMN_P4 = "p4";

    public static final String RECEIPT_COLUMN_MID = "mid";
    public static final String RECEIPT_COLUMN_CID = "msg_c_id";
    public static final String RECEIPT_COLUMN_TYPE = "type";
    private static final String RECEIPT_COLUMN_P1 = "p1";
    private static final String RECEIPT_COLUMN_P2 = "p2";
    private static final String RECEIPT_COLUMN_P3 = "p3";

    private static final String TYPE_TEXT = "text";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_INTEGER = "integer";

    private static String DATABASE_NAME = HSConfig.getString("libMessage", "DBName");
    static int DATABASE_VERION = 1;

    private final static String TAG = MessageDBManager.class.getName();
    private String mMid;
    private HashMap<String, String> mMessageColumns;
    private HashMap<String, String> mReceiptColumns;

    public MessageDBManager(String mid) {
        super(HSApplication.getContext(), DATABASE_NAME, null, DATABASE_VERION);
        mMid = mid;
        {
            mMessageColumns = new HashMap<String, String>();
            mMessageColumns.put(COLUMN_SID, TYPE_TEXT);
            mMessageColumns.put(COLUMN_CID, TYPE_TEXT);
            mMessageColumns.put(COLUMN_TYPE, TYPE_TEXT);
            mMessageColumns.put(COLUMN_PUSH_TAG, TYPE_TEXT);
            mMessageColumns.put(COLUMN_SCENARIO, TYPE_TEXT);
            mMessageColumns.put(COLUMN_MO, TYPE_INTEGER);
            mMessageColumns.put(COLUMN_FROM, TYPE_TEXT);
            mMessageColumns.put(COLUMN_TO, TYPE_TEXT);
            mMessageColumns.put(COLUMN_CONTENT, TYPE_TEXT);
            mMessageColumns.put(COLUMN_TIMESTAMP, TYPE_LONG);
            mMessageColumns.put(COLUMN_STATUS, TYPE_INTEGER);
            mMessageColumns.put(COLUMN_MEDIA_STATUS, TYPE_INTEGER);
            mMessageColumns.put(COLUMN_LOCAL_FILE_INFO, TYPE_TEXT);
            mMessageColumns.put(COLUMN_IS_MEDIA_READ, TYPE_INTEGER);
            mMessageColumns.put(COLUMN_REJECTED, TYPE_INTEGER);
            mMessageColumns.put(COLUMN_P1, TYPE_TEXT);
            mMessageColumns.put(COLUMN_P2, TYPE_TEXT);
            mMessageColumns.put(COLUMN_P3, TYPE_TEXT);
            mMessageColumns.put(COLUMN_P4, TYPE_TEXT);
        }
        {
            mReceiptColumns = new HashMap<String, String>();
            mReceiptColumns.put(RECEIPT_COLUMN_MID, TYPE_TEXT);
            mReceiptColumns.put(RECEIPT_COLUMN_CID, TYPE_TEXT);
            mReceiptColumns.put(RECEIPT_COLUMN_TYPE, TYPE_TEXT);
            mReceiptColumns.put(RECEIPT_COLUMN_P1, TYPE_TEXT);
            mReceiptColumns.put(RECEIPT_COLUMN_P2, TYPE_TEXT);
            mReceiptColumns.put(RECEIPT_COLUMN_P3, TYPE_TEXT);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void doCreateTables() {
        if (TextUtils.isEmpty(this.mMid))
            return;
        SQLiteDatabase db = getWritableDatabase();
        String msgTableName = getMessageTableName();
        StringBuffer sqlSentence = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
        sqlSentence.append(msgTableName);
        sqlSentence.append(" ( ");
        int sum = mMessageColumns.size();
        int count = 0;
        for (String key : mMessageColumns.keySet()) {
            count++;
            String type = mMessageColumns.get(key);
            sqlSentence.append(key);
            sqlSentence.append(" ");
            sqlSentence.append(type);
            if (count < sum) {
                sqlSentence.append(", ");
            }
        }
        sqlSentence.append(");");
        db.execSQL(sqlSentence.toString());
        db.execSQL("CREATE INDEX IF NOT EXISTS MessageIndexTimestamp ON " + msgTableName + " (timestamp);");
        db.execSQL("CREATE INDEX IF NOT EXISTS MessageIndexFrom ON " + msgTableName + " (from_mid);");
        db.execSQL("CREATE INDEX IF NOT EXISTS MessageIndexTo ON " + msgTableName + " (to_mid);");
        db.execSQL("CREATE INDEX IF NOT EXISTS MessageIndexCID ON " + msgTableName + " (msg_c_id);");
    }

    private String getMessageTableName() {
        return "Messages_" + this.mMid;
    }

    public String getMid() {
        return mMid;
    }

    public MessageInsertResult insertMessages(List<HSBaseMessage> messages) {
        ArrayList<HSBaseMessage> insertedMessages = new ArrayList<HSBaseMessage>();
        ArrayList<UnreadCountChange> unreadCountChanges = new ArrayList<UnreadCountChange>();
        MessageInsertResult result = new MessageInsertResult(insertedMessages, unreadCountChanges);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            HashSet<String> chatterMids = new HashSet<String>();
            for (HSBaseMessage msg : messages) {
                HSLog.d(TAG, "insert message with content " + msg);
                String msgID = msg.getMsgID();
                boolean isMessageExisted = isMessageExisted(msgID, db);
                if (!isMessageExisted) {
                    long r = db.insert(getMessageTableName(), null, msg.getDBInfo());
                    insertedMessages.add(msg);
                    HSLog.d(TAG, "insert message result: " + r);
                    chatterMids.add(msg.getChatterMid());
                }
            }
            for (String mid : chatterMids) {
                int count = getMessageUnreadCount(mid);
                unreadCountChanges.add(new UnreadCountChange(mid, count));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public MessageInsertResult insertMessage(HSBaseMessage message) {
        ArrayList<HSBaseMessage> list = new ArrayList<HSBaseMessage>();
        list.add(message);
        return insertMessages(list);
    }

    public void updateMessage(HSBaseMessage message) {
        this.getWritableDatabase().update(getMessageTableName(), message.getDBInfo(), "msg_c_id = ?", new String[] { message.getMsgID() });
    }

    private boolean isMessageExisted(String msgID, SQLiteDatabase db) {
        Cursor c = db.rawQuery("select * from " + getMessageTableName() + " where msg_c_id = ?", new String[] { msgID });
        HSLog.e(TAG, "isMessageExisted: " + c);
        boolean existed = false;
        try {
            if (null != c && c.moveToNext()) {
                existed = true;
            }
        } catch (Exception e) {
        } finally {
            if (null != c)
                c.close();
        }
        return existed;
    }

    public List<HSBaseMessage> queryMessages(String conditions, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        List<HSBaseMessage> messages = new ArrayList<HSBaseMessage>();
        Cursor cursor = db.rawQuery("select * from " + getMessageTableName() + " " + conditions, selectionArgs);
        while (cursor != null && cursor.moveToNext()) {
            HSBaseMessage msg = MessageFactory.messageWithCusor(cursor);
            messages.add(msg);
        }
        if (null != cursor) {
            cursor.close();
        }
        return messages;
    }

    public void deleteAllMessages() {
        this.getWritableDatabase().execSQL("delete from " + getMessageTableName());
    }

    public HSBaseMessage updateMessageMediaStatus(String msgID, int mediaStatus) {
        SQLiteDatabase db = getWritableDatabase();
        HSBaseMessage msg = getMessage(msgID, db);
        if (msg == null) {
            return null;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_MEDIA_STATUS, mediaStatus);
            int result = db.update(getMessageTableName(), cv, " msg_c_id = ?", new String[] { msgID });
            HSLog.d(TAG, "update message media status " + (result > 0 ? "succeeded" : "failed"));
            msg.setMediaStatusBackend(mediaStatus);
            if (mediaStatus == HSMessageMediaStatus.DOWNLOADED.getValue()) {
                msg.setDownloadProgress(1);
            }
            return msg;
        }
    }

    private HSBaseMessage getMessage(String msgID, SQLiteDatabase database) {
        Cursor c = database.rawQuery("select * from " + getMessageTableName() + " where msg_c_id = ?", new String[] { msgID });
        HSBaseMessage msg = null;
        if (c != null && c.moveToNext()) {
            msg = MessageFactory.messageWithCusor(c);
        }
        if (c != null)
            c.close();
        return msg;
    }

    int getMessageUnreadCount(String mid) {
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;
        Cursor c = db.rawQuery("select count(*) as UnreadCount from " + getMessageTableName() + " where from_mid = ? and status = ?", new String[] { mid,
                "" + HSMessageStatus.UNREAD.getValue() });
        try {
            if (c != null && c.moveToNext()) {
                count = c.getInt(c.getColumnIndex("UnreadCount"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return count;
    }

    public static class MessageDBOperationResult {

        List<HSBaseMessage> affectedMessages;
        List<UnreadCountChange> unreadCountChanges;

        public MessageDBOperationResult(List<HSBaseMessage> affectedMessage, List<UnreadCountChange> unreadCountChanges) {
            super();
            this.affectedMessages = affectedMessage;
            this.unreadCountChanges = unreadCountChanges;
        }

        public List<HSBaseMessage> getAffectedMessages() {
            return affectedMessages;
        }

        public List<UnreadCountChange> getUnreadCountChanges() {
            return unreadCountChanges;
        }

    }

    MessageDBOperationResult deleteMessages(List<HSBaseMessage> messages) {
        ArrayList<HSBaseMessage> deletedMessages = new ArrayList<HSBaseMessage>();
        ArrayList<UnreadCountChange> unreadCountChanges = new ArrayList<UnreadCountChange>();
        SQLiteDatabase db = getWritableDatabase();
        HashSet<String> mids = new HashSet<String>();
        String table = getMessageTableName();
        db.beginTransaction();
        for (HSBaseMessage msg : messages) {
            String msgID = msg.getMsgID();
            int result = db.delete(table, "msg_c_id = ?", new String[] { msgID });
            if (result > 0) {
                deletedMessages.add(msg);
                msg.deleteAllMediaFiles();
                if (msg.isMessageOriginate() == false) {
                    mids.add(msg.getFrom());
                }
            }
        }
        for (String mid : mids) {
            int unreadCount = getMessageUnreadCount(mid);
            unreadCountChanges.add(new UnreadCountChange(mid, unreadCount));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return new MessageDBOperationResult(deletedMessages, unreadCountChanges);
    }

    MessageDBOperationResult deleteMessages(String to) {
        ArrayList<UnreadCountChange> unreadCountChanges = new ArrayList<UnreadCountChange>();
        unreadCountChanges.add(new UnreadCountChange(to, 0));
        String table = getMessageTableName();
        List<HSBaseMessage> messagesToDelete = new ArrayList<HSBaseMessage>();
        Cursor c = getReadableDatabase().rawQuery("select * from " + table + " where to_mid = ? or from_mid = ?", new String[] { to, to });
        while (c != null && c.moveToNext()) {
            HSBaseMessage m = MessageFactory.messageWithCusor(c);
            messagesToDelete.add(m);
            m.deleteAllMediaFiles();
        }
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(table, " to_mid = ? or from_mid = ?", new String[] { to, to });
        db.setTransactionSuccessful();
        db.endTransaction();
        return new MessageDBOperationResult(messagesToDelete, unreadCountChanges);
    }

    MessageDBOperationResult markRead(String mid) {
        List<HSBaseMessage> messagesMarked = new ArrayList<HSBaseMessage>();
        ArrayList<UnreadCountChange> unreadCountChanges = new ArrayList<UnreadCountChange>();
        unreadCountChanges.add(new UnreadCountChange(mid, 0));
        String table = getMessageTableName();
        Cursor c = getReadableDatabase().rawQuery("select * from " + table + " where from_mid = ?", new String[] { mid });
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        while (c != null && c.moveToNext()) {
            HSBaseMessage m = MessageFactory.messageWithCusor(c);
            messagesMarked.add(m);
            m.setStatus(HSMessageStatus.READ);
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_STATUS, HSMessageStatus.READ.getValue());
            db.update(table, cv, "msg_c_id = ?", new String[] { m.getMsgID() });
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return new MessageDBOperationResult(messagesMarked, unreadCountChanges);
    }

    MessageDBOperationResult markRead(List<HSBaseMessage> messages) {
        String mid = null;
        List<UnreadCountChange> changes = new ArrayList<UnreadCountChange>();
        MessageDBOperationResult result = new MessageDBOperationResult(messages, changes);
        String table = getMessageTableName();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for (HSBaseMessage message : messages) {
            message.setStatus(HSMessageStatus.READ);
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_STATUS, HSMessageStatus.READ.getValue());
            db.update(table, cv, "msg_c_id = ?", new String[] { message.getMsgID() });
            if (message.isMessageOriginate())
                continue;
            else
                mid = message.getFrom();
            int count = getMessageUnreadCount(mid);
            changes.add(new UnreadCountChange(mid, count));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }

    MessageDBOperationResult markMediaRead(List<HSBaseMessage> messages) {
        List<UnreadCountChange> changes = new ArrayList<UnreadCountChange>();
        MessageDBOperationResult result = new MessageDBOperationResult(messages, changes);
        String table = getMessageTableName();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        for (HSBaseMessage message : messages) {
            if (!(message instanceof IMediaProtocol)) {
                continue; // Ignore non-media messages
            }
            message.setMediaRead(true);
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_MEDIA_STATUS, true);
            db.update(table, cv, "msg_c_id = ?", new String[] { message.getMsgID() });
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return result;
    }
}
