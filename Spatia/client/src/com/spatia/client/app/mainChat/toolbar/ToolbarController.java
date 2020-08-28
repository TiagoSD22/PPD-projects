package com.spatia.client.app.mainChat.toolbar;

import com.j_spaces.core.client.EntryAlreadyInSpaceException;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.spatia.client.app.mainChat.MainChatController;
import com.spatia.client.app.services.AudioService;
import com.jfoenix.controls.JFXButton;
import com.spatia.client.app.services.SpaceHandler;
import com.spatia.common.ChatRoom;
import com.spatia.common.Client;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.*;
import java.util.stream.Collectors;

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
    private ListView<ContactInfoBox> contactListView;
    @FXML
    private JFXButton soundBt;
    @FXML
    private JFXTabPane chatTabPane;
    @FXML
    private Tab chatTab;
    @FXML
    private Tab roomsTab;
    @FXML
    private ListView<ChatRoomInfoBox> roomListView;
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
    @FXML
    private TextField searchRoomTf;
    @FXML
    private ImageView toolbarBg;
    @FXML
    private Label toolbarInfo;
    @FXML
    private GridPane currentRoomIndicator;
    @FXML
    private Label currentRoomNameLabel;
    @FXML
    private JFXButton leaveCurrentRoomBt;

    private MainChatController mainChatController;
    private ContactInfoBox currentSelectedContactInfoBox;
    private BidiMap<String, ContactInfoBox> clientContactInfoBoxMap;
    private HashMap<String, Integer> clientUnreadMsgRegisterMap;
    private BidiMap<String, ChatRoomInfoBox> chatRoomInfoBoxMap;
    private ChatRoomInfoBox currentSelectedChatRoomInfoBox;
    private boolean sound = true;
    private ImageView soundOnImage;
    private ImageView soundOffImage;
    private TranslateTransition showCreateRoomCard;
    private TranslateTransition hideCreateRoomCard;
    private JFXSnackbar notificationSnack;
    private List<ChatRoomInfoBox> chatRoomInfoBoxList;
    private ChatRoom currentRoom;
    private Image noRoom;
    private Image lonely;

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

        createRoomConfirmBt.setDisable(true);

        createRoomNameTf.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if(t1.equals(""))
                    createRoomConfirmBt.setDisable(true);
                else
                    createRoomConfirmBt.setDisable(false);
            }
        });
        chatRoomInfoBoxMap = new DualHashBidiMap<>();

        notificationSnack = new JFXSnackbar(root);
        notificationSnack.setPrefWidth(300);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0));
        notificationSnack.setEffect(dropShadow);

        chatRoomInfoBoxList = new ArrayList<>();

        noRoom = new Image(getClass().getResourceAsStream("/assets/Images/no_room.png"));
        lonely = new Image(getClass().getResourceAsStream("/assets/Images/lonely.png"));
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

            getChatRoomRegisteredList();
        });
    }

    private void setCurrentUserName(String name) {
        currentUserName.setText(name);
    }

    private void setCurrentUserAvatar(Image avatar) {
        currentUserAvatar.setImage(avatar);
    }

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

        getChatRoomRegisteredList();
    }

    public void onRefreshRoomListBtClicked(){
        System.out.println("Clicou no botao para atualizar lista de salas");

        getChatRoomRegisteredList();
    }

    public void onCreateRoomConfirmBtClicked(){
        String roomName = createRoomNameTf.getText();

        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();

        try {
            spaceHandlerInstance.writeChatRoom(roomName);
            showRoomCreatedMessage(roomName);
            hideCreateRoomCard.play();
        } catch (EntryAlreadyInSpaceException e){
            System.out.println(e.getMessage());
            showRoomAlreadyExistMessage(roomName);
        }
    }

    private void showRoomAlreadyExistMessage(String roomName){
        notificationSnack.enqueue(new JFXSnackbar.SnackbarEvent(new Text("Sala " + roomName + "  já existe")));
    }

    private void showRoomCreatedMessage(String roomName){
        notificationSnack.enqueue(new JFXSnackbar.SnackbarEvent(new Text("Sala " + roomName + " criada com sucesso!")));
    }

    public void onCreateRoomCancelBtClicked(){
        hideCreateRoomCard.play();
    }

    private void getChatRoomRegisteredList(){
        chatRoomInfoBoxList.clear();

        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();

        SortedSet<ChatRoom> l = spaceHandlerInstance.readChatRoomRegisteredList();

        SortedSet<ChatRoom> roomList = new TreeSet<>(l);

        displayRoomList(roomList);
    }

    private void displayRoomList(SortedSet<ChatRoom> roomList){
        for(ChatRoom c: roomList){
            displayChatRoom(c);
        }

        FilteredList<ChatRoomInfoBox> filteredData = new FilteredList<>(
                FXCollections.observableList(chatRoomInfoBoxList), box -> true);

        searchRoomTf.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(box ->{
                if(newValue == null || newValue.isEmpty()){
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return box.getRoomName().toLowerCase().startsWith(lowerCaseFilter);
            });
        });

        SortedList<ChatRoomInfoBox> sortedData = new SortedList<>(filteredData);

        roomListView.setItems(sortedData);
    }

    private void displayChatRoom(ChatRoom c){
        ChatRoomInfoBox box = new ChatRoomInfoBox(c, this);

        chatRoomInfoBoxMap.put(c.getName(), box);
        box.setCursor(Cursor.HAND);
        box.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(currentSelectedChatRoomInfoBox != null){
                    currentSelectedChatRoomInfoBox.setFocus(false);
                }
                currentSelectedChatRoomInfoBox = box;
                currentSelectedChatRoomInfoBox.setFocus(true);
            }
        });

        chatRoomInfoBoxList.add(box);
    }

    void onEnterRoomConfirm(ChatRoom room){
        if(currentRoom != null && currentRoom.getName().equals(room.getName())){
            System.out.println("Usuario ja esta na sala " + room.getName());

            notificationSnack.enqueue(new JFXSnackbar.SnackbarEvent(new Text("Você já está nesta sala")));
        }
        else {
            System.out.println("Entrando na sala " + room.getName());

            if(currentRoom != null){
                leaveCurrentRoom();
            }

            SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();

            enterRoom(room.getName());

            currentRoom = spaceHandlerInstance.readRoom(room.getName());

            chatTabPane.getSelectionModel().select(0);

            currentRoomNameLabel.setText(currentRoom.getName());

            List<Client> others = currentRoom.getConnectedClientList().stream().
                    filter(c -> !c.getName().equals(mainChatController.getCurrentClient().getName()))
                    .collect(Collectors.toList());
            if (others.size() == 0) {
                showLonelyBg();
            } else {
                this.toolbarBg.setVisible(false);
                currentRoomIndicator.setVisible(true);
                contactListView.setVisible(true);

                displayRoomContactList(others);
            }

            spaceHandlerInstance.startChatRoomInteractionListener(currentRoom);
        }
    }

    private void displayRoomContactList(List<Client> clientList){
        contactListView.getItems().clear();

        for(Client c: clientList){
            displayContact(c);
        }
    }

    private void displayContact(Client c){
        Image avatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" +
                c.getAvatarName())
        );
        ContactInfoBox contactInfoBox = new ContactInfoBox(avatar, c.getName());

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

                /*Platform.runLater(() -> {
                    mainChatController.getChatController()
                            .setCurrentCollocutor(contactInfoBoxClientMap.get(contactInfoBox));
                });*/
            }
        });
        Platform.runLater(() -> {
            contactListView.getItems().add(contactInfoBox);
        });
    }

    public void leaveCurrentRoom(){
        System.out.println("Saindo da sala " + currentRoom.getName());

        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();

        spaceHandlerInstance.writeLeaveRoomInteraction(mainChatController.getCurrentClient(), currentRoom.getName());

        spaceHandlerInstance.stopChatRoomInteractionListener();

        clientContactInfoBoxMap.clear();
        contactListView.getItems().clear();

        currentRoom = null;

        showNoRoomBg();
    }

    private void enterRoom(String roomName){
        SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();

        spaceHandlerInstance.writeEnterRoomInteraction(mainChatController.getCurrentClient(), roomName);
    }

    private void showNoRoomBg(){
        currentRoomIndicator.setVisible(false);
        this.toolbarBg.setVisible(true);
        this.toolbarBg.setImage(noRoom);
        this.toolbarBg.setLayoutY(0);
        this.toolbarInfo.setText("Você não está em nenhuma sala");
        contactListView.setVisible(false);
    }

    private void showLonelyBg(){
        System.out.println("Cliente ficou sozinho na sala, exibindo imagem de lonely :(");

        currentRoomIndicator.setVisible(true);
        this.toolbarBg.setVisible(true);
        this.toolbarBg.setImage(lonely);
        this.toolbarBg.setLayoutY(80);
        this.toolbarInfo.setText("Acho que você é o único aqui");
        contactListView.setVisible(false);
    }

    public ChatRoom getCurrentRoom(){
        return currentRoom;
    }

    public void onNewClientEnteredRoom(Client newClient){
        currentRoom.getConnectedClientList().add(newClient);
        notificationSnack.enqueue(new JFXSnackbar.SnackbarEvent(new Text(newClient.getName() + " entrou na sala")));
        this.toolbarBg.setVisible(false);
        currentRoomIndicator.setVisible(true);
        contactListView.setVisible(true);

        displayContact(newClient);
    }

    public void onClientLeftRoom(Client client){
        System.out.println("Removendo cliente " + client.getName() + " da lista de contatos");

        System.out.println("Existem os seguintes contatos na sala: ");
        for(Client c: currentRoom.getConnectedClientList()){
            System.out.println(c.getName());
        }

        currentRoom.getConnectedClientList().remove(client);
        clientContactInfoBoxMap.remove(client.getName());

        System.out.println("Clientes restantes na sala atual " + currentRoom.getName() + ": " +
                currentRoom.getConnectedClientList().size());

        for(Client c: currentRoom.getConnectedClientList()){
            System.out.println(c.getName());
        }


        Platform.runLater(() -> {
            notificationSnack.enqueue(new JFXSnackbar.SnackbarEvent(new Text(client.getName() + " saiu da sala")));

            contactListView.getItems().removeIf(box -> box.getUserName().equals(client.getName()));

            if(currentRoom.getConnectedClientList().stream()
                    .filter(c -> !c.getName().equals(mainChatController.getCurrentClient().getName()))
                    .count() == 0) {
                showLonelyBg();
            }
        });
    }
}