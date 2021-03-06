package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class ConnectionSolicitation {
    private String userName;
    private String avatarName;

    public ConnectionSolicitation(){}

    public ConnectionSolicitation(String userName, String avatarName) {
        this.userName = userName;
        this.avatarName = avatarName;
    }

    @SpaceId(autoGenerate = false)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }
}
