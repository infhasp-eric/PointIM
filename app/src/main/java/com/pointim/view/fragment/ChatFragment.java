package com.pointim.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pointim.R;
import com.pointim.adapter.ChatHistoryAdapter;
import com.pointim.model.AddFriend;
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
    public static List<AddFriend> historyList;

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
        historyList = new ArrayList<AddFriend>();
        initView(view);
    }

    private void initView(View view) {
        chatList = (PullToRefreshListView) view.findViewById(R.id.chat_list);
        adapter = new ChatHistoryAdapter(getContext(), historyList);
        chatList.setAdapter(adapter);

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddFriend friend = (AddFriend) view.getTag();
                friend.setHasMessage(false);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("user", friend.getUsername());
                intent.putExtra("nickname", friend.getNickname());
                friend.setHasMessage(false);
                startActivity(intent);
                update.sendMessage(new Message());
            }
        });
    }

}
