package com.ihs.message_2013011392.managers;

import java.util.List;

import com.ihs.message_2013011392.types.HSBaseMessage;

public class MessageInsertResult {

    List<HSBaseMessage> messages;
    List<UnreadCountChange> changes;

    public MessageInsertResult(List<HSBaseMessage> messages, List<UnreadCountChange> changes) {
        super();
        this.messages = messages;
        this.changes = changes;
    }

    public List<HSBaseMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<HSBaseMessage> messages) {
        this.messages = messages;
    }

    public List<UnreadCountChange> getChanges() {
        return changes;
    }

    public void setChanges(List<UnreadCountChange> changes) {
        this.changes = changes;
    }

}
