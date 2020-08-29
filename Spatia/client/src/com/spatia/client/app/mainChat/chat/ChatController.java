package com.spatia.client.app.mainChat.chat;


import com.spatia.client.app.mainChat.MainChatController;
import com.spatia.client.app.services.AudioService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.spatia.client.app.services.SpaceHandler;
import com.spatia.common.ChatMessage;
import com.spatia.common.ChatRoomMessage;
import com.spatia.common.Client;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.*;


public class ChatController {

    private MainChatController mainChatController;
    @FXML
    private AnchorPane root;
    @FXML
    private JFXButton sendMessageBt;
    @FXML
    private TextArea textInput;
    @FXML
    private VBox messageArea;
    @FXML
    private ImageView currentCollocutorAvatar;
    @FXML
    private Text currentCollocutorName;
    @FXML
    private Text currentCollocutorStatus;
    @FXML
    private StackPane currentCollocutorInfoRegion;
    @FXML
    private StackPane emptyChatBg;
    @FXML
    private ScrollPane messageAreaPane;

    private JFXSnackbar notificationSnack;
    private Client currentCollocutor;
    private boolean isRoomCurrentCollocutor;
    private Circle currentCollocutorStatusInfo;
    private HashMap<String, List<ChatMessageBox>> conversationMap;
    private List<ChatMessageBox> chatConversation;
    private List<ChatMessageBox> roomConversation;
    private Image roomIcon;

