package com.ihs.message_2013011392.types;

/**
 * 消息类型的枚举
 * 
 * 用户请忽略除文本、语音、图片、位置消息之外的的其他类型，它们是当前版本暂不使用的消息类型。在线消息（online message）是一类特殊的消息，并不包含在这一枚举中
 */
public enum HSMessageType {
    UNKNOWN(0),
    TEXT(1), // 文本消息
    AUDIO(2), // 语音消息
    IMAGE(3), // 图片消息
    STICKER(4),
    LOCATION(5), // 位置消息
    LINK(6),
    VIDEO(7),
    INTERNAL_LINK(8),
    ACTION(9),
    FILE(10),
    TYPING(11),
    RECEIPT(12),
    LIKE_PLUS(13);

    private int value = -1;

    private HSMessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
