package com.pointim.controller;

import android.util.Log;

import com.pointim.model.AddFriend;
import com.pointim.model.FriendResult;
import com.pointim.model.ResultParam;
import com.pointim.smack.SmackManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Set;

/**
 * Created by Eric on 2016/5/15.
 */
public class FriendsController {

    public static void getAllFriends(final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<RosterEntry> friends = SmackManager.getInstance().getAllFriends();
                List<RosterEntry> list = new ArrayList<>();
                for(RosterEntry friend : friends) {
                    list.add(friend);
                }
                observer.update(null, list);
            }
        }).start();
    }

    public static void getFriednByUserName(final String user, final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*System.out.println("1111111111111111111111111111111111111111111111111");
                FriendResult result = new FriendResult();
                try {
                    Log.e("Friend", user);
                    RosterEntry friend = SmackManager.getInstance().getFriend(user);
                    Log.e("Friend", "friend is null" + (friend==null));
                    result.setFriend(friend);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    result.setMessage(e.getMessage());
                }
                observer.update(null, result);*/
                try {
                    SmackManager.getInstance().searchUser(user);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 添加好友
     * @param af
     * @param observer
     */
    public static void addFriend(final AddFriend af, final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResultParam resultParam = new ResultParam();
                resultParam.setFlag(SmackManager.getInstance().addFriend(af.getUsername(), af.getNickname(), af.getGroupname()));
                Log.e("Friend", "返回结果" + resultParam.isFlag());
                observer.update(null, resultParam);
            }
        }).start();
    }
}
