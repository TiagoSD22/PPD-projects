package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.List;

@SpaceClass
public class ChatRoom implements Serializable, Comparable<ChatRoom>{
    private String name;
    private List<Client> connectedClientList;

    public ChatRoom() {
    }

    public ChatRoom(String name, List<Client> connectedClientList) {
        this.name = name;
        this.connectedClientList = connectedClientList;
    }

    @SpaceId(autoGenerate = true)
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

    @Override
    public int compareTo(ChatRoom c) {
        return this.name.compareTo(c.getName());
    }
}
