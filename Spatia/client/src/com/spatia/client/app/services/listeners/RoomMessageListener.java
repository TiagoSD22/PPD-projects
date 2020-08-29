package com.spatia.client.app.services.listeners;

import com.spatia.client.app.services.SpaceHandler;
import com.spatia.common.ChatRoomMessage;
import org.openspaces.events.EventDriven;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.Notify;
import org.openspaces.events.notify.NotifyType;

@EventDriven
@Notify
@NotifyType(write = true)
@TransactionalEvent
public class RoomMessageListener {

    public RoomMessageListener() {}

    @SpaceDataEvent
    public void eventListener(ChatRoomMessage event){
        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();
        spaceHandlerInstance.onRoomMessageReceived(event);
    }
}
