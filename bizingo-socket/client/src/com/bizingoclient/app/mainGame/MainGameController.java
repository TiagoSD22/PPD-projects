package com.bizingoclient.app.mainGame;


import com.bizingoclient.Main;
import com.bizingoclient.app.mainGame.game.GameController;
import com.bizingoclient.app.mainGame.chatToolbar.ChatToolbarController;
import com.bizingoclient.app.services.MessageHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
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

    @FXML
    public void initialize() {

        Main.addOnChangeScreenListener(new Main.OnChangeSceen() {
            @Override
            public void onScreenChanged(String newScreen, Object data, Stage stage) {
                if (newScreen.equals("game")) {
                    mainStage = stage;
                    HashMap dataMap = ((HashMap<String, Object>) data);
                    Socket socket = (Socket) dataMap.get("socket");
                    String nickname = (String) dataMap.get("nickname");
                    String avatar = (String) dataMap.get("avatar");

                    Image image = new Image(getClass().getResourceAsStream("/assets/game_bg.jpg"));
                    BackgroundSize backgroundSize = new BackgroundSize(1366, 768, false,
                            false, false, false);
                    BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
                    Background background = new Background(backgroundImage);
                    mainPane.setBackground(background);

                    initControllers(socket, nickname, avatar);
                }
                else if(newScreen.equalsIgnoreCase("stop")){
                    msgHandler.closeSocket();
                }
            }
        });
    }

    public void initControllers(Socket socket, String nickname, String avatar) {
        chatToolbarController.init(this);
        gameController.init(this);
        msgHandler = new MessageHandler(socket, this, chatToolbarController, gameController, nickname, avatar);
    }

    public GameController getGameController() {
        return gameController;
    }

    public ChatToolbarController getChatToolbarController() {
        return chatToolbarController;
    }

    public MessageHandler getMessageHandler() {
        return msgHandler;
    }

}



