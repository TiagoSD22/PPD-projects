package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceId;

import java.util.List;

public class ChatRoom {
    private String name;
    private List<Client> connectedClientList;

    public ChatRoom() {
    }

    public ChatRoom(String name, List<Client> connectedClientList) {
        this.name = name;
        this.connectedClientList = connectedClientList;
    }

    @SpaceId(autoGenerate = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Client> getConnectedClientList() {
        return connectedClientList;
    }

    public void setConnectedClientList(List<Client> connectedClientList) {
        this.connectedClientList = connectedClientList;
    }
}
