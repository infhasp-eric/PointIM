package com.pointim.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pointim.R;
import com.pointim.controller.FriendsController;
import com.pointim.model.AddFriend;
import com.pointim.model.ResultParam;
import com.pointim.ui.ClearEditText;
import com.pointim.utils.StringUtils;
import com.pointim.view.fragment.FriendsFragment;

import java.util.Observable;
import java.util.Observer;

public class AddFriendActivity extends AppCompatActivity {
    private TextView username,nickname;
    private ClearEditText remark;
    private Button addFriend;
    private AddFriend af;

    private Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            String str = (String) message.obj;
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        af = (AddFriend) getIntent().getSerializableExtra("af");
        initView();
    }

    private void initView() {
        username = (TextView) findViewById(R.id.username);
        nickname = (TextView) findViewById(R.id.nickname);
        remark = (ClearEditText) findViewById(R.id.remark);
        username.setText(af.getUsername());
        nickname.setText(af.getNickname());

        findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取备注名
                String remarkname = remark.getText().toString();
                Log.e("Friend", "备注名 " + remarkname);
                //新建模型，储存添加好友需要的东西
                AddFriend af = new AddFriend();
                if (!StringUtils.isBlank(remarkname))
                af.setRemark(remarkname);//设置备注名
                else
                af.setRemark(nickname.getText().toString());
                af.setUsername(username.getText().toString());//设置好友用户名
                af.setGroupname(null);//分组名称
                FriendsController.addFriend(af, new Observer() {
                    @Override
                    public void update(Observable observable, Object data) {
                        ResultParam rp = (ResultParam) data;
                        Message msg = new Message();

                        if(rp.isFlag()) {
                            msg.obj = "添加成功";
                            FriendsFragment.getAllFriends();
                        } else {
                            msg.obj = "添加失败";
                        }
                        toastHandler.sendMessage(msg);
                    }
                });
            }
        });
    }
}
