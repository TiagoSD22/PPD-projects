package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;

public class Client implements Serializable {
    private String name;
    private String avatarName;
    private Status status;

    public Client() {
    }

    public Client(String name, String avatarName, Status status) {
        this.name = name;
        this.avatarName = avatarName;
        this.status = status;
    }

    @SpaceId(autoGenerate = false)
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
