package com.pointim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointim.R;
import com.pointim.model.AddFriend;
import com.pointim.utils.StringUtils;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.List;

/**
 * Created by Eric on 2016/5/15.
 */
public class FriendAdapter extends BaseAdapter {
    private List<AddFriend> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    public FriendAdapter(Context context, List<AddFriend> mList) {
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

        convertView = mInflater.inflate(R.layout.item_friend, null);
        // construct an item tag
        viewTag = mList.get(position);
        convertView.setTag(viewTag);

        nickName = (TextView) convertView.findViewById(R.id.nickname);
        if (StringUtils.isBlank(viewTag.getRemark()))
        nickName.setText(viewTag.getNickname());
        else
        nickName.setText(viewTag.getRemark());
        username = (TextView) convertView.findViewById(R.id.username);
        username.setText(viewTag.getUsername());
        return convertView;
    }
}
