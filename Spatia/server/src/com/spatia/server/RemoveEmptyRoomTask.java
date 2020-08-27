package com.spatia.server;

import java.util.TimerTask;

public class RemoveEmptyRoomTask extends TimerTask {
    private Server server;
    private String roomName;

    public RemoveEmptyRoomTask(Server server, String roomName) {
        this.server = server;
        this.roomName = roomName;
    }

    @Override
    public void run() {
        server.removeRoom(roomName);
    }
}
