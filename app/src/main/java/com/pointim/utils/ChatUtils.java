package com.pointim.utils;

import com.pointim.model.ChatParam;
import com.pointim.task.AddChatParamTask;
import com.pointim.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric
 * on 2016/5/20
 * for project PointIM
 */
public class ChatUtils {

    public static List<ChatParam> getChatParams(String chatJid) {
        List<ChatParam> chatParams = MainActivity.chatRecord.get(chatJid);//从临时记录图里面获取到相应的聊天数据列表
        if(chatParams == null) {
            chatParams = new ArrayList<ChatParam>();
            MainActivity.chatRecord.put(chatJid, chatParams);
        }
        return chatParams;
    }
}
