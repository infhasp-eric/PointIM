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
                Log.e("List", "Friends size is " + friends.size());
                List<RosterEntry> list = new ArrayList<>();
                if(friends != null) {
                    for (RosterEntry friend : friends) {
                        list.add(friend);
                    }
                }
                observer.update(null, list);
            }
        }).start();
    }

    public static void getFriednByUserName(final String user, final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<AddFriend> result = SmackManager.getInstance().searchUser(user);
                    observer.update(null, result);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    observer.update(null, null);
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                    observer.update(null, null);
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                    observer.update(null, null);
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
                resultParam.setFlag(SmackManager.getInstance().addFriend(af.getUsername(), af.getRemark(), af.getGroupname()));
                Log.e("Friend", "返回结果" + resultParam.isFlag());
                observer.update(null, resultParam);
            }
        }).start();
    }
}
