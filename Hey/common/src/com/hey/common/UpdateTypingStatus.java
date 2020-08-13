package com.hey.common;

public class UpdateTypingStatus extends MessageContent {
    private TypingStatus typingStatus;
    private String clientSenderName;
    private String clientReceiverName;

    public UpdateTypingStatus(TypingStatus typingStatus, String clientSenderName, String clientReceiverName) {
        this.typingStatus = typingStatus;
        this.clientSenderName = clientSenderName;
        this.clientReceiverName = clientReceiverName;
    }

    public TypingStatus getTypingStatus() {
        return typingStatus;
    }

    public void setTypingStatus(TypingStatus typingStatus) {
        this.typingStatus = typingStatus;
    }

    public String getClientSenderName() {
        return clientSenderName;
    }

    public void setClientSenderName(String clientSenderName) {
        this.clientSenderName = clientSenderName;
    }

    public String getClientReceiverName() {
        return clientReceiverName;
    }

    public void setClientReceiverName(String clientReceiverName) {
        this.clientReceiverName = clientReceiverName;
    }
}
