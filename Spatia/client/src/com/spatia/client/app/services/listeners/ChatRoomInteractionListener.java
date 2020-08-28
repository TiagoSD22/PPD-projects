package com.spatia.client.app.services.listeners;

import com.spatia.client.app.services.SpaceHandler;
import com.spatia.common.ChatRoomInteraction;
import org.openspaces.events.EventDriven;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.Notify;
import org.openspaces.events.notify.NotifyType;

@EventDriven
@Notify
@NotifyType(write = true)
@TransactionalEvent
public class ChatRoomInteractionListener {

    public ChatRoomInteractionListener(){}

    @SpaceDataEvent
    public void eventListener(ChatRoomInteraction event){
        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();
        spaceHandlerInstance.onChatRoomInteraction(event);
    }
}