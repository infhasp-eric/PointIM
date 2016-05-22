package com.pointim.controller;

import android.util.Log;

import com.pointim.model.LoginParam;
import com.pointim.model.RegisterParam;
import com.pointim.model.ResultParam;
import com.pointim.smack.SmackManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

/**
 * 用户相关连接控制器
 * Created by Eric on 2016/5/14.
 */
public class UserController {

    /**
     * 用户登录
     * @param loginParam  登录数据
     * @param observer    回调类
     */
    public static void userLogin(final LoginParam loginParam, final Observer observer) {
        //开启一个子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResultParam result = new ResultParam();
                try {
                    boolean flag = SmackManager.getInstance().login(loginParam.getUsername(), loginParam.getPassword());
                    result.setFlag(flag);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(e.getMessage().equals("Client is already logged in")) {
                        result.setFlag(true);
                    } else {
                        result.setFlag(false);
                        result.setMessage(e.getMessage());
                    }
                } finally {
                    observer.update(null, result);
                }
            }
        }).start();
    }

    /**
     * 注销账号
     */
    public static void userLogout(final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = SmackManager.getInstance().logout();
                SmackManager.getInstance().disconnect();
                if(observer != null)
                    observer.update(null, flag);
            }
        }).start();
    }

    /**
     * 用户注册
     * @param param
     * @param observer
     */
    public static void userRegister(final RegisterParam param, final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("Regist", "2222222222222222222222222222222");
                Map<String, String> attributes = new HashMap<>();
                attributes.put("Name", param.getNickname());
                Log.e("Regist", "username:" + param.getUsername() + "|password:" + param.getPassword());
                final boolean flag = SmackManager.getInstance().registerUser(param.getUsername(), param.getPassword(), attributes);
                Log.e("Regist", "2222222222222222222222222222222" + flag);
                ResultParam param = new ResultParam();
                param.setFlag(flag);
                observer.update(null, param);
            }
        }).start();
    }
}
