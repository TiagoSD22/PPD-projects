package com.heyclient.app.mainChat;


import com.hey.common.Client;
import com.heyclient.Main;
import com.heyclient.app.mainChat.chatToolbar.ChatToolbarController;
import com.heyclient.app.mainChat.chat.ChatController;
import com.heyclient.app.services.MessageHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;


public class MainChatController {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ChatController chatController;
    @FXML
    private ChatToolbarController chatToolbarController;
    private Stage mainStage;
    private MessageHandler msgh;
    private Client currentClient;

    @FXML
    public void initialize() {

        Main.addOnChangeScreenListener(new Main.OnChangeSceen() {
            @Override
            public void onScreenChanged(String newScreen, Object data, Stage stage) {
                if (newScreen.equals("chat-screen")) {
                    mainStage = stage;
                    HashMap dataMap = ((HashMap<String, Object>) data);

                    /*Image image = new Image(getClass().getResourceAsStream("/assets/Images/game_bg.jpg"));
                    BackgroundSize backgroundSize = new BackgroundSize(1366, 768, false,
                            false, false, false);
                    BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
                    Background background = new Background(backgroundImage);
                    mainPane.setBackground(background);*/

                    msgh = (MessageHandler) dataMap.get("msgHandler");
                    currentClient = (Client) dataMap.get("client");

                    initControllers();
                }
                else if(newScreen.equalsIgnoreCase("stop")){

                }
            }
        });
    }

    public void initControllers() {
        chatToolbarController.init(this);
        chatController.init(this);
        msgh.setMainChatController(this);
    }

    public MessageHandler getMessageHandler(){
        return msgh;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public ChatToolbarController getChatToolbarController() {
        return chatToolbarController;
    }

    public Client getCurrentClient(){
        return currentClient;
    }
}



