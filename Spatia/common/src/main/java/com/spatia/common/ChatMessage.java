package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceId;

public class ChatMessage {
    private String senderName;
    private String receiverName;
    private String text;

    public ChatMessage() {
    }

    public ChatMessage(String senderName, String receiverName, String text) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.text = text;
    }

    @SpaceId(autoGenerate = false)
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
