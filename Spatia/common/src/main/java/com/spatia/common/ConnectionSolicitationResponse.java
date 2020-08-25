package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class ConnectionSolicitationResponse {
    private String userName;
    private Boolean isAccepted;

    public ConnectionSolicitationResponse() {
    }

    public ConnectionSolicitationResponse(String userName, Boolean isAccepted) {
        this.userName = userName;
        this.isAccepted = isAccepted;
    }

    @SpaceId(autoGenerate = false)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}
