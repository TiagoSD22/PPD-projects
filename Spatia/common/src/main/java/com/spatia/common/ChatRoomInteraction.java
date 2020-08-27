package com.spatia.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class ChatRoomInteraction {
    private InteractionType type;
    private String roomName;
    private Client client;

    public ChatRoomInteraction() {
    }

    public ChatRoomInteraction(InteractionType type, String roomName, Client client) {
        this.type = type;
        this.roomName = roomName;
        this.client = client;
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
}
