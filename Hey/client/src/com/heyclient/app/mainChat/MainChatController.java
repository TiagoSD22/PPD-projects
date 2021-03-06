package com.heyclient.app.mainChat;


import com.hey.common.Client;
import com.hey.common.Status;
import com.heyclient.Main;
import com.heyclient.app.mainChat.chat.ChatController;
import com.heyclient.app.mainChat.toolbar.ToolbarController;
import com.heyclient.app.services.AudioService;
import com.heyclient.app.services.MessageHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainChatController {

    @FXML
    private AnchorPane mainPane;
    @FXML
    private ToolbarController toolbarController;
    @FXML
    private ChatController chatController;
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

                    msgh = (MessageHandler) dataMap.get("msgHandler");
                    currentClient = (Client) dataMap.get("client");

                    initControllers();
                    getContactList();

                    msgh.connectToMessageBroker(currentClient.getName());
                }
                else if(newScreen.equalsIgnoreCase("stop")){

                }
            }
        });
    }

    public void initControllers() {
        chatController.init(this);
        toolbarController.init(this);
        msgh.setMainChatController(this);
    }

    public MessageHandler getMessageHandler(){
        return msgh;
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

    private void getContactList(){
        msgh.getContactList();
    }

    public void onContactListReceived(List<Client> contactList){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for(Client c: contactList){
            System.out.println("Contato recebido: " +
                    "\nNome: " + c.getName() +
                    "\nAvatar: " + c.getAvatarName() +
                    "\nStatus: " + c.getStatus() +
                    (c.getStatus().equals(Status.OFFLINE)? ("\nVisto por ultimo " + dateFormat.format(c.getLastSeen()))
                            : "")
            );
        }

        toolbarController.displayContactList(contactList);
    }

    public void onNewClientConnected(Client c){
        toolbarController.displayContact(c);
        AudioService.getInstance().playNewClientConnectedSound();
        chatController.showNewClientConnectedNotification(c);
    }

    public void onClientStatusUpdated(String clientName, Status newStatus, Date lastSeen){
        toolbarController.updateClientStatus(clientName, newStatus, lastSeen);
        chatController.updateClientStatus(clientName, newStatus, lastSeen);
    }

    public void onClientAvatarUpdated(String clientName, String newAvatar){
        toolbarController.updateClientAvatar(clientName, newAvatar);
        chatController.updateClientAvatar(clientName, newAvatar);
    }
}



