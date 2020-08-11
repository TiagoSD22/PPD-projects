package com.hey.common;

public enum MessageType {
    CONNECTION_SOLICITATION("CONNECTION_SOLICITATION"),
    CONNECTION_ACCEPTANCE("CONNECTION_ACCEPTANCE"),
    CHAT("CHAT"),
    TYPING_STATUS("TYPING_STATUS"),
    CLOSE("CLOSE"),
    QUIT("QUIT");

    private String value;

    MessageType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }
}