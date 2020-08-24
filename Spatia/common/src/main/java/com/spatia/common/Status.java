package com.spatia.common;

import java.io.Serializable;

public enum Status implements Serializable {
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
