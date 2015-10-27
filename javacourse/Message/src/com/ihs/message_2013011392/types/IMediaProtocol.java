package com.ihs.message_2013011392.types;

/**
 * 多媒体消息的接口
 * 
 * 多媒体消息指语音消息与图片消息，它们支持多媒体文件的下载和取消下载方法
 */
public interface IMediaProtocol {

    void download();
    void cancelDownlad();
}
