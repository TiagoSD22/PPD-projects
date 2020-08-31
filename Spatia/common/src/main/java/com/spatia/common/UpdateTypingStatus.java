package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class UpdateTypingStatus {
    private TypingStatus typingStatus;
    private String sender;
    private String receiver;

    public UpdateTypingStatus() {
    }

    public UpdateTypingStatus(TypingStatus typingStatus, String sender, String receiver) {
        this.typingStatus = typingStatus;
        this.sender = sender;
        this.receiver = receiver;
    }

    public TypingStatus getTypingStatus() {
        return typingStatus;
    }

    public void setTypingStatus(TypingStatus typingStatus) {
        this.typingStatus = typingStatus;
    }

    @SpaceId(autoGenerate = false)
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
