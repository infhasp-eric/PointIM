package com.pointim.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pointim.controller.FriendsController;
import com.pointim.model.AddFriend;
import com.pointim.model.ChatParam;
import com.pointim.model.MessageModel;
import com.pointim.smack.SmackManager;
import com.pointim.task.AddChatParamTask;
import com.pointim.view.activity.ChatActivity;
import com.pointim.view.activity.MainActivity;
import com.pointim.view.fragment.ChatFragment;
import com.pointim.view.fragment.FriendsFragment;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.filetransfer.FileTransfer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Eric
 * on 2016/5/20
 * for project PointIM
 */
public class ChatUtils {

    /**
     * 获取所有好友列表
     */
    public static void getAllFriends(final Handler upHandler) {
        FriendsController.getAllFriends(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                List<AddFriend> list = (List<AddFriend>) data;
                MainActivity.friendList.clear();

                for (AddFriend re : list) {
                    //如果在线则放在第一位
                    if(re.getStatus() != null && re.getStatus().equals("在线")) MainActivity.friendList.add(0, re);
                    //不在线则放在末尾
                    else MainActivity.friendList.add(re);
                    MainActivity.friendMap.put(re.getUsername(), re);
                    //Log.e("Friend", re.getName());
                }
                upHandler.sendMessage(new Message());
            }
        });
    }

    public static List<ChatParam> getChatParams(String chatJid) {
        List<ChatParam> chatParams = MainActivity.chatRecord.get(chatJid);//从临时记录图里面获取到相应的聊天数据列表
        if(chatParams == null) {
            chatParams = new ArrayList<ChatParam>();
            MainActivity.chatRecord.put(chatJid, chatParams);
        }
        return chatParams;
    }

    /**
     * 通过chatJid获取本地好友信息
     * @param chatJid
     * @return
     */
    public static AddFriend getLocalFriendByChatJid(String chatJid) {
        AddFriend friend = null;
        String username = chatJid.replace(("@" + SmackManager.SERVER_NAME), "").replace("/Smack", "");
        Log.e("ChatUtil", "username is " + username);
        for(AddFriend af : MainActivity.friendList) {
            Log.e("ChatUtil", "friend username is " + username);
            if(username.equals(af.getUsername())) {
                friend = af;
                //Log.e("")
                return friend;
            }
        }
        return friend;
    }

    /**
     * 将新信息添加进历史列表
     * @param chatJid
     */
    public static void addNewHistoryChat(String chatJid) {
        final AddFriend friend = getLocalFriendByChatJid(chatJid);
        Log.e("ChatUtil", "有无好友" + (friend == null));

        final MessageModel mmd = new MessageModel();
        mmd.setType(MessageModel.TYPE_CHAT);
        if(friend != null) {
            mmd.setAddFriend(friend);
            ChatFragment.adapter.addItem(mmd);
        } else {
            String username = chatJid.replace(("@" + SmackManager.SERVER_NAME), "").replace("/Smack", "");
            FriendsController.getFriednByUserName(username, new Observer() {
                @Override
                public void update(Observable observable, Object data) {
                    if(data != null && (List<AddFriend>) data != null) {
                        AddFriend af = null;
                        List<AddFriend> result = (List<AddFriend>) data;
                        if (result.size() > 0)
                            af = result.get(0);
                        if(af != null) {
                            mmd.setAddFriend(af);
                            ChatFragment.adapter.addItem(mmd);
                        }
                    }
                }
            });
        }
    }

    /**
     * 检查文件是否接收完成
     * @param transfer
     * @param type		文件类型，语音或图片
     * @param isSend	是否为自己发送的
     * @param username  发送or接收的好友的username
     */
    public static void checkTransferStatus(final FileTransfer transfer, final String file_path, final int type, final boolean isSend, final String username) {
        //final String name = username;
        final ChatParam param = new ChatParam();
        Log.e("file", "type is" + type);
        param.setMessage_type(type);
        param.setSend(isSend);
        param.setFriendChatJid(username);
        param.setDatetime(new Date());
        //param.setMessage_type(ChatParam.TYPE_SOUND);
        param.setFinish(false);
        param.setFile_path(file_path);

        new Thread(){
            public void run() {
                if(transfer.getProgress() < 1) {//传输开始
                    //addChatParm(param, MainActivity.handler);
                    Message msg = new Message();
                    msg.obj = param;
                    msg.what = 1;
                    MainActivity.chatHandler.sendMessage(msg);
                }
                while(!transfer.isDone()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(FileTransfer.Status.complete.equals(transfer.getStatus())) {
                    //传输完成
                    Log.e("chat", "文件传输完成");
                    param.setFinishType(ChatParam.SEND_SUCCESS);
                } else if(FileTransfer.Status.cancelled.equals(transfer.getStatus())) {
                    //传输取消
                    Log.e("chat", "文件传输取消");
                    param.setFinishType(ChatParam.SEND_CALCEL);
                } else if(FileTransfer.Status.error.equals(transfer.getStatus())) {
                    //传输错误
                    Log.e("chat", "文件传输错误" + transfer.getException().getMessage());
                    transfer.getException().printStackTrace();
                    param.setFinishType(ChatParam.SEND_ERROR);
                } else if(FileTransfer.Status.refused.equals(transfer.getStatus())) {
                    //传输拒绝
                    Log.e("chat", "文件传输拒绝");
                    param.setFinishType(ChatParam.SEND_REFUSE);
                }
                if(ChatActivity.isActive) {
                    ChatActivity.handler.sendMessage(new Message());
                }
            };
        }.start();
    }

    /**
     * 将聊天数据存入聊天记录中
     * @param param
     */
    public static void addChatParm(ChatParam param) {
        String chatjid = param.getFriendChatJid().replace("/Spark", "").replace("/Smack", "");
        AddChatParamTask addtask = new AddChatParamTask(chatjid, param);
        MainActivity.workQueue.execute(addtask);
    }

    /**
     * 将聊天数据存入聊天记录中
     * @param param
     */
    public static void addChatParm(ChatParam param, Handler handler) {
        String chatjid = param.getFriendChatJid().replace("/Spark", "").replace("/Smack", "");
        AddChatParamTask addtask = new AddChatParamTask(chatjid, param, handler);
        MainActivity.workQueue.execute(addtask);
    }

    /**
     * 添加数据
     * @param type
     * @param param
     */
    public static void notify(int type, ChatParam param) {
        switch(type) {
            //有内容
            case 1:
                //此时判断是否正在聊天
                //正在聊天
                if(ChatActivity.isActive) {
                    //当前聊天的对象和接收到数据的对象一致
                    if (param.getFriendChatJid().startsWith(ChatActivity.chatJid)) {
                        //将新信息添加进聊天框中
                        //Log.e("Chat", param.getBody());
                        ChatUtils.addChatParm(param, ChatActivity.handler);
                    } else {
                        //在聊天列表中添加新信息提醒
                        ChatUtils.addChatParm(param);
                        //此时应该提示用户有新消息
                        if(FriendsFragment.isEx) {
                            String username = param.getFriendChatJid().replace("@point-im-server/Spark", "").replace("/Smack", "");
                            Log.e("Chat", "username is " + username);
                            //FriendsFragment.setFriendStatus(username);
                        }
                    }
                } else {
                    //在聊天列表中添加新信息提醒
                    ChatUtils.addChatParm(param);
                    //此时应该提示用户有新消息
                    if(FriendsFragment.isEx) {
                        String username = param.getFriendChatJid().replace("@point-im-server/Spark", "").replace("/Smack", "");
                        Log.e("Chat", "username is " + username);
                        //FriendsFragment.setFriendStatus(username);
                    }
                }
                break;
            //无内容
            case 0:
                if(ChatActivity.isActive) {
                    //当前聊天的对象和接收到数据的对象一致
                    if (param.getFriendChatJid().startsWith(ChatActivity.chatJid)) {
                        //提示对方正在输入
                        Log.e("Chat", "33333333333333333333333333333333333");
                    } else {
                        //不做任何操作
                        break;
                    }
                }
                break;
        }
    }

    /**
     * 将图片记录添加进聊天历史中
     * @param filepath
     */
    public static void addImageChatParam(String filepath, String sendUser) {
        ChatParam param = new ChatParam();
        param.setFile_path(filepath);
        param.setFriendChatJid(sendUser);
        param.setMessage_type(ChatParam.TYPE_IMAGE);
        param.setSend(true);
        param.setDatetime(new Date());
        param.setFinish(false);
        android.os.Message msg = new android.os.Message();
        msg.obj = param;
        msg.what = 1;
        MainActivity.chatHandler.sendMessage(msg);
    }
}
