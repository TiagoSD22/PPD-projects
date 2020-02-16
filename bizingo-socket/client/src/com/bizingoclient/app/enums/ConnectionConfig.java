package com.bizingoclient.app.enums;

public enum ConnectionConfig {
    HOST("0.tcp.ngrok.io"),
    PORT("15319");

    private String value;

    ConnectionConfig(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
