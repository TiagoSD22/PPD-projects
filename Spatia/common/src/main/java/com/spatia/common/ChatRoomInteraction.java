package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class ChatRoomInteraction {
    private InteractionType type;
    private String roomName;
    private Client client;
    private Boolean notify;

    public ChatRoomInteraction() {
    }

    public ChatRoomInteraction(InteractionType type, String roomName, Client client, Boolean notify) {
        this.type = type;
        this.roomName = roomName;
        this.client = client;
        this.notify = notify;
    }

    public InteractionType getType() {
        return type;
    }

    public void setType(InteractionType type) {
        this.type = type;
    }

    @SpaceId(autoGenerate = false)
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }
}
