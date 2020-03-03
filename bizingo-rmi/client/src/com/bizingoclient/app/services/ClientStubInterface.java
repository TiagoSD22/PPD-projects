package com.bizingoclient.app.services;

import bizingo.commons.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientStubInterface extends Remote {
    String nickname = "";
    String avatarName = "";

    void startGame() throws RemoteException;

    void receiveOtherPlayerHandshake(Handshake handshake) throws RemoteException;

    void receiveGameConfig(GameConfig playerConfig) throws RemoteException;

    void updateTypingStatus(TypingStatus status) throws RemoteException;

    void movePiece(PlayerMovement mov) throws RemoteException;

    void onRestartSolicitationDenied() throws RemoteException;

    void onRestartSolicitation() throws RemoteException;

    void receiveTextMessage(String text) throws RemoteException;

    void otherPlayerDisconnected() throws RemoteException;

    String getNickname() throws RemoteException;

    String getAvatarName() throws RemoteException;
}
