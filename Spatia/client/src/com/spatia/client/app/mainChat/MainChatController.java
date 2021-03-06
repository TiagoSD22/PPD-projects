package com.spatia.client.app.mainChat;


import com.spatia.client.Main;
import com.spatia.client.app.mainChat.chat.ChatController;
import com.spatia.client.app.mainChat.toolbar.ToolbarController;
import com.spatia.client.app.services.AudioService;
import com.spatia.client.app.services.SpaceHandler;
import com.spatia.common.ChatMessage;
import com.spatia.common.ChatRoom;
import com.spatia.common.Client;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


public class MainChatController {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ToolbarController toolbarController;
    @FXML
    private ChatController chatController;
    private Stage mainStage;
    private Client currentClient;

    @FXML
    public void initialize() {

        Main.addOnChangeScreenListener(new Main.OnChangeSceen() {
            @Override
            public void onScreenChanged(String newScreen, Object data, Stage stage) {
                if (newScreen.equals("chat-screen")) {
                    mainStage = stage;
                    HashMap dataMap = ((HashMap<String, Object>) data);

                    currentClient = (Client) dataMap.get("client");

                    initControllers();

                }
                else if(newScreen.equalsIgnoreCase("stop")){
                    if(currentClient != null) {
                        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();
                        ChatRoom currentInRoom = toolbarController.getCurrentRoom();

                        String currentInRoomName = currentInRoom != null ? currentInRoom.getName() : null;
                        spaceHandlerInstance.writeCloseConnectionSolicitation(currentClient.getName(), currentInRoomName);
                    }
                }
            }
        });
    }

    public void initControllers() {
        chatController.init(this);
        toolbarController.init(this);
        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();
        spaceHandlerInstance.setMainChatController(this);
    }


    public ToolbarController getToolbarController() {
        return toolbarController;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public Client getCurrentClient(){
        return currentClient;
    }

    public void onDirectMessageReceived(ChatMessage msg){
        System.out.println("Nova mensagem de " + msg.getSenderName() + " recebida: " + msg.getText());
        chatController.onMessageReceived(msg);
    }

    public void onRoomChatMessageReceived(String sender, String text){
        System.out.println("Nova mensagem de " + sender + " recebida para a sala inteira: " + text);
        chatController.onRoomMessageReceived(sender, text);
    }
}



