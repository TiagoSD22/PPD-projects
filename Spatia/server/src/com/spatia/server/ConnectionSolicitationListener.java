package com.spatia.server;

import com.spatia.common.ConnectionSolicitation;
import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.NotifyType;
import org.openspaces.events.polling.Polling;

@EventDriven
@Polling
@NotifyType(write = true)
@TransactionalEvent
public class ConnectionSolicitationListener {
    private Server serverInstance;

    ConnectionSolicitationListener(Server serverInstance){
        this.serverInstance = serverInstance;
    }

    @EventTemplate
    ConnectionSolicitation unprocessedData(){
        return new ConnectionSolicitation();
    }

    @SpaceDataEvent
    public void eventListener(ConnectionSolicitation event){
        serverInstance.onConnectionSolicitationReceived(event);
    }
}
