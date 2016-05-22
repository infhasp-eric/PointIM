package com.pointim.task;

import android.os.Handler;
import android.os.Message;

import com.pointim.model.ChatParam;
import com.pointim.utils.ChatUtils;
import com.pointim.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eric
 * on 2016/5/20
 * for project PointIM
 */
public class AddChatParamTask implements Runnable{//任务接口
    private ChatParam chatParam;
    private String chatjid;
    private Handler handler;

    public AddChatParamTask(String chatjid, ChatParam chatParam) {
        this.chatjid = chatjid;
        this.chatParam = chatParam;
    }

    public AddChatParamTask(String chatjid, ChatParam chatParam, Handler handler) {
        this.chatjid = chatjid;
        this.chatParam = chatParam;
        this.handler = handler;
    }

    public void run(){
        String name=Thread.currentThread().getName();
        List<ChatParam> chatParamList = ChatUtils.getChatParams(chatjid);
        chatParamList.add(chatParam);
        System.out.println(String.format("Thread %s: ChatParam %s is add in %s on %s",  name,chatParam.getBody(), chatjid, (new Date()).toString()));
        ChatUtils.addNewHistoryChat(chatjid);
        if(handler != null) {
            handler.sendMessage(new Message());
        }
    }
}