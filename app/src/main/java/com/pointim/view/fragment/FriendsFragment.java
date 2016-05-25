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
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pointim.R;
import com.pointim.adapter.FriendAdapter;
import com.pointim.controller.FriendsController;
import com.pointim.model.AddFriend;
import com.pointim.utils.ChatUtils;
import com.pointim.view.activity.ChatActivity;
import com.pointim.view.activity.MainActivity;
import com.pointim.view.activity.SearchFriendActivity;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Eric on 2016/5/14.
 */
public class FriendsFragment extends Fragment {
    public static boolean isEx = false;

    private ListView listView;
    private static FriendAdapter friendAdapter;

    public static Handler upHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("好友操作", "这时获取到全部好友" + new Date());
            if(isEx)
            friendAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.friends_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        isEx = true;
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.friends_list);
        friendAdapter = new FriendAdapter(getActivity().getApplicationContext(), MainActivity.friendList);
        listView.setAdapter(friendAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddFriend friend = (AddFriend) view.getTag();
                friend.setHasMessage(false);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("user", friend.getUsername());
                intent.putExtra("nickname", friend.getNickname());
                startActivity(intent);
                upHandler.sendMessage(new Message());
            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            private View delview;

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AddFriend af = (AddFriend) view.getTag();
                Log.e("Friend", "username" + af.getUsername());
                FriendsController.deleteFriend(af.getUsername(), new Observer() {
                    @Override
                    public void update(Observable observable, Object data) {
                        ChatUtils.getAllFriends(upHandler);
                    }
                });
                return true;
                /*if (lastPress < parent.getCount()) {
                    delview = parent.getChildAt(lastPress).findViewById(R.id.linear_del);
                    if (null != delview) {
                        delview.setVisibility(View.GONE);
                    }
                }

                delview = view.findViewById(R.id.linear_del);
                delview.setVisibility(View.VISIBLE);

                delview.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delview.setVisibility(View.GONE);
                        curList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                delview.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delview.setVisibility(View.GONE);
                    }
                });

                lastPress = position;
                delState = true;
                return true;*/
            }
        });

        view.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置好友是否有数据
     */
    /*public static void setFriendStatus(String username) {
        try {
            AddFriend af = friendMap.get(username);
            af.setHasMessage(true);
            upHandler.sendMessage(new Message());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        isEx = false;
    }
}
