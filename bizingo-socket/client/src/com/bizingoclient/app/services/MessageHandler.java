package com.bizingoclient.app.services;

import bizingo.commons.*;
import com.bizingoclient.app.enums.ConnectionConfig;
import com.bizingoclient.app.mainGame.MainGameController;
import com.bizingoclient.app.menu.MenuController;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static bizingo.commons.MessageType.HANDSHAKE;

public class MessageHandler {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private MainGameController mainController;
    private MenuController menuController;

    private String nickname;
    private Image avatar;
    private String avatarName;

    private String otherClientNickname;
    private Image otherClientAvatar;

    private boolean run = true;

    public MessageHandler(MenuController menuController, Socket socket, String nickname, String avatar){
        this.socket = socket;
        this.menuController = menuController;
        try {
            output = new ObjectOutputStream(this.socket.getOutputStream());
            input = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        startListen();
        this.nickname = nickname;
        this.avatarName = avatar;
        this.avatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" + avatar));
    }

    private void sendHandshake(String nickname, String avatar) {
        Handshake handshake = new Handshake(nickname, avatar, socket.getInetAddress().getHostAddress(),
                ConnectionConfig.HOST.getValue());
        Message msg = new Message(HANDSHAKE, handshake);
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListen() {
        new Thread(() -> {
            try {
                while (run) {
                    Message msg = (Message) input.readObject();
                    if (msg != null) {
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
                            case QUIT:
                                System.out.println("Mensagem de desistencia do outro jogador recebida. " +
                                        "Encerrando partida");
                                Platform.runLater(() -> mainController.getGameController().showGiveUpDialog());
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("impossivel ler a mensagem do servidor");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String text) {
        System.out.println("Mensagem digitada: " + text);
        TextMessage txtMsg = new TextMessage(text, socket.getInetAddress().getHostAddress(),
                ConnectionConfig.HOST.getValue());
        Message msg = new Message(MessageType.TEXT, txtMsg);
        try {
            output.writeObject(msg);
            mainController.getChatToolbarController().displayOwnMessage(avatar, text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerMovement(String source, String dest){
        PlayerMovement mov = new PlayerMovement(source, dest, socket.getInetAddress().getHostAddress(),
                ConnectionConfig.HOST.getValue());

        Message msg = new Message(MessageType.MOVEMENT, mov);
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCloseMessage(){
        TextMessage txtMSg = new TextMessage("close", socket.getInetAddress().getHostAddress(),
                ConnectionConfig.HOST.getValue());

        Message msg = new Message(MessageType.CLOSE, txtMSg);
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendQuitMessage(){
        String source = socket.getInetAddress().getHostAddress();
        MessageContent content = new TextMessage("quit", source, ConnectionConfig.HOST.getValue());
        Message quitMsg = new Message(MessageType.QUIT, content);
        try {
            output.writeObject(quitMsg);
            run = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRestartMessage(){
        TextMessage txt = new TextMessage("restart", socket.getInetAddress().getHostAddress(),
                ConnectionConfig.HOST.getValue());
        Message rtMsg = new Message(MessageType.RESTART, txt);
        try{
            output.writeObject(rtMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStartMessage(){
        TextMessage txt = new TextMessage("start", socket.getInetAddress().getHostAddress(),
                ConnectionConfig.HOST.getValue());
        Message rtMsg = new Message(MessageType.START, txt);
        try{
            output.writeObject(rtMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTypingStatusMessage(TypingStatus status){
        TypingStatusMessage statusMessage = new TypingStatusMessage(status, socket.getInetAddress().getHostAddress(),
                ConnectionConfig.HOST.getValue());
        Message msg = new Message(MessageType.TYPING_STATUS, statusMessage);
        try{
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket(){
        run = false;
        try {
            output.close();
            input.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainController(MainGameController mainController){
        this.mainController = mainController;
        sendStartMessage();
    }

}
