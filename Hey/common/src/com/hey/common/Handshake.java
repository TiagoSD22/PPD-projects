package com.hey.common;

public class Handshake extends MessageContent{
    private String userName;
    private String avatarImageName;

    public Handshake(String userName, String avatarImageName){
        this.userName = userName;
        this.avatarImageName = avatarImageName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarImageName() {
        return avatarImageName;
    }

    public void setAvatarImageName(String avatarImageName) {
        this.avatarImageName = avatarImageName;
    }
}
