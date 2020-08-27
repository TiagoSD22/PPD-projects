package com.spatia.common;

import java.io.Serializable;

public enum InteractionType implements Serializable {
    ENTER("ENTER"),
    LEAVE("LEAVE");

    private String value;

    InteractionType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }
}
