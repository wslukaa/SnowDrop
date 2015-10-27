package com.ihs.demo.message_2013011392;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.app.framework.HSSessionMgr;
import com.ihs.commons.utils.HSError;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.R;
import com.ihs.message_2013011392.managers.HSMessageChangeListener;
import com.ihs.message_2013011392.managers.HSMessageManager;
import com.ihs.message_2013011392.managers.MessageDBManager;
import com.ihs.message_2013011392.managers.HSMessageChangeListener.HSMessageChangeType;
import com.ihs.message_2013011392.managers.HSMessageManager.QueryResult;
import com.ihs.message_2013011392.managers.HSMessageManager.SendMessageCallback;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSOnlineMessage;
import com.ihs.message_2013011392.types.HSTextMessage;
import com.ihs.message_2013011392.types.HSBaseMessage.HSMessageStatus;
import com.ihs.message_2013011392.types.HSImageMessage;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;

public class ChatActivity extends HSActionBarActivity {

    private EditText messageET;
    private RefreshListView messagesContainer;
    private Button sendBtn, imgBtn;
    private ChatAdapter adapter;
    private List<HSBaseMessage> chatHistory;
    private List<HSBaseMessage> viewMessages;
    private String name;
    private String mid;
    private final static String TAG = SampleFragment.class.getName();
    private long cursor = -1;
    private List<HSBaseMessage> deletedMessage;
    private static final int IMAGE = 1;
    private NotificationManager myNoManager;

    //重载onStart函数，清除所有的消息通知
    @Override
    protected void onStart(){
    	super.onStart();
    	String mySer = Context.NOTIFICATION_SERVICE;
    	myNoManager = (NotificationManager) getSystemService(mySer);    	
    	myNoManager.cancelAll();
    }
    
    //监听消息变化
    private HSMessageChangeListener listener = new HSMessageChangeListener(){
    	public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages){
			if (changeType == HSMessageChangeType.ADDED && !messages.isEmpty()) {
            	for (int i = 0; i < messages.size(); i++){           		
        			if (messages.get(i).getChatterMid().equals(mid)){
        				if (messages.get(i) instanceof HSImageMessage)
        					displayMessage(messages.get(i));
        				else if (!HSAccountManager.getInstance().getMainAccount().getMID().equals(messages.get(i).getFrom())){
//                			MessageDBManager dbManager = new MessageDBManager(messages.get(i).getFrom());
//                			dbManager.doCreateTables();
//                			dbManager.insertMessage(messages.get(i));
                			displayMessage(messages.get(i));
//                			messages.get(i).setStatus(HSMessageStatus.READ);        					
        				}
               		}
            	}
            }
			if (changeType == HSMessageChangeType.DELETED && !messages.isEmpty()){
				for (int i = 0; i < messages.size(); i++){
					adapter.notifyDataSetChanged();
				}
			}
			if (changeType == HSMessageChangeType.UPDATED && !messages.isEmpty()){
				for (int i = 0; i < messages.size(); i++){
					if (messages.get(i).getStatus() == HSMessageStatus.FAILED)
						Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
					if (messages.get(i).getStatus() == HSMessageStatus.SENDING)
						Toast.makeText(ChatActivity.this, "正在发送", Toast.LENGTH_SHORT).show();
					if (messages.get(i).getStatus() == HSMessageStatus.SENT)
						Toast.makeText(ChatActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				}
			}
    	}
    	@Override
    	public void onTypingMessageReceived(String fromMid){}
    	@Override
    	public void onOnlineMessageReceived(HSOnlineMessage message){}
    	@Override
    	public void onUnreadMessageCountChanged(String mid, int newCount){}
    	@Override
    	public void onReceivingRemoteNotification(JSONObject pushInfo){}
    };
    
