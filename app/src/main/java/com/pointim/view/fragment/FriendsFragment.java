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

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pointim.R;
import com.pointim.adapter.FriendAdapter;
import com.pointim.controller.FriendsController;
import com.pointim.controller.UserController;
import com.pointim.view.activity.AddFriendActivity;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Eric on 2016/5/14.
 */
public class FriendsFragment extends Fragment {
    private static List<RosterEntry> friendList = new ArrayList<RosterEntry>();
    private PullToRefreshListView listView;
    private FriendAdapter friendAdapter;

    public Handler upHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
        getAllFriends();
    }

    private void initView(View view) {
        listView = (PullToRefreshListView) view.findViewById(R.id.friends_list);
        friendAdapter = new FriendAdapter(getActivity().getApplicationContext(), friendList);
        listView.setAdapter(friendAdapter);

        view.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getAllFriends() {
        FriendsController.getAllFriends(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                List<RosterEntry> list = (List<RosterEntry>) data;
                for (RosterEntry re : list) {
                    friendList.add(re);
                    Log.e("Friend", re.getName());
                }
                upHandler.sendMessage(new Message());
            }
        });
    }
}