    public void init(MainChatController mainChatController) {
        sendMessageBt.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream("/assets/Images/send_message.png"))));
        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);
        sendMessageBt.setEffect(ds);

        conversationMap = new HashMap<>();
        chatConversation = new ArrayList<>();
        roomConversation = new ArrayList<>();

        this.mainChatController = mainChatController;

        Image image = new Image(getClass().getResourceAsStream("/assets/Images/chat_bg.jpg"));
        BackgroundSize backgroundSize = new BackgroundSize(978, 588, false,
                false, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        Background background = new Background(backgroundImage);

        messageArea.setFocusTraversable(false);
        messageArea.setBackground(background);

        removeNewLineEvent();

        notificationSnack = new JFXSnackbar(root);
        notificationSnack.setPrefWidth(300);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0));
        notificationSnack.setEffect(dropShadow);

        roomIcon = new Image(getClass().getResourceAsStream("/assets/Images/room.png"));

        /*textInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue.length() > 0 && newValue.length() == 0){ //parou de digitar
                mainChatController.getMessageHandler()
                        .sendTypingStatusMessageToBroker(mainChatController.getCurrentClient(), currentCollocutor, TypingStatus.STOPED);
            }
            else if(oldValue.length() == 0 && newValue.length() > 0){ //comecou a digitar
                mainChatController.getMessageHandler()
                        .sendTypingStatusMessageToBroker(mainChatController.getCurrentClient(), currentCollocutor, TypingStatus.TYPING);
            }
        });*/
    }

    public void sendMessage() {
        String text = textInput.getText();
        if(!text.isEmpty()) {
            textInput.clear();
            if(isRoomCurrentCollocutor){
                SpaceHandler.getInstance().writeRoomChatMessage(
                        mainChatController.getToolbarController().getCurrentRoom().getName(),
                        mainChatController.getCurrentClient().getName(),
                        text);
            }
            else{
                SpaceHandler.getInstance().writeDirectMessage(
                        mainChatController.getCurrentClient().getName(),
                        currentCollocutor.getName(),
                        text);
            }
            addOwnMessageToConversation(text);
        }
    }

    /*public void showTypingStatus(String senderName, TypingStatus typingStatus){
        if(currentCollocutor != null && currentCollocutor.getName().equals(senderName)){
            if(typingStatus.equals(TypingStatus.TYPING)) {
                currentCollocutorStatus.setText("Digitando...");
                currentCollocutorStatus.setFill(Color.valueOf("#0ab9c2"));
            }
            else{
                currentCollocutorStatus.setText(currentCollocutor.getStatus().getValue().toLowerCase());
                currentCollocutorStatus.setFill(Color.valueOf("#2f2f2f"));
            }
        }
        else{
            mainChatController.getToolbarController().updateClientTypingStatus(senderName, typingStatus);
        }
    }*/

    private void addIncomingMessageToConversation(String sender, String text) {
        if(!conversationMap.containsKey(sender)){
            conversationMap.put(sender, new ArrayList<>());
        }

        List<ChatMessageBox> conversation = conversationMap.get(sender);

        ChatMessageBox messageBox = new ChatMessageBox(text, false, sender, false);
        conversation.add(messageBox);

        if(currentCollocutor != null && currentCollocutor.getName().equals(sender) && !isRoomCurrentCollocutor){
            messageArea.getChildren().add(messageBox);
        }
    }

    private void addRoomMessageToConversation(String sender, String text){
        ChatMessageBox messageBox = new ChatMessageBox(text, false, sender, true);
        roomConversation.add(messageBox);

        if(isRoomCurrentCollocutor){
            messageArea.getChildren().add(messageBox);
        }
    }

    private void addOwnMessageToConversation(String text) {
        ChatMessageBox messageBox = new ChatMessageBox(text, true, "", false);

        if(!isRoomCurrentCollocutor) {
            if (!conversationMap.containsKey(currentCollocutor.getName())) {
                conversationMap.put(currentCollocutor.getName(), new ArrayList<>());
            }

            List<ChatMessageBox> conversation = conversationMap.get(currentCollocutor.getName());
            conversation.add(messageBox);
        }
        else{
            roomConversation.add(messageBox);
        }

        messageArea.getChildren().add(messageBox);
    }

    private void removeNewLineEvent() {
        textInput.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isShiftDown()) {
                    if (event.getCode() == KeyCode.ENTER) {
                        textInput.setText(textInput.getText() + "\n");
                        textInput.positionCaret(textInput.getText().length());
                    }
                } else {
                    if (event.getCode() == KeyCode.ENTER) {
                        event.consume();
                        sendMessage();
                    }
                }
            }
        });
    }

    public void setCurrentCollocutor(Client c){
        boolean collocutorChanged = false;

        if(currentCollocutor == null || !currentCollocutor.getName().equals(c.getName())){
            collocutorChanged = true;
        }

        this.emptyChatBg.setVisible(false);
        currentCollocutor = c;
        setCurrentCollocutorAvatar(new Image(getClass().getResourceAsStream("/assets/Images/avatars/"
                + c.getAvatarName())));
        setCurrentCollocutorName(c.getName());

        if(collocutorChanged || isRoomCurrentCollocutor) {
            messageArea.getChildren().clear();
            textInput.clear();
            loadCurrentCollocutorConversation();
        }

        isRoomCurrentCollocutor = false;
    }

    private void setCurrentCollocutorAvatar(Image avatar) {
        currentCollocutorAvatar.setImage(avatar);
    }

    private void setCurrentCollocutorName(String userName) {
        currentCollocutorName.setText(userName);
    }

    public void onMessageReceived(ChatMessage msg){
        if(currentCollocutor == null || !msg.getSenderName().equals(currentCollocutor.getName())
                || isRoomCurrentCollocutor){
            System.out.println("Registrando mensagem nao lida para o cliente " + msg.getSenderName());
            Platform.runLater(() -> {
                mainChatController.getToolbarController().registerUnreadMsg(msg.getSenderName());
            });
        }

        AudioService.getInstance().playIncomingMessageSound();

        addIncomingMessageToConversation(msg.getSenderName(), msg.getText());
    }

    public void onRoomMessageReceived(String sender, String text){
        if(!isRoomCurrentCollocutor){
            System.out.println("Registrando nova mensagem nao lida na sala");
            Platform.runLater(() -> {
                mainChatController.getToolbarController().registerRoomUnreadMsg();
            });
        }

        AudioService.getInstance().playIncomingMessageSound();
        addRoomMessageToConversation(sender, text);
    }

    public void onClientDisconnected(Client c){
        if(currentCollocutor != null && currentCollocutor.getName().equals(c.getName())){
            emptyChatBg.setVisible(true);
        }
    }

    private void loadCurrentCollocutorConversation(){
        if(conversationMap.containsKey(currentCollocutor.getName())) {
            List<ChatMessageBox> conversation = conversationMap.get(currentCollocutor.getName());
            messageArea.getChildren().addAll(conversation);
        }
    }

    public void setRoomAsCurrentCollocutor(boolean value){
        if(value && !isRoomCurrentCollocutor){
            textInput.clear();
        }

        isRoomCurrentCollocutor = value;

        Platform.runLater(() -> {
            this.emptyChatBg.setVisible(false);
            setCurrentCollocutorAvatar(roomIcon);
            setCurrentCollocutorName(mainChatController.getToolbarController().getCurrentRoom().getName());
        });

        messageArea.getChildren().clear();
        messageArea.getChildren().addAll(roomConversation);
    }

    public void onRoomLeft(){
        this.emptyChatBg.setVisible(true);
        currentCollocutor = null;
        isRoomCurrentCollocutor = false;

        conversationMap.clear();
        roomConversation.clear();
    }

    public void showEmptyBg(){
        emptyChatBg.setVisible(true);
    }
}
