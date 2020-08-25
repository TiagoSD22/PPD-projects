package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;

@SpaceClass
public class ChatRoomMessage extends ChatMessage {
    private String roomName;

    public ChatRoomMessage(){super();}

    public ChatRoomMessage(String roomName, String senderName, String receiverName, String text) {
        super(senderName, receiverName, text);
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
