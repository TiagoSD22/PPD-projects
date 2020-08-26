package com.spatia.server;

import com.spatia.common.ChatRoom;
import org.openspaces.events.EventDriven;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.Notify;
import org.openspaces.events.notify.NotifyType;

@EventDriven
@Notify
@NotifyType(write = true)
@TransactionalEvent
public class ChatRoomCreationListener {
    private Server serverInstance;

    public ChatRoomCreationListener(Server serverInstance) {
        this.serverInstance = serverInstance;
    }

    @SpaceDataEvent
    public void eventListener(ChatRoom event){
        serverInstance.onChatRoomCreated(event);
    }
}
