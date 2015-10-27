/**
 * 
 */
package test.contacts.demo.friends.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ihs.commons.utils.HSLog;

public class FriendsDBHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称
     */
    public final static String MONKEY_DB_NAME = "contactFriends"; // 数据库名称

    /**
     * 数据库名称
     */
    private final static String DB_NAME = "contactFriends"; // 数据库名称

    private final static int DB_TABLE_VERSION_1 = 1;
    private final static int DB_TABLE_VERSION = DB_TABLE_VERSION_1; // 数据库有变动需要升级数据库

    /**
     * 扩展字段列名
     */
    public final static String EXTENDED_DATA1 = "data1";// 扩展字段data1
    public final static String EXTENDED_DATA2 = "data2";// 扩展字段data2
    public final static String EXTENDED_DATA3 = "data3";// 扩展字段data3

    private static FriendsDBHelper dbHelper;
    protected Context context;
    /**
     * this must init as early as possbile.
     * 
     * @param context
     * @return
     */
    public static synchronized FriendsDBHelper init(Context context) {
        if (dbHelper == null) {
            dbHelper = new FriendsDBHelper(context);
        }
        return dbHelper;
    }

    public static synchronized FriendsDBHelper getInstance() {
        return dbHelper;
    }

    protected FriendsDBHelper(Context context) {
        super(context, MONKEY_DB_NAME, null, DB_TABLE_VERSION);
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        HSLog.d("DBHelper creating database with version " + db.getVersion());
        // Note that numeric arguments in parentheses that following the type
        // name (ex: "VARCHAR(255)") are ignored by SQLite - SQLite does not
        // impose any length restrictions (other than the large global
        // SQLITE_MAX_LENGTH limit) on the length of strings, BLOBs or numeric
        // values.
        // ref:http://www.sqlite.org/datatype3.html

        try {
            db.beginTransaction();

            creataTables(db);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 执行不成功则回滚
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        HSLog.d("FriendsDBHelper upgrading database from " + oldVersion + " to " + newVersion);
        db.beginTransaction();
        try {
            creataTables(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    protected void creataTables(SQLiteDatabase db) {
        HSLog.d("start creating all tables....");
        ContactFriendsDao.createTable(db);
        HSLog.d("init db finished, created all tables");
    }


}
