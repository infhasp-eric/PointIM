package com.pointim.view.activity;

import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pointim.R;
import com.pointim.controller.FriendsController;
import com.pointim.model.AddFriend;
import com.pointim.model.FriendResult;
import com.pointim.model.ResultParam;
import com.pointim.ui.ClearEditText;
import com.pointim.utils.StringUtils;

import org.jivesoftware.smack.roster.RosterEntry;
import org.w3c.dom.Text;

import java.util.Observable;
import java.util.Observer;

public class AddFriendActivity extends AppCompatActivity {
    private ImageView btQuery;
    private ClearEditText etUsername;
    private RosterEntry friend;
    private LinearLayout friendContent;
    private TextView fUsername, addFriend;
    private ClearEditText fRemarkname;

    public Handler upHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            friendContent.setVisibility(View.VISIBLE);
            fUsername.setText(friend.getName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        btQuery = (ImageView) findViewById(R.id.bt_query);
        etUsername = (ClearEditText) findViewById(R.id.et_username);
        friendContent = (LinearLayout) findViewById(R.id.friend_content);
        fUsername = (TextView) findViewById(R.id.f_username);
        fRemarkname = (ClearEditText) findViewById(R.id.f_remarkname);
        addFriend = (TextView) findViewById(R.id.add_friend);

        btQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUsername.getText().toString();
                if (!StringUtils.isBlank(user)) {
                    FriendsController.getFriednByUserName(user, new Observer() {
                        @Override
                        public void update(Observable observable, Object data) {
                            FriendResult result = (FriendResult) data;
                            if (result.getFriend() != null) {
                                friend = result.getFriend();
                                System.out.println("===============================================");
                                Log.e("Friend", friend.getName() + "/" + friend.getUser());
                                upHandler.sendMessage(new Message());//发送广播通知界面更新数据
                            } else {
                                System.out.println("2222222222222222222222222222222222222222222222");
                                toast(result.getMessage());
                            }
                        }
                    });
                } else {
                    toast("请先输入好友账号");
                }
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取备注名
                String remarkname = fRemarkname.getText().toString();
                Log.e("Friend", "备注名 " + remarkname);
                //新建模型，储存添加好友需要的东西
                AddFriend af = new AddFriend();
                af.setNickname(remarkname);//设置备注名
                af.setUsername(etUsername.getText().toString());//设置好友用户名
                af.setGroupname(null);//分组名称
                FriendsController.addFriend(af, new Observer() {
                    @Override
                    public void update(Observable observable, Object data) {
                        ResultParam rp = (ResultParam) data;
                        if(rp.isFlag()) {
                            toast("添加成功");
                        } else {
                            toast("添加失败");
                        }
                    }
                });
            }
        });
    }

    private void toast(String message) {
        Toast.makeText(AddFriendActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
