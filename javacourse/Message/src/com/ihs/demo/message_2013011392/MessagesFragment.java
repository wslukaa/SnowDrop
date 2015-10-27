package com.ihs.demo.message_2013011392;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONObject;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.message_2013011392.R;
import com.ihs.message_2013011392.managers.HSMessageChangeListener;
import com.ihs.message_2013011392.managers.HSMessageManager;
import com.ihs.message_2013011392.managers.HSMessageChangeListener.HSMessageChangeType;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSOnlineMessage;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MessagesFragment extends Fragment {

	private ListView listView;
    private MessagesAdapter adapter = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    private HSMessageChangeListener messageListener = new HSMessageChangeListener(){

		@Override
		public void onMessageChanged(HSMessageChangeType changeType, List<HSBaseMessage> messages) {
			//增加消息时更新
			if (changeType == HSMessageChangeType.ADDED && !messages.isEmpty()) {
            	for (int i = 0; i < messages.size(); i++){
            		if (!HSAccountManager.getInstance().getMainAccount().getMID().equals(messages.get(i).getFrom())){
//            			MessageDBManager dbManager = new MessageDBManager(messages.get(i).getFrom());
//            			dbManager.doCreateTables();
//            			dbManager.insertMessage(messages.get(i));           			
            			refresh();
            		}
            	}
            }
		}
		@Override
		public void onTypingMessageReceived(String fromMid) {
		}
		@Override
		public void onOnlineMessageReceived(HSOnlineMessage message) {
		}
		@Override
		public void onUnreadMessageCountChanged(String mid, int newCount) {
		}
		@Override
		public void onReceivingRemoteNotification(JSONObject pushInfo) {
		}  	
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        listView = (ListView) view.findViewById(R.id.message_list);      
        final List<MessagesItem> messages = new ArrayList<MessagesItem>();
        adapter = new MessagesAdapter(this.getActivity(), R.layout.cell_item_message, messages);
        listView.setAdapter(adapter);        
        HSMessageManager.getInstance().addListener(messageListener, new Handler());
        
        refresh();
        listView.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//长按删除同一人的所有聊天记录
				final String mid = messages.get(position).getMid();
                final String name = messages.get(position).getName();
                //确认删除对话框
                AlertDialog.Builder dialog = new Builder(getActivity());
                dialog.setMessage("你确认删除同此人的全部聊天记录吗？");
                dialog.setTitle("警告！");
                dialog.setPositiveButton("是", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						HSMessageManager.getInstance().deleteMessages(mid);
						refresh();
					}
                });
                dialog.setNegativeButton("否", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}            	
                });
                dialog.show();
				return true;
			}
        	
        });
        
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mid = messages.get(position).getMid();
                String name = messages.get(position).getName();
//                Toast.makeText(getActivity(), "你点击了名字为：" + name + " mid为：" + mid + "的联系人，需要在这里跳转到同此人的聊天界面（�?个Activity�?", Toast.LENGTH_LONG).show();
                //跳转到同此人的聊天界面
                Intent intent = new Intent(MessagesFragment.this.getActivity(), ChatActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("mid",mid);
                startActivity(intent);
            }
        });       
        return view;
    }

    //刷新页面
    void refresh() {
        adapter.getMessages().clear();
        List<Contact> friends = FriendManager.getInstance().getAllFriends();
        for (int i = 0; i < friends.size(); i++){
        	//将有最后一条消息的联系人加入
        	MessagesItem item = new MessagesItem(friends.get(i));
        	if ((item.getLastMessage() != null) && 
        		(!item.getContact().getMid().equals(HSAccountManager.getInstance().
        				getMainAccount().getMID()))){
        		item.setNotReadNum(HSMessageManager.getInstance().
        				queryUnreadCount(item.getContact().getMid()));
        		adapter.getMessages().add(item);
        	}
        }        
        Collections.sort(adapter.getMessages(), new Comparator<MessagesItem>(){
        	//按时间先后排序
        	@Override
        	public int compare(MessagesItem x, MessagesItem y){
        		if (x.getLastMessage().getTimestamp().after(y.getLastMessage().getTimestamp()))
        			return -1;
        		else if (x.getLastMessage().getTimestamp().before(y.getLastMessage().getTimestamp()))
        			return 1;
        		return 0;
        	}
        });
        adapter.notifyDataSetChanged();   	
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	refresh();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    	HSMessageManager.getInstance().removeListener(messageListener);
    }
}