    //实现消息的单条删除
    private void deleteMessage(){
    	AlertDialog.Builder dialog = new Builder(this);
        dialog.setMessage("你确认删除此条聊天记录吗？");
        dialog.setTitle("警告！");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				HSMessageManager.getInstance().deleteMessages(deletedMessage);
				deletedMessage.clear();
				cursor = -1;
				loadHistory();
			}			
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}       	
        });
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	HSLog.d("!!!in onCreat");
    	deletedMessage = new ArrayList <HSBaseMessage>();
    	viewMessages = new ArrayList <HSBaseMessage>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_show);
        //接收名字和mid内容
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        mid = intent.getStringExtra("mid");
        
        HSMessageManager.getInstance().addListener(listener, new Handler());        
        HSMessageManager.getInstance().markRead(mid);
        initControls();        
    }
   
    private void initControls() {
        messagesContainer = (RefreshListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);
        imgBtn = (Button) findViewById(R.id.ImageSendButton);
        HSLog.d("!!!in initControls");

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText(name+","+mid);
        loadHistory();
        
        messagesContainer.setRefreshEnableState(true);
        messagesContainer.setOnRefreshListener(new RefreshListView.OnRefreshListener() {		
			@Override
			public void onRefresh() {
				QueryResult result = HSMessageManager.getInstance().queryMessages(mid, 10, cursor);
		    	chatHistory.addAll(result.getMessages());
		    	cursor = result.getCursor();
		    	//已读完所有的记录
		    	if (result.getMessages().size() < 10)
		    		messagesContainer.setRefreshEnableState(false);
		    	
		    	adapter = new ChatAdapter(ChatActivity.this, new ArrayList<HSBaseMessage>()); 
		        messagesContainer.setAdapter(adapter);
		        
		        for(int i = chatHistory.size()-1; i >= 0; i--) {
		            HSBaseMessage message = chatHistory.get(i);
		    		message.setStatus(HSMessageStatus.READ);
		            displayMessage(chatHistory.get(i));
		        }
		        //加载出新的之后回到顶部
		        messagesContainer.onRefreshComplete(1);
			}
		});
        
        //发送
        sendBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                HSBaseMessage chatMessage = new HSTextMessage(mid, messageText);             
                messageET.setText("");
                displayMessage(chatMessage);
                
                HSMessageManager.getInstance().send(chatMessage, new SendMessageCallback() {
                    @Override
                    public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                        HSLog.d(TAG, "success: " + success);
                    }
                }, new Handler());
            }
        });
        
        //弹出照片选择窗口
        imgBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent imgIntent = new Intent(Intent.ACTION_GET_CONTENT);
				imgIntent.setType("image/*");
				startActivityForResult(imgIntent, IMAGE);
			}
		});

        //长按监听，删除单条消息
        messagesContainer.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
				deletedMessage.add(viewMessages.get(position-1));
				deleteMessage();
				return true;
			}
        });
    }
    
    //图片发送时自动调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	if (resultCode != RESULT_OK){
    		return;
    	}
    	String path = "";
    	if (requestCode == IMAGE){
    		try{
    			Uri origUri = intent.getData();
    			String[] proj = {MediaStore.Images.Media.DATA};
    			Cursor cursor = managedQuery(origUri, proj, null, null, null);
    			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    			cursor.moveToFirst();
    			path = cursor.getString(column_index);
    			HSImageMessage img = new HSImageMessage(mid, path);
    			HSMessageManager.getInstance().send(img, new HSMessageManager.SendMessageCallback() {				
					@Override
					public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
					}
				}, new Handler());
    		}catch (Exception e){    			
    		}
    	}
    }

    //显示单条消息
    public void displayMessage(HSBaseMessage message){
        adapter.add(message);
        adapter.notifyDataSetChanged();
        if (HSSessionMgr.getTopActivity() != null)
        	HSMessageManager.getInstance().markRead(mid);
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    //加载历史消息
    private void loadHistory(){
    	if (cursor == -1){
    		viewMessages.clear();
    	}
    	QueryResult result = HSMessageManager.getInstance().queryMessages(mid, 10, cursor);
    	chatHistory = result.getMessages();
    	cursor = result.getCursor();
       
        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<HSBaseMessage>()); 
        messagesContainer.setAdapter(adapter);
        
        for(int i = chatHistory.size()-1; i >= 0; i--) {
            HSBaseMessage message = chatHistory.get(i);
    		message.setStatus(HSMessageStatus.READ);
            displayMessage(chatHistory.get(i));
            viewMessages.add(chatHistory.get(i));
        }       
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	HSMessageManager.getInstance().markRead(mid);
    }
    
    public void onDestroy(){
    	super.onDestroy();  
        HSMessageManager.getInstance().markRead(mid);
    	HSMessageManager.getInstance().removeListener(listener);
    }
}