package com.spatia.server;

import com.spatia.common.ChatRoomInteraction;
import org.openspaces.events.EventDriven;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.NotifyType;
import org.openspaces.events.polling.Polling;

@EventDriven
@Polling
@NotifyType(write = true)
@TransactionalEvent
public class ChatRoomInteractionListener {
    private Server serverInstance;

    public ChatRoomInteractionListener(Server serverInstance){
        this.serverInstance = serverInstance;
    }

    @SpaceDataEvent
    public void eventListener(ChatRoomInteraction event){
        serverInstance.onChatRoomInteractionCreated(event);
    }
}
