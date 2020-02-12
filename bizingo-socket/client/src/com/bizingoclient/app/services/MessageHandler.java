package com.bizingoclient.app.services;

import bizingo.commons.*;
import com.bizingoclient.app.enums.ConnectionConfig;
import com.bizingoclient.app.mainGame.MainGameController;
import com.bizingoclient.app.mainGame.chatToolbar.ChatToolbarController;
import com.bizingoclient.app.mainGame.game.GameController;
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
    private ChatToolbarController chatController;
    private GameController gameController;
    private MainGameController mainController;

    private String nickname;
    private Image avatar;

    private String otherClientNickname;
    private Image otherClientAvatar;

    public MessageHandler(Socket socket, MainGameController mainController, ChatToolbarController chatController,
                          GameController gameController, String nickname, String avatar) {
        this.socket = socket;
        this.mainController = mainController;
        this.chatController = chatController;
        this.gameController = gameController;

        try {
            output = new ObjectOutputStream(this.socket.getOutputStream());
            input = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        startListen();
        this.nickname = nickname;
        this.avatar = new Image(getClass().getResourceAsStream("/assets/avatars/" + avatar));
        sendHandshake(nickname, avatar);
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
                while (true) {
                    Message msg = (Message) input.readObject();
                    if (msg != null) {
                        switch (msg.getType()){
                            case HANDSHAKE:
                                Handshake handshake = (Handshake) msg.getContent();
                                System.out.println("Handshake recebido");
                                System.out.println("Nick do outro jogador: " + handshake.getNickname() +
                                        "\nAvatar do outro jogador: " + handshake.getAvatar());
                                otherClientAvatar = new Image(getClass().getResourceAsStream("/assets/avatars/" +
                                        handshake.getAvatar()));
                                otherClientNickname = handshake.getNickname();
                                chatController.setOtherPlayerAvatar(otherClientAvatar);
                                chatController.setOtherPlayerNickname(otherClientNickname);
                                break;
                            case CONFIG:
                                GameConfig playerConfig = (GameConfig) msg.getContent();
                                System.out.println("Mensagem de configuracao de partida recebida");
                                mainController.getGameController().setPlayerColor(playerConfig.getPlayerPieceColor());
                                mainController.getGameController().setTurnToPlay(playerConfig.isFirstTurn());
                                break;
                            case TEXT:
                                TextMessage txtMsg = (TextMessage) msg.getContent();
                                System.out.println("Mensagem recebida do servidor: " + txtMsg.getText());
                                chatController.displayIncomingMessage(otherClientAvatar, txtMsg.getText());
                                break;
                            case MOVEMENT:
                                PlayerMovement mov = (PlayerMovement) msg.getContent();
                                String source = mov.getCoordSource();
                                String dest = mov.getCoordDest();
                                System.out.println("Mensagem de movimento recebida, movendo celula em " + source +
                                        " para " + dest);
                                mainController.getGameController().showOponentMove(source, dest);
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
            chatController.displayOwnMessage(avatar, text);
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

    public String getOtherClientNickname() {
        return otherClientNickname;
    }

    public Image getOtherClientAvatar() {
        return otherClientAvatar;
    }

    public void closeSocket(){
        try {
            output.close();
            input.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
