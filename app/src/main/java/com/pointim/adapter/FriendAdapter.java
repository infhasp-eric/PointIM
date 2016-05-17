package com.pointim.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pointim.R;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.List;

/**
 * Created by Eric on 2016/5/15.
 */
public class FriendAdapter extends BaseAdapter {
    private List<RosterEntry> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    public FriendAdapter(Context context, List<RosterEntry> mList) {
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
        RosterEntry viewTag;
        TextView nickName;

        convertView = mInflater.inflate(R.layout.item_friend, null);
        // construct an item tag
        viewTag = mList.get(position);
        convertView.setTag(viewTag);

        nickName = (TextView) convertView.findViewById(R.id.nickname);
        nickName.setText(viewTag.getName() + "(" + viewTag.getUser() + ")");
        return convertView;
    }
}
