package com.spatia.common;

public enum TypingStatus {

    TYPING("TYPING"),
    STOPED("STOPED");

    private String value;

    TypingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
