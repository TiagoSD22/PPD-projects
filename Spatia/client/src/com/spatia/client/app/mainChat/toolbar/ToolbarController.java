package com.spatia.client.app.mainChat.toolbar;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.spatia.client.app.mainChat.MainChatController;
import com.spatia.client.app.services.AudioService;
import com.jfoenix.controls.JFXButton;
import com.spatia.common.Client;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.Date;
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
    @FXML
    private JFXButton soundBt;
    @FXML
    private JFXTabPane chatTabPane;
    @FXML
    private Tab chatTab;
    @FXML
    private Tab roomsTab;
    @FXML
    private ListView<GridPane> roomListView;
    @FXML
    private JFXButton createRoomBt;
    @FXML
    private JFXButton refreshRoomListBt;
    @FXML
    private GridPane createRoomCard;
    @FXML
    private JFXTextField createRoomNameTf;
    @FXML
    private JFXButton createRoomConfirmBt;
    @FXML
    private JFXButton createRoomCancelBt;

    private MainChatController mainChatController;
    private ContactInfoBox currentSelectedContactInfoBox;
    private BidiMap<String, ContactInfoBox> clientContactInfoBoxMap;
    private HashMap<String, Integer> clientUnreadMsgRegisterMap;
    //private HashMap<ContactInfoBox, Client> contactInfoBoxClientMap;
    private boolean sound = true;
    private ImageView soundOnImage;
    private ImageView soundOffImage;
    private TranslateTransition showCreateRoomCard;
    private TranslateTransition hideCreateRoomCard;

    public void init(MainChatController mainChatController) {
        this.mainChatController = mainChatController;

        soundOnImage = new ImageView(new Image(getClass().getResourceAsStream("/assets/Images/sound_on.png")));
        soundOffImage = new ImageView(new Image(getClass().getResourceAsStream("/assets/Images/sound_off.png")));
        soundOnImage.setFitHeight(18);
        soundOnImage.setFitWidth(18);
        soundOffImage.setFitHeight(18);
        soundOffImage.setFitWidth(18);
        soundBt.setGraphic(soundOnImage);
        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);
        soundBt.setEffect(ds);

        Client currentClient = mainChatController.getCurrentClient();

        Image avatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" +
                currentClient.getAvatarName()));

        setCurrentUserAvatar(avatar);
        setCurrentUserName(currentClient.getName());

        chatTab.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/assets/Images/chat_icon.png"))
        ));

        roomsTab.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/assets/Images/rooms_icon.png"))
        ));

        chatTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            if(newTab.equals(roomsTab)){
                onRoomTabClicked();
            }
            else{
                onChaTabClicked();
            }
        });

        clientContactInfoBoxMap = new DualHashBidiMap<>();
        clientUnreadMsgRegisterMap = new HashMap<>();

        loadShowCreateRoomCardAnimation();
        loadHideCreateRoomCardAnimation();
        //contactInfoBoxClientMap = new HashMap<>();
    }

    private void loadShowCreateRoomCardAnimation(){
        showCreateRoomCard =new TranslateTransition(new Duration(250), createRoomCard);
        showCreateRoomCard.setFromX(-400);
        showCreateRoomCard.setToX(0);
    }

    private void loadHideCreateRoomCardAnimation(){
        hideCreateRoomCard = new TranslateTransition(new Duration(250), createRoomCard);
        hideCreateRoomCard.setFromX(0);
        hideCreateRoomCard.setToX(-400);

        hideCreateRoomCard.setOnFinished(event -> {
            createRoomCard.setVisible(false);
            roomListView.setVisible(true);
            createRoomNameTf.clear();
        });
    }

    private void setCurrentUserName(String name) {
        currentUserName.setText(name);
    }

    private void setCurrentUserAvatar(Image avatar) {
        currentUserAvatar.setImage(avatar);
    }

    /*public void displayContactList(List<Client> contactList){
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
        contactInfoBoxClientMap.put(contactInfoBox, c);
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
                    mainChatController.getChatController()
                            .setCurrentCollocutor(contactInfoBoxClientMap.get(contactInfoBox));
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

    public void updateClientStatus(String clientName, Status newStatus, Date lastSeen){
        ContactInfoBox contactInfoBox = clientContactInfoBoxMap.get(clientName);
        Client c = contactInfoBoxClientMap.get(contactInfoBox);

        c.setStatus(newStatus);
        c.setLastSeen(lastSeen);

        contactInfoBox.setUserStatus(c.getStatus());
    }

    public void updateClientAvatar(String clientName, String newAvatar){
        ContactInfoBox contactInfoBox = clientContactInfoBoxMap.get(clientName);
        Client c = contactInfoBoxClientMap.get(contactInfoBox);

        c.setAvatarName(newAvatar);

        contactInfoBox.setAvatar(new Image(getClass().getResourceAsStream("/assets/Images/avatars/" + newAvatar)));
    }

    public void updateClientTypingStatus(String clientName, TypingStatus typingStatus){
        ContactInfoBox contactInfoBox = clientContactInfoBoxMap.get(clientName);
        contactInfoBox.setUserTypingStatus(typingStatus);
    }*/

    public void onSoundBtClicked(){
        sound = !sound;
        AudioService.getInstance().onMute();
        if(sound){
            soundBt.setGraphic(soundOnImage);
        }
        else{
            soundBt.setGraphic(soundOffImage);
        }
    }

    public void onCreateRoomBtClicked(){
        System.out.println("Solicitando criar nova sala");

        roomListView.setVisible(false);
        createRoomCard.setVisible(true);

        showCreateRoomCard.play();
    }

    private void onChaTabClicked(){
        System.out.println("Clicou na tab de chat");
    }

    private void onRoomTabClicked(){
        System.out.println("Clicou na tab de salas");
    }

    public void onRefreshRoomListBtClicked(){

    }

    public void onCreateRoomConfirmBtClicked(){
        hideCreateRoomCard.play();
    }

    public void onCreateRoomCancelBtClicked(){
        hideCreateRoomCard.play();
    }
}