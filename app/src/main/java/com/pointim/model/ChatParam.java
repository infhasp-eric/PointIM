package com.pointim.model;

import org.jivesoftware.smack.packet.*;

import java.util.Date;

/**
 * Created by Eric on 2016/5/20.
 */
public class ChatParam {
    public static int MESSAGE_TYPE_TEXT = 1;

    private org.jivesoftware.smack.packet.Message.Type type;
    private String body;
    private String friendChatJid;
    private Date datetime;//发送的时间

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

    public org.jivesoftware.smack.packet.Message.Type getType() {
        return type;
    }

    public void setType(org.jivesoftware.smack.packet.Message.Type type) {
        this.type = type;
    }

    private String to;
}
