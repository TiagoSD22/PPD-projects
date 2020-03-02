package com.bizingoclient.app.services;

import bizingo.commons.*;
import com.bizingo.server.ServerStubInterface;
import com.bizingoclient.app.mainGame.MainGameController;
import com.bizingoclient.app.menu.MenuController;
import javafx.application.Platform;
import javafx.scene.image.Image;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static bizingo.commons.MessageType.HANDSHAKE;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

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
    public void receiveMessage(Message msg) throws RemoteException {
        switch (msg.getType()){
            case START:
                System.out.println("Mensagem de jogo pronto para iniciar recebida, iniciando partida");
                Platform.runLater(() -> this.menuController.gameReadyToStart());
                break;
            case HANDSHAKE:
                Handshake handshake = (Handshake) msg.getContent();
                System.out.println("Handshake recebido");
                System.out.println("Nick do outro jogador: " + handshake.getNickname() +
                        "\nAvatar do outro jogador: " + handshake.getAvatar());
                otherClientAvatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" +
                        handshake.getAvatar()));
                otherClientNickname = handshake.getNickname();
                mainController.getChatToolbarController().setOtherPlayerAvatar(otherClientAvatar);
                mainController.getChatToolbarController().setOtherPlayerNickname(otherClientNickname);
                break;
            case CONFIG:
                GameConfig playerConfig = (GameConfig) msg.getContent();
                System.out.println("Mensagem de configuracao de partida recebida");
                mainController.getGameController().setPlayerColor(playerConfig.getPlayerPieceColor());
                mainController.getGameController().setTurnToPlay(playerConfig.isFirstTurn());
                Platform.runLater(() -> sendHandshake(nickname, avatarName));
                break;
            case TEXT:
                TextMessage txtMsg = (TextMessage) msg.getContent();
                System.out.println("Mensagem recebida do servidor: " + txtMsg.getText());
                mainController.getChatToolbarController().displayIncomingMessage(otherClientAvatar, txtMsg.getText());
                break;
            case TYPING_STATUS:
                TypingStatusMessage statusMsg = (TypingStatusMessage) msg.getContent();
                System.out.println("Mensagem de status de digitacao recebida, status: " +
                        statusMsg.getStatus().getValue());
                mainController.getChatToolbarController().showTypingStatus(statusMsg.getStatus());
                break;
            case MOVEMENT:
                PlayerMovement mov = (PlayerMovement) msg.getContent();
                String source = mov.getCoordSource();
                String dest = mov.getCoordDest();
                System.out.println("Mensagem de movimento recebida, movendo celula em " + source +
                        " para " + dest);
                mainController.getGameController().showOponentMove(source, dest);
                break;
            case RESTART:
                System.out.println("Mensagem de solicitacao de recomeco de partida recebida");
                Platform.runLater(() -> mainController.getGameController().otherPlayerWannaRestart());
                break;
            case DENY_RESTART:
                System.out.println("Mensagem de recuso de reinicio de partida ecebida");
                Platform.runLater(() -> mainController.getGameController().onRestartSolicitationDenied());
                break;
            case QUIT:
                System.out.println("Mensagem de desistencia do outro jogador recebida. " +
                        "Encerrando partida");
                Platform.runLater(() -> mainController.getGameController().showGiveUpDialog());
                break;
            default:
                break;
        }
    }

    private void sendMessage(Message msg){
        try {
            this.server.handleClientMessage(this, msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendHandshake(String nickname, String avatar) {
        Handshake handshake = new Handshake(nickname, avatar);
        Message msg = new Message(HANDSHAKE, handshake);
        sendMessage(msg);
    }

    public void sendTextMessage(String text) {
        System.out.println("Mensagem digitada: " + text);
        TextMessage txtMsg = new TextMessage(text);
        Message msg = new Message(MessageType.TEXT, txtMsg);
        sendMessage(msg);
        mainController.getChatToolbarController().displayOwnMessage(avatar, text);
    }

    public void sendPlayerMovement(String source, String dest){
        PlayerMovement mov = new PlayerMovement(source, dest);
        Message msg = new Message(MessageType.MOVEMENT, mov);
        sendMessage(msg);
    }

    public void sendCloseMessage(){
        TextMessage txtMSg = new TextMessage("close");
        Message msg = new Message(MessageType.CLOSE, txtMSg);
        sendMessage(msg);
    }

    public void sendQuitMessage(){
        MessageContent content = new TextMessage("quit");
        Message quitMsg = new Message(MessageType.QUIT, content);
        sendMessage(quitMsg);
    }

    public void sendRestartMessage(){
        TextMessage txt = new TextMessage("restart");
        Message rtMsg = new Message(MessageType.RESTART, txt);
        sendMessage(rtMsg);
    }

    public void sendDenyRestartMessage(){
        Message msg = new Message(MessageType.DENY_RESTART, null);
        sendMessage(msg);
    }

    public void sendStartMessage(){
        TextMessage txt = new TextMessage("start");
        Message rtMsg = new Message(MessageType.START, txt);
        sendMessage(rtMsg);
    }

    public void sendTypingStatusMessage(TypingStatus status){
        TypingStatusMessage statusMessage = new TypingStatusMessage(status);
        Message msg = new Message(MessageType.TYPING_STATUS, statusMessage);
        sendMessage(msg);
    }
}
