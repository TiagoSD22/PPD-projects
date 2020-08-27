package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class CloseConnectionSolicitation {
    private String userName;
    private String currentInRoomName;

    public CloseConnectionSolicitation() {
    }

    public CloseConnectionSolicitation(String userName, String currentInRoomName) {
        this.userName = userName;
        this.currentInRoomName = currentInRoomName;
    }

    @SpaceId(autoGenerate = false)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrentInRoomName() {
        return currentInRoomName;
    }

    public void setCurrentInRoomName(String currentInRoomName) {
        this.currentInRoomName = currentInRoomName;
    }
}
