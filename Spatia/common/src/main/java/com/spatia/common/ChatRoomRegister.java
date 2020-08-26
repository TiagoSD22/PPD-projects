package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import java.util.SortedSet;

@SpaceClass
public class ChatRoomRegister{
    private String id;

    private SortedSet<ChatRoom> registeredRoomList;

    public ChatRoomRegister() {
    }

    public ChatRoomRegister(SortedSet<ChatRoom> registeredRoomList) {
        this.registeredRoomList = registeredRoomList;
    }

    public SortedSet<ChatRoom> getRegisteredRoomList() {
        return registeredRoomList;
    }

    public void setRegisteredRoomList(SortedSet<ChatRoom> registeredRoomList) {
        this.registeredRoomList = registeredRoomList;
    }

    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
