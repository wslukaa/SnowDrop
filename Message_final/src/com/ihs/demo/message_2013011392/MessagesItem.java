package com.ihs.demo.message_2013011392;

import java.util.List;

import com.ihs.message_2013011392.managers.HSMessageManager;
import com.ihs.message_2013011392.managers.MessageDBManager;
import com.ihs.message_2013011392.managers.HSMessageManager.QueryResult;
import com.ihs.message_2013011392.types.HSBaseMessage;

public class MessagesItem{
    private int notReadNum = 0;
    private HSBaseMessage lastMessage = null;
    private Contact contact;

    public Contact getContact(){
    	return contact;
    }
    
    public void setContact(Contact contact){
    	this.contact = contact;
    }
    
    public int getNotReadNum() {
        return notReadNum;
    }
    
    public void setNotReadNum(int num) {
    	notReadNum = num;
    }
    
    public void setLastMessage(HSBaseMessage lastMessage){
    	this.lastMessage = lastMessage;
    }
    
    public HSBaseMessage getLastMessage(){
    	return lastMessage;
    }
    
    public String getMid() {
        return contact.mid;
    }

    public void setNotReadNum(String mid) {
    	contact.mid = mid;
    }

    public String getName() {
        return contact.name;
    }

    public void setName(String name) {
    	contact.name = name;
    }

    public MessagesItem(Contact contact) {
        this.contact = contact;
        //得到最后一条消息
        QueryResult result = HSMessageManager.getInstance().queryMessages(contact.getMid(), 10, -1);
        List<HSBaseMessage> chatHistory = result.getMessages();

        if (chatHistory.size() != 0){
        	lastMessage = chatHistory.get(0);
        }
/*        MessageDBManager manager = new MessageDBManager(contact.getMid());
        manager.doCreateTables();
        String condition = "order by "+MessageDBManager.COLUMN_TIMESTAMP;
        List<HSBaseMessage> all = manager.queryMessages(condition, new String[]{});
        if (all.size() != 0)
        	lastMessage = all.get(all.size()-1);   */   
    }

}
