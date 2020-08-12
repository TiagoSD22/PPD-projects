package com.heyclient.app.mainChat.toolbar;


import com.hey.common.Client;
import com.heyclient.app.mainChat.MainChatController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.HashMap;
import java.util.List;


public class ToolbarController {
    @FXML
    public AnchorPane root;
    @FXML
    private ImageView currentUserAvatar;
    @FXML
    private Text currentUserName;
    @FXML
    private StackPane currentUserInfoRegion;
    @FXML
    private ListView<GridPane> contactListView;

    private MainChatController mainChatController;
    private BidiMap<String, ContactInfoBox> clientContactInfoBoxMap;
    private ContactInfoBox currentSelectedContactInfoBox;
    private HashMap<String, Integer> clientUnreadMsgRegisterMap;

    public void init(MainChatController mainChatController) {
        this.mainChatController = mainChatController;

        Circle clip = new Circle(36);
        clip.setStroke(Color.valueOf("#087e8b"));
        clip.setStrokeWidth(3);
        clip.setFill(Color.valueOf("#2f2f2f"));
        clip.setTranslateX(-110);
        clip.setTranslateY(5);
        currentUserInfoRegion.getChildren().add(clip);
        clip.toBack();

        Client currentClient = mainChatController.getCurrentClient();

        Image avatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" +
                currentClient.getAvatarName()));

        setCurrentUserAvatar(avatar);
        setCurrentUserName(currentClient.getName());

        clientContactInfoBoxMap = new DualHashBidiMap<>();
        clientUnreadMsgRegisterMap = new HashMap<>();
    }

    private void setCurrentUserName(String name) {
        currentUserName.setText(name);
    }

    private void setCurrentUserAvatar(Image avatar) {
        currentUserAvatar.setImage(avatar);
    }

    public void displayContactList(List<Client> contactList){
        for(Client c: contactList){
            displayContact(c);
        }
    }

    public void displayContact(Client c){
        Image avatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" +
                c.getAvatarName())
        );
        ContactInfoBox contactInfoBox = new ContactInfoBox(avatar, c.getName(), c.getStatus());

        clientContactInfoBoxMap.put(c.getName(), contactInfoBox);
        contactInfoBox.setCursor(Cursor.HAND);
        contactInfoBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(currentSelectedContactInfoBox != null){
                    currentSelectedContactInfoBox.setFocus(false);
                }
                currentSelectedContactInfoBox = contactInfoBox;
                currentSelectedContactInfoBox.setFocus(true);

                if(clientUnreadMsgRegisterMap.containsKey(c.getName())){
                    clientUnreadMsgRegisterMap.put(c.getName(), 0);
                    contactInfoBox.registerUnreadMsg(0);
                }

                Platform.runLater(() -> {
                    mainChatController.getChatController().setCurrentCollocutor(c);
                });
            }
        });
        Platform.runLater(() -> {
            contactListView.getItems().add(contactInfoBox);
        });
    }

    public void registerUnreadMsg(Client c){
        if(clientUnreadMsgRegisterMap.containsKey(c.getName())){
            int unreadMsgQtd = clientUnreadMsgRegisterMap.get(c.getName());
            unreadMsgQtd++;
            clientUnreadMsgRegisterMap.put(c.getName(), unreadMsgQtd);
        }
        else{
            clientUnreadMsgRegisterMap.put(c.getName(), 1);
        }

        ContactInfoBox contactInfoBox = clientContactInfoBoxMap.get(c.getName());
        contactInfoBox.registerUnreadMsg(clientUnreadMsgRegisterMap.get(c.getName()));
    }
}