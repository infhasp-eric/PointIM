package com.pointim.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pointim.LoginActivity;
import com.pointim.R;
import com.pointim.controller.UserController;
import com.pointim.view.activity.MainActivity;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Eric on 2016/5/14.
 */
public class CenterFragment extends Fragment {
    private TextView loginOut, about;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.center_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        loginOut = (TextView) view.findViewById(R.id.login_out);
        about = (TextView) view.findViewById(R.id.about);

        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserController.userLogout(new Observer() {
                    @Override
                    public void update(Observable observable, Object data) {
                        boolean falg = (boolean) data;
                        if(falg) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });
            }
        });
    }
}
