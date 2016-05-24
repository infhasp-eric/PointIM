package com.pointim.model;

/**
 * 消息模型
 * Created by Eric on 2016/5/25.
 */
public class MessageModel {
    public static int TYPE_CHAT = 1;//聊天状态
    public static int TYPE_REQUEST = 2;//好友请求状态

    private int type;
    private AddFriend addFriend;//好友数据

    public AddFriend getAddFriend() {
        return addFriend;
    }

    public void setAddFriend(AddFriend addFriend) {
        this.addFriend = addFriend;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
