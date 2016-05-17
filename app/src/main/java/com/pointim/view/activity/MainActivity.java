package com.pointim.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pointim.R;
import com.pointim.view.fragment.CenterFragment;
import com.pointim.view.fragment.ChatFragment;
import com.pointim.view.fragment.FriendsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2016/1/27.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //获取FragmentManager实例
        fgManager = getSupportFragmentManager();
        init();
        LoginIn();

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
     * 后台登录
     */
    private void LoginIn() {
        /*Intent intent = new Intent(LoginActivity.this, FragmentAct.class);
        startActivity(intent);*/
        /*LoginParam loginParam = OverallApplication.getLoginParam();
        if(loginParam != null && !StringUtils.isBlank(loginParam.getUsername()) && !StringUtils.isBlank(loginParam.getPassword())) {
            Log.e("Login", "开始登陆");
            UserContorller.loginToMain(loginParam, new Observer() {
                @Override
                public void update(Observable observable, Object o) {
                    //这里写数据返回之后的逻辑
                    UserInfo r = (UserInfo) o;
                    if (o != null && r != null && r.isStatus()) {
                        OverallApplication.userinfo = r.getData();
                        if(PersonalCenter.isExample) {
                            PersonalCenter.updateHandler.sendMessage(new Message());
                        }
                        savePush();
                    }
                }
            });
        }*/
    }

}
