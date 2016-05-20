package com.pointim.controller;

import com.pointim.model.ChatParam;
import com.pointim.smack.SmackManager;
import com.pointim.utils.DateUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;

import java.util.Date;
import java.util.Observer;

/**
 * Created by Eric
 * on 2016/5/20
 * for project PointIM
 */
public class ChatController {

    public static void sendMessage(final Chat chat, final ChatParam param, final Observer observer) {
        new Thread(){
            public void run() {
                try {
                    chat.sendMessage(param.getBody());
                    //String currNickname = SmackManager.getInstance().getAccountName();
                    //com.pointim.model.Message msg = new com.pointim.model.Message(com.pointim.model.Message.MESSAGE_TYPE_TEXT, currNickname, DateUtil.formatDatetime(new Date()), true);
                    //msg.setContent(param.getBody());
                    param.setSend(true);
                    param.setDatetime(new Date());
                    //handler.obtainMessage(1, msg).sendToTarget();
                    observer.update(null, param);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    observer.update(null, null);
                }
            };
        }.start();

    }

}
