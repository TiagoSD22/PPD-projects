package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.util.List;

@SpaceClass
public class ChatRoomRegister {
    private List<ChatRoom> registeredRoomList;

    public ChatRoomRegister() {
    }

    public ChatRoomRegister(List<ChatRoom> registeredRoomList) {
        this.registeredRoomList = registeredRoomList;
    }

    @SpaceId(autoGenerate = false)
    public List<ChatRoom> getRegisteredRoomList() {
        return registeredRoomList;
    }

    public void setRegisteredRoomList(List<ChatRoom> registeredRoomList) {
        this.registeredRoomList = registeredRoomList;
    }
}
