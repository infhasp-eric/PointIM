package com.pointim.adapter;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pointim.R;
import com.pointim.model.AddFriend;
import com.pointim.model.MessageModel;
import com.pointim.utils.StringUtils;
import com.pointim.view.activity.ChatActivity;
import com.pointim.view.fragment.ChatFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric on 2016/5/20.
 */
public class ChatHistoryAdapter extends BaseAdapter {
    private List<MessageModel> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    private Map<String, MessageModel> hasMap = new HashMap<String, MessageModel>();//储存是否已有的好友

    public ChatHistoryAdapter(Context context, List<MessageModel> mList) {
        this.mList = mList;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public MessageModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageModel viewTag = mList.get(position);
        TextView nickName;
        TextView username;
        TextView hasMessage;
        convertView = mInflater.inflate(R.layout.item_friend, null);
        nickName = (TextView) convertView.findViewById(R.id.nickname);
        if(viewTag.getType() == MessageModel.TYPE_CHAT) {
            // construct an item tag
            convertView.setTag(viewTag);
            try {
                if (StringUtils.isBlank(viewTag.getAddFriend().getRemark()))
                    nickName.setText(viewTag.getAddFriend().getNickname());
                else
                    nickName.setText(viewTag.getAddFriend().getRemark());
                username = (TextView) convertView.findViewById(R.id.username);
                username.setText(viewTag.getAddFriend().getUsername());
                hasMessage = (TextView) convertView.findViewById(R.id.has_message);
                //设置影藏显示
                if (viewTag.getAddFriend().isHasMessage()) hasMessage.setVisibility(View.VISIBLE);
                else hasMessage.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            nickName.setText("新的好友请求");
            convertView.setTag(viewTag);
        }
        return convertView;
    }

    /**
     * 添加对象
     * @param message
     */
    public void addItem(MessageModel message) {
        message.getAddFriend().setHasMessage(true);
        try {
            if (ChatActivity.isActive) {
                //当前聊天的对象和接收到数据的对象一致
                if (ChatActivity.chatJid.startsWith(message.getAddFriend().getUsername())) {
                    message.getAddFriend().setHasMessage(false);
                }
            }
        } catch (ExceptionInInitializerError e) {
            e.printStackTrace();
        }
        MessageModel has = hasMap.get(message.getAddFriend().getUsername());
        if (has == null) {
            mList.add(0, message);
            hasMap.put(message.getAddFriend().getUsername(), message);
        } else {
            mList.remove(has);
            mList.add(0, message);
            hasMap.put(message.getAddFriend().getUsername(), message);
        }
        ChatFragment.update.sendMessage(new Message());
    }
}
