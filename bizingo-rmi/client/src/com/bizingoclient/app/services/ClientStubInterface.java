package com.bizingoclient.app.services;

import bizingo.commons.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientStubInterface extends Remote {
    String nickname = "";
    String avatarName = "";

    void receiveMessage(Message msg) throws RemoteException;

    String getNickname() throws RemoteException;

    String getAvatarName() throws RemoteException;
}
