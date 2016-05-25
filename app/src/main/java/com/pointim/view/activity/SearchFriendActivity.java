package com.pointim.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.pointim.R;
import com.pointim.adapter.FriendAdapter;
import com.pointim.adapter.SearchAdapter;
import com.pointim.controller.FriendsController;
import com.pointim.model.AddFriend;
import com.pointim.ui.ClearEditText;
import com.pointim.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SearchFriendActivity extends AppCompatActivity {
    private ImageView btQuery;
    private ClearEditText etUsername;
    private ClearEditText fRemarkname;
    private ListView userList;

    private List<AddFriend> friendList = new ArrayList<AddFriend>();
    private SearchAdapter adapter;

    public Handler upHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        btQuery = (ImageView) findViewById(R.id.bt_query);
        etUsername = (ClearEditText) findViewById(R.id.et_username);
        userList = (ListView) findViewById(R.id.user_list);
        adapter = new SearchAdapter(SearchFriendActivity.this, friendList);
        userList.setAdapter(adapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddFriend af = (AddFriend) view.getTag();
                Intent intent = new Intent(SearchFriendActivity.this, AddFriendActivity.class);
                intent.putExtra("af", af);
                startActivity(intent);
            }
        });

        btQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUsername.getText().toString();
                friendList.clear();
                if (!StringUtils.isBlank(user)) {
                    FriendsController.getFriednByUserName(user, new Observer() {
                        @Override
                        public void update(Observable observable, Object data) {
                            if (data != null && (List<AddFriend>)data != null) {
                                List<AddFriend> result = (List<AddFriend>) data;
                                for (AddFriend af : result) {
                                    if(af.getUsername().equals(MainActivity.mineUsername)) {
                                        continue;
                                    }
                                    friendList.add(af);
                                }
                                upHandler.sendMessage(new Message());
                            } else {
                                toast("没有查询到相关用户");
                            }
                        }
                    });
                } else {
                    toast("请先输入好友账号");
                }
            }
        });

    }

    private void toast(String message) {
        Toast.makeText(SearchFriendActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
