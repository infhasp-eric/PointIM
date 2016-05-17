package com.pointim.model;

import org.jivesoftware.smack.roster.RosterEntry;

/**
 * Created by Eric
 * on 2016/5/16
 * for project PointIM
 */
public class FriendResult {
    private String message;

    public RosterEntry getFriend() {
        return friend;
    }

    public void setFriend(RosterEntry friend) {
        this.friend = friend;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private RosterEntry friend;
}
