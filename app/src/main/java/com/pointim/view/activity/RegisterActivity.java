package com.pointim.view.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.pointim.R;
import com.pointim.controller.UserController;
import com.pointim.model.RegisterParam;
import com.pointim.model.ResultParam;
import com.pointim.ui.ClearEditText;
import com.pointim.utils.StringUtils;

import java.util.Observable;
import java.util.Observer;

public class RegisterActivity extends AppCompatActivity {
    private ClearEditText etUsername, etNickname, etPassword, etRepassword;
    private Button btRegister;
    private ImageView btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        initView();
    }

    private void initView() {
        etUsername = (ClearEditText) findViewById(R.id.et_username);
        etNickname = (ClearEditText) findViewById(R.id.et_nickname);
        etPassword = (ClearEditText) findViewById(R.id.et_password);
        etRepassword = (ClearEditText) findViewById(R.id.et_repassword);
        btBack = (ImageView) findViewById(R.id.bt_back);
        btRegister = (Button) findViewById(R.id.btn_register_ok);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterClick();
            }
        });
    }

    private void onRegisterClick() {
        String username = etUsername.getText().toString();
        String nickname = etNickname.getText().toString();
        String password = etPassword.getText().toString();
        String repassword = etRepassword.getText().toString();

        //判断用户名是否为空
        if(StringUtils.isBlank(username)) {
            toast("请输入用户名");
            return;
        }
        if(StringUtils.isBlank(nickname)) {
            toast("请输入昵称");
            return;
        }
        if(StringUtils.isBlank(password)) {
            toast("请输入密码");
            return;
        }
        if(StringUtils.isBlank(repassword)) {
            toast("请确认密码");
            return;
        }
        if(!password.equals(repassword)) {
            toast("两次密码不一致，请重新输入");
            etRepassword.setText("");
            return;
        }

        RegisterParam rParam = new RegisterParam();
        rParam.setUsername(username);
        rParam.setPassword(password);
        rParam.setNickname(nickname);
        Log.e("Regist", "111111111111111111111111111");
        UserController.userRegister(rParam, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                ResultParam param = (ResultParam) data;
                if (param.isFlag()) {
                    toast("注册成功");
                    finish();
                } else {
                    toast("注册失败");
                }
            }
        });

    }


    //弹出提示
    private void toast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
