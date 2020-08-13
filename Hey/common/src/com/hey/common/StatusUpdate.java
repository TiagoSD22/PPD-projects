package com.hey.common;

import java.util.Date;

public class StatusUpdate extends MessageContent {
    private String clientName;
    private Status newStatus;
    private Date lastSeen;

    public StatusUpdate(String clientName, Status newStatus, Date lastSeen) {
        this.clientName = clientName;
        this.newStatus = newStatus;
        this.lastSeen = lastSeen;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Status newStatus) {
        this.newStatus = newStatus;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }
}
