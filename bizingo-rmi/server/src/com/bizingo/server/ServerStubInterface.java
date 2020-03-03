package com.bizingo.server;

import bizingo.commons.Handshake;
import bizingo.commons.Message;
import bizingo.commons.PlayerMovement;
import bizingo.commons.TypingStatus;
import com.bizingoclient.app.services.ClientStubInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerStubInterface extends Remote {
    void registerClient(ClientStubInterface client) throws RemoteException;

    void onClientReady() throws RemoteException;

    void onClientQuit(ClientStubInterface client) throws RemoteException;

    void closeClientConnection(ClientStubInterface client) throws RemoteException;

    void receiveClientTextMessage(ClientStubInterface client, String text) throws RemoteException;

    void onRestartSolicitation(ClientStubInterface client) throws RemoteException;

    void onRestartSolicitationDenied(ClientStubInterface client) throws RemoteException;

    void receiveClientHandshake(ClientStubInterface client, Handshake handshake) throws RemoteException;

    void onClientMovePiece(ClientStubInterface client, PlayerMovement mov) throws RemoteException;

    void updateClientTypingStatus(ClientStubInterface client, TypingStatus status) throws RemoteException;
}
