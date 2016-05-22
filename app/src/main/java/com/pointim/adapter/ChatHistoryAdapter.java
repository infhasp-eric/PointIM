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
    private List<AddFriend> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    private Map<String, AddFriend> hasMap = new HashMap<String, AddFriend>();//储存是否已有的好友

    public ChatHistoryAdapter(Context context, List<AddFriend> mList) {
        this.mList = mList;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public AddFriend getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddFriend viewTag;
        TextView nickName;
        TextView username;
        TextView hasMessage;

        convertView = mInflater.inflate(R.layout.item_friend, null);
        // construct an item tag
        viewTag = mList.get(position);
        convertView.setTag(viewTag);
        try {
            nickName = (TextView) convertView.findViewById(R.id.nickname);
            if (StringUtils.isBlank(viewTag.getRemark()))
                nickName.setText(viewTag.getNickname());
            else
                nickName.setText(viewTag.getRemark());
            username = (TextView) convertView.findViewById(R.id.username);
            username.setText(viewTag.getUsername());
            hasMessage = (TextView) convertView.findViewById(R.id.has_message);
            //设置影藏显示
            if (viewTag.isHasMessage()) hasMessage.setVisibility(View.VISIBLE);
            else hasMessage.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    /**
     * 添加对象
     * @param friend
     */
    public void addItem(AddFriend friend) {
        friend.setHasMessage(true);
        if(ChatActivity.isActive) {
            //当前聊天的对象和接收到数据的对象一致
            if (ChatActivity.chatJid.startsWith(friend.getUsername())) {
                friend.setHasMessage(false);
            }
        }
        AddFriend has = hasMap.get(friend.getUsername());
        if(has == null) {
            mList.add(0, friend);
            hasMap.put(friend.getUsername(), friend);
        } else {
            Log.e("ChatHistory", "remove last " + mList.size());
            mList.remove(has);
            mList.add(0, friend);
            hasMap.put(friend.getUsername(), friend);
            Log.e("ChatHistory", "remove after " + mList.size());
        }
        ChatFragment.update.sendMessage(new Message());
    }
}
