package com.hey.common;

public class AvatarUpdate extends MessageContent {
    private String clientName;
    private String newAvatarName;

    public AvatarUpdate(String clientName, String newAvatarName) {
        this.clientName = clientName;
        this.newAvatarName = newAvatarName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getNewAvatarName() {
        return newAvatarName;
    }

    public void setNewAvatarName(String newAvatarName) {
        this.newAvatarName = newAvatarName;
    }
}
