package com.spatia.client.app.services.listeners;

import com.spatia.client.app.services.SpaceHandler;
import com.spatia.common.ChatMessage;
import org.openspaces.events.EventDriven;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.NotifyType;
import org.openspaces.events.polling.Polling;

@EventDriven
@Polling
@NotifyType(write = true)
@TransactionalEvent
public class DirectMessageListener {
    public DirectMessageListener() {}

    @SpaceDataEvent
    public void eventListener(ChatMessage event){
        SpaceHandler.getInstance().onDirectMessageReceived(event);
    }
}
