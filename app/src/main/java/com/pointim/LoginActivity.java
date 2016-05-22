package com.pointim;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pointim.controller.UserController;
import com.pointim.model.LoginParam;
import com.pointim.model.ResultParam;
import com.pointim.view.activity.MainActivity;
import com.pointim.view.activity.RegisterActivity;

import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private Button btLogin;

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
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });

        findViewById(R.id.bt_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * 登陆响应
     *
     */
    public void onLoginClick() {
        final String strusername = username.getText().toString();
        final String strpassword = password.getText().toString();
        //新建登录类型，保存登录所需要的数据
        LoginParam param = new LoginParam();
        param.setUsername(strusername);
        param.setPassword(strpassword);

        //通过自定义控制器向服务器请求
        UserController.userLogin(param, new Observer() {
            //无论登录成功与否最后回调的方法
            @Override
            public void update(Observable observable, Object data) {
                //处理登录操作后的结果，然后做出相应的处理
                ResultParam result = (ResultParam) data;//将返回数据强转为自定义类，在调用回调时必须是相同的类或者子类，否则会出现类型转换错误的报错
                Log.e("test", "返回结果" + result.isFlag());
                if(result.isFlag()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Message message = new Message();
                    message.obj = result.getMessage();
                    toastHandler.sendMessage(message);
                }
            }
        });

    }

}
