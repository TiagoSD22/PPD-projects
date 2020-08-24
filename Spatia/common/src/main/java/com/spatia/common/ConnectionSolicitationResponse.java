package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceId;

public class ConnectionSolicitationResponse {
    private String userName;
    private Boolean accepted;

    public ConnectionSolicitationResponse() {
    }

    public ConnectionSolicitationResponse(String userName, Boolean accepted) {
        this.userName = userName;
        this.accepted = accepted;
    }

    @SpaceId(autoGenerate = true)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
