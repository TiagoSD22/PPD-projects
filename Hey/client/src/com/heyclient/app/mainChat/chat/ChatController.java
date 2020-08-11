package com.heyclient.app.mainChat.chat;


import com.heyclient.app.mainChat.MainChatController;
import javafx.fxml.FXML;
import javafx.scene.layout.*;


public class ChatController {
    @FXML
    public AnchorPane root;

    private MainChatController main;

    public void init(MainChatController mainChatController) {
        main = mainChatController;
        root.setBackground(Background.EMPTY);
    }
}