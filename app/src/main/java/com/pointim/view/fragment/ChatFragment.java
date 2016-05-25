package com.pointim.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pointim.R;
import com.pointim.adapter.ChatHistoryAdapter;
import com.pointim.model.AddFriend;
import com.pointim.model.MessageModel;
import com.pointim.view.activity.AddFriendActivity;
import com.pointim.view.activity.ChatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 2016/5/14.
 */
public class ChatFragment extends Fragment {
    private PullToRefreshListView chatList;
    public static ChatHistoryAdapter adapter;

    //保存历史聊天用户
    public static List<MessageModel> historyList;

    //用于刷新列表
    public static Handler update = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.chat_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyList = new ArrayList<MessageModel>();
        initView(view);
    }

    private void initView(View view) {
        chatList = (PullToRefreshListView) view.findViewById(R.id.chat_list);
        adapter = new ChatHistoryAdapter(getContext(), historyList);
        chatList.setAdapter(adapter);

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageModel message = (MessageModel) view.getTag();
                if(message.getType() == MessageModel.TYPE_CHAT) {//为聊天类型的消息
                    message.getAddFriend().setHasMessage(false);
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("user", message.getAddFriend().getUsername());
                    intent.putExtra("nickname", message.getAddFriend().getNickname());
                    message.getAddFriend().setHasMessage(false);
                    startActivity(intent);
                } else {//为好友请求类型的消息
                    Log.e("好友操作", "historyList size " + historyList.size());
                    historyList.remove(message);
                    Log.e("好友操作", "historyList size " + historyList.size());
                    AddFriend af = message.getAddFriend();
                    Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                    intent.putExtra("af", af);
                    startActivity(intent);
                }
                update.sendMessage(new Message());
            }
        });
    }

}
