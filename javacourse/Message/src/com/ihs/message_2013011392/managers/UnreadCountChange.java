package com.ihs.message_2013011392.managers;

public class UnreadCountChange {

    private String mid;
    private int unreadCount;

    public UnreadCountChange(String mid, int unreadCount) {
        super();
        this.mid = mid;
        this.unreadCount = unreadCount;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

}
