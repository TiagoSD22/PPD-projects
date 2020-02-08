package com.bizingoclient.app.mainGame;


import com.bizingoclient.Main;
import com.bizingoclient.app.mainGame.game.GameController;
import com.bizingoclient.app.mainGame.chatToolbar.ChatToolbarController;
import com.bizingoclient.app.services.MessageHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;


public class MainGameController {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private GameController gameController;
    @FXML
    private ChatToolbarController chatToolbarController;
    private MessageHandler msgHandler;
    private Stage mainStage;

    public GameController getGameController() {
        return gameController;
    }

    public ChatToolbarController getChatToolbarController() {
        return chatToolbarController;
    }

    @FXML
    public void initialize() {

        Main.addOnChangeScreenListener(new Main.OnChangeSceen() {
            @Override
            public void onScreenChanged(String newScreen, Object data, Stage stage) {
                mainStage = stage;
                HashMap dataMap = ((HashMap<String, Object>) data);
                Socket socket = (Socket) dataMap.get("socket");
                String nickname = (String) dataMap.get("nickname");
                String avatar = (String) dataMap.get("avatar");
                //ObjectInputStream input = (ObjectInputStream) dataMap.get("input");
                //ObjectOutputStream output = (ObjectOutputStream) dataMap.get("output");

                initControllers(socket, nickname, avatar);
            }
        });
    }

    public void initControllers(Socket socket, String nickname, String avatar) {
        chatToolbarController.init(this);
        gameController.init(this);
        msgHandler = new MessageHandler(socket, this, chatToolbarController, gameController,
                nickname, avatar);
    }

    public MessageHandler getMessageHandler() {
        return msgHandler;
    }

}



