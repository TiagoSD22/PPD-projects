package com.bizingo.server;

import bizingo.commons.Message;
import com.bizingoclient.app.services.ClientStubInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerStubInterface extends Remote {
    void registerClient(ClientStubInterface client) throws RemoteException;
    void removeClient(ClientStubInterface client) throws RemoteException;
    void handleClientMessage(ClientStubInterface client, Message msg) throws RemoteException;
}
