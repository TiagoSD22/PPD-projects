package com.hey.common;

import java.io.Serializable;
import java.util.Date;

public class Client implements Serializable {
    private String name;
    private String avatarName;
    private Status status;
    private Date lastSeen;

    public Client(){};

    public Client(String name, String avatarName){
        this.name = name;
        this.avatarName = avatarName;
        this.status = Status.ONLINE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
