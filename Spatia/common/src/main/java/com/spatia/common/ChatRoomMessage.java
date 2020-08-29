package com.spatia.common;

import com.gigaspaces.annotation.pojo.FifoSupport;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass(fifoSupport = FifoSupport.OPERATION)
public class ChatRoomMessage {
    private String roomName;
    private String senderName;
    private String text;
    private String forwardTo;

    public ChatRoomMessage(){}

    public ChatRoomMessage(String roomName, String senderName, String forwardTo, String text) {
        this.roomName = roomName;
        this.senderName = senderName;
        this.forwardTo = forwardTo;
        this.text = text;
    }

    @SpaceId(autoGenerate = false)
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getForwardTo() {
        return forwardTo;
    }

    public void setForwardTo(String forwardTo) {
        this.forwardTo = forwardTo;
    }
}
