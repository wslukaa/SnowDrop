package com.ihs.demo.message_2013011392;

import java.util.List;

import org.json.JSONObject;

import test.contacts.demo.friends.api.HSContactFriendsMgr;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.account.HSAccountManager.HSAccountSessionState;
import com.ihs.app.framework.HSApplication;
import com.ihs.app.framework.HSSessionMgr;
import com.ihs.commons.keepcenter.HSKeepCenter;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.notificationcenter.INotificationObserver;
import com.ihs.commons.utils.HSBundle;
import com.ihs.commons.utils.HSLog;
import com.ihs.contacts.api.HSPhoneContactMgr;
import com.ihs.message_2013011392.R;
import com.ihs.message_2013011392.managers.HSMessageChangeListener;
import com.ihs.message_2013011392.managers.HSMessageManager;
import com.ihs.message_2013011392.managers.MessageDBManager;
import com.ihs.message_2013011392.types.HSAudioMessage;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSImageMessage;
import com.ihs.message_2013011392.types.HSOnlineMessage;
import com.ihs.message_2013011392.types.HSTextMessage;
import com.ihs.message_2013011392.utils.Utils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class DemoApplication extends HSApplication implements HSMessageChangeListener, INotificationObserver {

    /*
     * 同步好友列表的服务器 URL
     */
    public static final String URL_SYNC = "http://54.223.212.19:8024/template/contacts/friends/get";
    public static final String URL_ACK = "http://54.223.212.19:8024/template/contacts/friends/get";

    private static final String TAG = DemoApplication.class.getName(); // 用于打印 log

    @Override
    public void onCreate() {
        super.onCreate();

        HSAccountManager.getInstance();

        doInit();

        initImageLoader(this);

        // 初始化百度地�? SDK
        SDKInitializer.initialize(getApplicationContext());

        // 初始化�?�讯录管理类，同步�?�讯录，用于生成好友列表
        HSPhoneContactMgr.init();
        HSPhoneContactMgr.enableAutoUpload(true);
        HSPhoneContactMgr.startSync();

        // 初始化好友列表管理类，同步好友列�?
        HSContactFriendsMgr.init(this, null, URL_SYNC, URL_ACK);
        HSContactFriendsMgr.startSync(true);

        // 将本类添加为 HSMessageManager 的监听�?�，监听各类消息变化事件
        // 参见 HSMessageManager 类与 HSMessageChangeListener 接口
        HSMessageManager.getInstance().addListener(this, new Handler());

        // �? HSGlobalNotificationCenter 功能设定监听接口
        INotificationObserver observer = this;
        HSGlobalNotificationCenter.addObserver(SampleFragment.SAMPLE_NOTIFICATION_NAME, observer);// 演示HSGlobalNotificationCenter功能：增加名�? SAMPLE_NOTIFICATION_NAME 的观察�??
    }

    public static void doInit() {
        HSLog.d(TAG, "doInit invoked");

        // 验证登录状�??
        if (HSAccountManager.getInstance().getSessionState() == HSAccountSessionState.VALID) {
            HSLog.d(TAG, "doInit during session is valid");
            HSMessageManager.getInstance();

            // 初始化长连接服务管理�? HSKeepCenter
            // �?传入标记应用�? App ID、标记帐户身份的 mid 和标记本次登录的 Session ID，三项信息均可从 HSAccountManager 获得
            HSKeepCenter.getInstance().set(HSAccountManager.getInstance().getAppID(), HSAccountManager.getInstance().getMainAccount().getMID(),
                    HSAccountManager.getInstance().getMainAccount().getSessionID());
            // 建立长连�?
            HSKeepCenter.getInstance().connect();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 返回配置文件�?
     */
    @Override
    protected String getConfigFileName() {
        return "config.ya";
    }

    public static void initImageLoader(Context context) {

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * 返回多媒体消息的文件存储路径
     */
    void getMediaFilePath() {
        HSLog.d("getMediaFilePath: ", Utils.getMediaPath());
    }

    /**
     * 收到 “正在输入�?? 消息时被调用
     * 
     * @param fromMid “正在输入�?? 消息发�?��?�的 mid
     */
    @Override
    public void onTypingMessageReceived(String fromMid) {

    }

    /**
     * 收到在线消息时被调用
     * 
     * @param message 收到的在线消息，�? content 值由用户定制，可实现自己的�?�讯协议和交互�?�辑
     */
    @Override
    public void onOnlineMessageReceived(HSOnlineMessage message) {
        HSLog.d(TAG, "onOnlineMessageReceived");

        // 弹出 Toast 演示示例在线消息�? content 消息体内�?
        HSBundle bundle = new HSBundle();
        bundle.putString(SampleFragment.SAMPLE_NOTIFICATION_BUNDLE_STRING, message.getContent().toString());
        HSGlobalNotificationCenter.sendNotificationOnMainThread(SampleFragment.SAMPLE_NOTIFICATION_NAME, bundle);
    }

    /**
     * 当来自某人的消息中，未读消息数量发生变化时被调用
     * 
     * @param mid 对应人的 mid
     * @param newCount 变化后的未读消息数量
     */
    @Override
    public void onUnreadMessageCountChanged(String mid, int newCount) {
        // 消息未读数量的变化大家可以在这里进行处理，比如修改每条会话的未读数量等�??
    }

    /**
     * 当收到服务器通过长连接发送过来的推�?��?�知时被调用，用途是进行新消息在通知窗口的�?�知，�?�知格式如下�? alert 项为提示文字，fmid 代表是哪�? mid 发来的消�?
     * {"act":"msg","aps":{"alert":"@: sent to a message","sound":"push_audio_1.wav","badge":1},"fmid":"23"}
     * 
     * @param pushInfo 收到通知的信�?
     */
    @Override
    public void onReceivingRemoteNotification(JSONObject userInfo) {
        HSLog.d(TAG, "receive remote notification: " + userInfo);
        if (HSSessionMgr.getTopActivity() == null) {
            // 大家在这里做通知中心的�?�知即可
        	
        }
    }

    /**
     * 有消息发生变化时的回调方�?
     * 
     * @param changeType 变化种类，消息增�? / 消息删除 / 消息状�?�变�?
     * @param messages 变化涉及的消息对�?
     */
    @Override
    public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages) {
        // 同学们可以根�? changeType 的消息增加�?�删除�?�更新信息进行会话数据的构建
        if (changeType == HSMessageChangeType.ADDED && !messages.isEmpty()) {
        	int notifyIndex = 0;
        	for (int i = 0; i < messages.size(); i++){
        		MediaPlayer player;
                player = MediaPlayer.create(this,messages.get(i).isMessageOriginate()?R.raw.message_ringtone_sent:R.raw.message_ringtone_received);
                player.start();
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.release();
                    }
                });
        		if (HSSessionMgr.getTopActivity() == null 
        		&& !HSAccountManager.getInstance().getMainAccount().getMID().equals(messages.get(i).getFrom())
        		){
//        			MessageDBManager dbManager = new MessageDBManager(messages.get(i).getFrom());
//        			dbManager.doCreateTables();
//        			dbManager.insertMessage(messages.get(i)); 
        			notifyIndex = Integer.parseInt(messages.get(i).getChatterMid());
        			String mySer = Context.NOTIFICATION_SERVICE;
        			NotificationManager myNoManager = (NotificationManager) getSystemService(mySer);
        			//通知栏显示的内容
        			int icon = R.drawable.notification_icon;
        			CharSequence fText = "新消息来啦！";
        			long nowTime = System.currentTimeMillis();
                    //拉下来后的具体内容
                    Context context = getApplicationContext();
                    String contact = FriendManager.getInstance().getFriend(messages.get(i).getChatterMid()).getName();
                    CharSequence title = "这是来自"+contact+"的"+HSMessageManager.getInstance().queryUnreadCount(messages.get(i).getChatterMid())+"条消息";
                    CharSequence text = "";
                    if (messages.get(i) instanceof HSTextMessage)
                    	text = ((HSTextMessage)messages.get(i)).getText();
                    if (messages.get(i) instanceof HSImageMessage)
                    	text = contact+"发来一个图片消息";
                    if (messages.get(i) instanceof HSAudioMessage)
                    	text = contact+"发来一条语音消息";
                    //跳转到相应的聊天界面
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("name", contact);
                    intent.putExtra("mid", messages.get(i).getFrom());
                    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notify2 = new Notification.Builder(this)  
                            .setSmallIcon(R.drawable.notification_icon)
                            .setTicker("新消息来啦！")
                            .setContentTitle(title)
                            .setContentText(text)
                            .setContentIntent(pIntent)
                            .getNotification();                   
                    myNoManager.notify(notifyIndex, notify2);
        		}
        	}
        }
        
    }

    /**
     * 收到推�?��?�知时的回调方法
     */
    @Override
    public void onReceive(String notificaitonName, HSBundle bundle) {
        // �? HSGlobalNotificationCenter 功能参�?�，弹出 Toast 演示通知的效�?
        String string = TextUtils.isEmpty(bundle.getString(SampleFragment.SAMPLE_NOTIFICATION_BUNDLE_STRING)) ? "消息为空" : bundle
                .getString(SampleFragment.SAMPLE_NOTIFICATION_BUNDLE_STRING); // 取得 bundle 中的信息
        Toast toast = Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG);
        toast.show();
    }

}
