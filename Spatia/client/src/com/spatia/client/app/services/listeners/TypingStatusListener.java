package com.spatia.client.app.services.listeners;

import com.spatia.client.app.services.SpaceHandler;
import com.spatia.common.UpdateTypingStatus;
import org.openspaces.events.EventDriven;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.NotifyType;
import org.openspaces.events.polling.Polling;

@EventDriven
@Polling
@NotifyType(write = true)
@TransactionalEvent
public class TypingStatusListener {
    public TypingStatusListener() {
    }

    @SpaceDataEvent
    public void eventListener(UpdateTypingStatus event){
        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();
        spaceHandlerInstance.onUpdateTypingStatusReceived(event);
    }
}
