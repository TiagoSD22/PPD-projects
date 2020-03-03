package com.bizingoclient.app.services;

import bizingo.commons.*;
import com.bizingo.server.ServerStubInterface;
import com.bizingoclient.app.mainGame.MainGameController;
import com.bizingoclient.app.menu.MenuController;
import javafx.application.Platform;
import javafx.scene.image.Image;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientStub extends UnicastRemoteObject implements ClientStubInterface {
    private ServerStubInterface server;
    private String nickname;
    private String avatarName;

    private MainGameController mainController;
    private MenuController menuController;
    private String otherClientNickname;
    private Image otherClientAvatar;
    private Image avatar;

    public ClientStub(ServerStubInterface server, String nickname, String avatar) throws RemoteException {
        super();
        this.server = server;
        this.nickname = nickname;
        this.avatarName = avatar;
        this.avatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" + avatar));
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public void setGameController(MainGameController controller){
        mainController = controller;
        sendStartMessage();
    }

    public void setMenuController(MenuController controller){
        menuController = controller;
    }

    @Override
    public void startGame(){
        System.out.println("Iniciando partida");
        Platform.runLater(() -> this.menuController.gameReadyToStart());
    }

    @Override
    public void receiveOtherPlayerHandshake(Handshake handshake){
        System.out.println("Handshake recebido");
        System.out.println("Nick do outro jogador: " + handshake.getNickname() +
                "\nAvatar do outro jogador: " + handshake.getAvatar());
        otherClientAvatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" +
                handshake.getAvatar()));
        otherClientNickname = handshake.getNickname();
        mainController.getChatToolbarController().setOtherPlayerAvatar(otherClientAvatar);
        mainController.getChatToolbarController().setOtherPlayerNickname(otherClientNickname);
    }

    @Override
    public void receiveGameConfig(GameConfig playerConfig){
        System.out.println("Mensagem de configuracao de partida recebida");
        mainController.getGameController().setPlayerColor(playerConfig.getPlayerPieceColor());
        mainController.getGameController().setTurnToPlay(playerConfig.isFirstTurn());
        Platform.runLater(() -> sendHandshake(nickname, avatarName));
    }

    @Override
    public void updateTypingStatus(TypingStatus status){
        System.out.println("Mensagem de status de digitacao recebida, status: " + status.getValue());
        mainController.getChatToolbarController().showTypingStatus(status);
    }

    @Override
    public void movePiece(PlayerMovement mov){
        String source = mov.getCoordSource();
        String dest = mov.getCoordDest();
        System.out.println("Mensagem de movimento recebida, movendo celula em " + source +
                " para " + dest);
        mainController.getGameController().showOponentMove(source, dest);
    }

    @Override
    public void onRestartSolicitationDenied(){
        System.out.println("Mensagem de recuso de reinicio de partida ecebida");
        Platform.runLater(() -> mainController.getGameController().onRestartSolicitationDenied());
    }

    @Override
    public void onRestartSolicitation(){
        System.out.println("Mensagem de solicitacao de recomeco de partida recebida");
        Platform.runLater(() -> mainController.getGameController().otherPlayerWannaRestart());
    }

    @Override
    public void receiveTextMessage(String text){
        System.out.println("Mensagem recebida do servidor: " + text);
        mainController.getChatToolbarController().displayIncomingMessage(otherClientAvatar, text);
    }

    @Override
    public void otherPlayerDisconnected(){
        System.out.println("Jogador oponente desistiu. Encerrando partida");
        Platform.runLater(() -> mainController.getGameController().showGiveUpDialog());
    }

    private void sendHandshake(String nickname, String avatar) {
        Handshake handshake = new Handshake(nickname, avatar);
        try {
            server.receiveClientHandshake(this, handshake);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String text) {
        System.out.println("Mensagem digitada: " + text);
        try {
            server.receiveClientTextMessage(this, text);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mainController.getChatToolbarController().displayOwnMessage(avatar, text);
    }

    public void sendPlayerMovement(String source, String dest){
        PlayerMovement mov = new PlayerMovement(source, dest);
        try {
            server.onClientMovePiece(this, mov);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendCloseMessage(){
        try {
            server.closeClientConnection(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendQuitMessage(){
        try {
            server.onClientQuit(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendRestartMessage(){
        try {
            server.onRestartSolicitation(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendDenyRestartMessage(){
        try {
            server.onRestartSolicitationDenied(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendStartMessage(){
        try {
            server.onClientReady();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendTypingStatusMessage(TypingStatus status){
        try {
            server.updateClientTypingStatus(this, status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
