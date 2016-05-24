package com.pointim.model;

import java.io.Serializable;

/**
 * Created by Eric on 2016/5/16.
 */
public class AddFriend implements Serializable {
    private String username;
    private String nickname;
    private String chatjid;
    private String status;//好友状态
    private String groupname;
    private String remark;
    private boolean hasMessage = false;

    public String getChatjid() {
        return chatjid;
    }

    public void setChatjid(String chatjid) {
        this.chatjid = chatjid;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public boolean isHasMessage() {
        return hasMessage;
    }

    public void setHasMessage(boolean hasMessage) {
        this.hasMessage = hasMessage;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
