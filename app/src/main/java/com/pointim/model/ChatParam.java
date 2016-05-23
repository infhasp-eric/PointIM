package com.pointim.model;

import org.jivesoftware.smack.packet.*;

import java.io.File;
import java.util.Date;

/**
 * Created by Eric on 2016/5/20.
 */
public class ChatParam {
    //消息类型
    public static int TYPE_IMAGE = 1;       //图片信息
    public static int TYPE_SOUND = 2;       //音频信息
    public static int MESSAGE_TYPE_TEXT = 3;       //普通文本信息
    public static int TYPE_FILE = 4;        //文件信息
    //消息发送结果
    public static int SEND_SUCCESS = 1;     //发送成功
    public static int SEND_CALCEL = 2;      //发送取消
    public static int SEND_ERROR = 3;       //发送错误
    public static int SEND_REFUSE = 4;      //拒绝接收

    private String body;
    private String friendChatJid;
    private Date datetime;//发送的时间

    public int getFinishType() {
        return finishType;
    }

    public void setFinishType(int finishType) {
        this.finishType = finishType;
    }

    private int finishType;

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    private boolean finish;

    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    private int message_type;

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    private String file_path;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    private String username;//发送人的名称

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    private boolean send;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFriendChatJid() {
        return friendChatJid;
    }

    public void setFriendChatJid(String friendChatJid) {
        this.friendChatJid = friendChatJid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String to;
}
