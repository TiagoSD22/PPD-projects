package com.hey.common;

public class ConnectionAcceptance extends MessageContent {
    private boolean accepted;
    private String msg;

    public ConnectionAcceptance(boolean accepted, String msg) {
        this.accepted = accepted;
        this.msg = msg;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
