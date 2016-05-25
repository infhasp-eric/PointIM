package com.pointim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pointim.R;
import com.pointim.model.AddFriend;
import com.pointim.utils.StringUtils;

import java.util.List;

/**
 * Created by Eric
 * on 2016/5/25
 * for project PointIM
 */
public class SearchAdapter extends BaseAdapter {
    private List<AddFriend> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    public SearchAdapter(Context context, List<AddFriend> mList) {
        this.mList = mList;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AddFriend viewTag;
        TextView nickName;
        TextView username;
        TextView hasMessage;
        TextView status;

        convertView = mInflater.inflate(R.layout.item_friend, null);
        // construct an item tag
        viewTag = mList.get(position);
        convertView.setTag(viewTag);
        try {
            nickName = (TextView) convertView.findViewById(R.id.nickname);
            if (!StringUtils.isBlank(viewTag.getNickname()))
                nickName.setText(viewTag.getNickname());
            else
                nickName.setText(viewTag.getUsername());
            username = (TextView) convertView.findViewById(R.id.username);
            username.setText(viewTag.getUsername());
            status = (TextView) convertView.findViewById(R.id.status);
            status.setText("(" + viewTag.getUsername() + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}