package com.hey.common;

public enum Status {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE");

    private String value;

    Status(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }
}
