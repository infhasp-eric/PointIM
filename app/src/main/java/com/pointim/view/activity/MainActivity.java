package com.pointim.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pointim.R;
import com.pointim.model.ChatParam;
import com.pointim.smack.SmackManager;
import com.pointim.task.AddChatParamTask;
import com.pointim.utils.DateUtil;
import com.pointim.utils.StringUtils;
import com.pointim.utils.WorkQueue;
import com.pointim.view.fragment.CenterFragment;
import com.pointim.view.fragment.ChatFragment;
import com.pointim.view.fragment.FriendsFragment;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric on 2016/5/15.
 */
public class MainActivity extends FragmentActivity {
    private LinearLayout btChat, btList, btCenter;
    private Fragment chatFragment, friendsFragment, centerFragment;
    private ImageView imgChat, imgList, imgCenter;
    FragmentManager fgManager;

    public List<Fragment> fragments = new ArrayList<Fragment>();
    public List<ImageView> imgs = new ArrayList<ImageView>();
    private int currentTab; // 当前Tab页面索引

    private int[][] backgrounds = {
            {R.mipmap.bt_message, R.mipmap.bt_message},
            {R.mipmap.bt_list,R.mipmap.bt_list},
            {R.mipmap.bt_center, R.mipmap.bt_center}};

    //储存聊天记录，用户jid为key，list保存记录
    public static Map<String, List<ChatParam>> chatRecord;

    //聊天监听器
    public static ChatManagerListener chatManagerListener;

    //线程池
    public static WorkQueue workQueue;

    //接收消息的处理器
    public static Handler chatHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            ChatParam param = (ChatParam) message.obj;
            switch(message.what) {
                //有内容
                case 1:
                    //此时判断是否正在聊天
                    //正在聊天
                    if(ChatActivity.isActive) {
                        //当前聊天的对象和接收到数据的对象一致
                        if (param.getFriendChatJid().startsWith(ChatActivity.chatJid)) {
                            //将新信息添加进聊天框中
                            Log.e("Chat", param.getBody());
                            addChatParm(param, ChatActivity.handler);
                        } else {
                            //在聊天列表中添加新信息提醒
                            addChatParm(param);
                            //此时应该提示用户有新消息
                            if(FriendsFragment.isEx) {
                                String username = param.getFriendChatJid().replace("@admin-pc/Spark", "");
                                Log.e("Chat", "username is " + username);
                                FriendsFragment.setFriendStatus(username);
                            }
                        }
                    } else {
                        //在聊天列表中添加新信息提醒
                        addChatParm(param);
                        //此时应该提示用户有新消息
                        if(FriendsFragment.isEx) {
                            String username = param.getFriendChatJid().replace("@admin-pc/Spark", "");
                            Log.e("Chat", "username is " + username);
                            FriendsFragment.setFriendStatus(username);
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //获取FragmentManager实例
        chatRecord = new HashMap<String, List<ChatParam>>();
        fgManager = getSupportFragmentManager();
        workQueue = new WorkQueue(10);//初始化线程池
        init();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //去除监听类
        SmackManager.getInstance().getChatManager().removeChatListener(chatManagerListener);
        //清空数据
        chatRecord.clear();
        chatRecord = null;
    }

    // 初始化信息
    public void init() {
        //绑定控件
        btChat = (LinearLayout) findViewById(R.id.bt_chat);
        btList = (LinearLayout) findViewById(R.id.bt_list);
        btCenter = (LinearLayout) findViewById(R.id.bt_center);
        imgChat = (ImageView) findViewById(R.id.img_chat);
        imgList = (ImageView) findViewById(R.id.img_list);
        imgCenter = (ImageView) findViewById(R.id.img_center);
        //将图片控件放入列表，方便以后控制
        imgs.add(imgChat);
        imgs.add(imgList);
        imgs.add(imgCenter);
        //新建fragment
        chatFragment = new ChatFragment();
        friendsFragment = new FriendsFragment();
        centerFragment = new CenterFragment();
        //将fragment放入列表中，以后可以直接从管理器里面进行切换管理
        fragments.add(chatFragment);
        fragments.add(friendsFragment);
        fragments.add(centerFragment);
        switchFragment(0);

    }

    //更改显示的fragment
    public void switchFragment(int i) {
        //从之前的列表中取出对应的fragment
        Fragment fragment = fragments.get(i);
        //获取管理器
        FragmentTransaction ft = obtainFragmentTransaction(i);

        getCurrentFragment().onPause(); // 暂停当前tab

        if(fragment.isAdded()){
            fragment.onResume(); // 启动目标tab的onResume()
        }else{
            ft.add(R.id.fragmentRoot, fragment);
        }
        showTab(i); // 显示目标tab
        ft.commit();//涂胶时间
    }

    public Fragment getCurrentFragment(){
        return fragments.get(currentTab);
    }

    /**
     * 获取一个带动画的FragmentTransaction
     * @param index
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction(int index){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // 设置切换动画
        if(index > currentTab){
            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
        }else{
            ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        }
        return ft;
    }


    /**
     * 切换tab
     * @param idx
     */
    private void showTab(int idx){
        for(int i = 0; i < fragments.size(); i++){
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);

            if(idx == i){
                ft.show(fragment);
            }else{
                ft.hide(fragment);
            }
            ft.commit();
        }
        imgs.get(currentTab).setImageResource(backgrounds[currentTab][0]);
        currentTab = idx; // 更新目标tab为当前tab
        imgs.get(currentTab).setImageResource(backgrounds[currentTab][1]);
    }

    /**
     * 初始化监听器
     */
    public void initListener() {
        btChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchFragment(0);
                //changeRadioButtonImage(v.getId());
            }
        });
        btList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(1);
                //changeRadioButtonImage(v.getId());
            }
        });
        btCenter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchFragment(2);
            }
        });

        chatManagerListener = new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                        //接收到消息Message之后进行消息展示处理，这个地方可以处理所有人的消息
                        Message msg = new Message();
                        //判断是否有内容
                        if(StringUtils.isBlank(message.getBody())) {
                            msg.what = 0;
                        } else {
                            msg.what = 1;
                        }
                        ChatParam param = new ChatParam();
                        param.setBody(message.getBody());
                        param.setFriendChatJid(message.getFrom());
                        param.setType(message.getType());
                        param.setTo(message.getTo());
                        param.setDatetime(new Date());
                        msg.obj = param;
                        //发送通知，让程序处理消息
                        chatHandler.sendMessage(msg);
                    }
                });
            }
        };
        SmackManager.getInstance().getChatManager().addChatListener(chatManagerListener);
    }
    /**
     * 将聊天数据存入聊天记录中
     * @param param
     */
    public static void addChatParm(ChatParam param) {
        String chatjid = param.getFriendChatJid().replace("/Spark", "");
        AddChatParamTask addtask = new AddChatParamTask(chatjid, param);
        workQueue.execute(addtask);
    }

    /**
     * 将聊天数据存入聊天记录中
     * @param param
     */
    public static void addChatParm(ChatParam param, Handler handler) {
        String chatjid = param.getFriendChatJid().replace("/Spark", "");
        AddChatParamTask addtask = new AddChatParamTask(chatjid, param, handler);
        workQueue.execute(addtask);
    }
}

