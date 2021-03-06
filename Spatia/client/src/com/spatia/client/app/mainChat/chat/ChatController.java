package com.spatia.client.app.mainChat.chat;


import com.spatia.client.app.mainChat.MainChatController;
import com.spatia.client.app.services.AudioService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.spatia.client.app.services.SpaceHandler;
import com.spatia.common.ChatMessage;
import com.spatia.common.ChatRoom;
import com.spatia.common.Client;
import com.spatia.common.TypingStatus;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
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
import java.util.stream.Collectors;


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
        sendMessageBt.setTooltip(new Tooltip("Enviar mensagem"));

        textInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!isRoomCurrentCollocutor) {
                if (oldValue.length() > 0 && newValue.length() == 0) { //parou de digitar
                    SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();
                    spaceHandlerInstance.writeTypingStatus(currentCollocutor.getName(), TypingStatus.STOPED);
                } else if (oldValue.length() == 0 && newValue.length() > 0) { //comecou a digitar
                    SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();
                    spaceHandlerInstance.writeTypingStatus(currentCollocutor.getName(), TypingStatus.TYPING);
                }
            }
        });
    }

    public void sendMessage() {
        String text = textInput.getText();
        if(!text.isEmpty()) {
            textInput.clear();
            if(isRoomCurrentCollocutor){
                SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();

                ChatRoom currentRoom = mainChatController.getToolbarController().getCurrentRoom();
                Client currentClient = mainChatController.getCurrentClient();

                List<String> forwardToList = currentRoom.getConnectedClientList().stream()
                        .filter( c -> !c.getName().equals(currentClient.getName()))
                        .map(Client::getName)
                        .collect(Collectors.toList());

                spaceHandlerInstance.writeRoomChatMessage(currentRoom.getName(),
                        currentClient.getName(), forwardToList, text);

                Platform.runLater(() -> {
                    mainChatController.getToolbarController().registerRoomLastMessage(
                            mainChatController.getCurrentClient().getName(), text);
                });
            }
            else{
                SpaceHandler.getInstance().writeDirectMessage(
                        mainChatController.getCurrentClient().getName(),
                        currentCollocutor.getName(),
                        text);

                Platform.runLater(() -> {
                    mainChatController.getToolbarController().registerLastMessage(currentCollocutor.getName(), text);
                });
            }
            addOwnMessageToConversation(text);
        }
    }

    public void showTypingStatus(String senderName, TypingStatus typingStatus){
        if(currentCollocutor != null && currentCollocutor.getName().equals(senderName)){
            if(typingStatus.equals(TypingStatus.TYPING)) {
                currentCollocutorStatus.setText("Digitando...");
                currentCollocutorStatus.setFill(Color.valueOf("#081521"));
            }
            else{
                currentCollocutorStatus.setText("");
                currentCollocutorStatus.setFill(Color.valueOf("#1D100C"));
            }
        }
        else{
            mainChatController.getToolbarController().updateClientTypingStatus(senderName, typingStatus);
        }
    }

    private void addIncomingMessageToConversation(String sender, String text) {
        if(!conversationMap.containsKey(sender)){
            conversationMap.put(sender, new ArrayList<>());
        }

        List<ChatMessageBox> conversation = conversationMap.get(sender);
        boolean drawBubbleIndicator = false;
        Insets boxInsets;

        if(conversation.size() == 0 ||
                (conversation.get(conversation.size() - 1)).isMyOwn()){
            boxInsets = new Insets(15, 30, 0, 30);
            drawBubbleIndicator = true;
        }
        else{
            boxInsets = new Insets(0, 30, 0, 40);
        }

        ChatMessageBox messageBox = new ChatMessageBox(text, false, sender, false, drawBubbleIndicator);
        messageBox.setPadding(boxInsets);

        conversation.add(messageBox);

        if(currentCollocutor != null && currentCollocutor.getName().equals(sender)){
            messageArea.getChildren().add(messageBox);
            messageAreaPane.setVvalue(1);
        }
    }

    private void addRoomMessageToConversation(String sender, String text){
        ChatMessageBox messageBox = null;

        if(roomConversation.size() == 0 ||
                !(roomConversation.get(roomConversation.size() - 1)).getSender()
                        .equals(sender)){

            messageBox = new ChatMessageBox(text, false, sender, true, true);
            messageBox.setPadding(new Insets(15, 30, 0, 30));
        }
        else{
            messageBox = new ChatMessageBox(text, false, sender, false, false);
            messageBox.setPadding(new Insets(0, 30, 0, 40));
        }

        roomConversation.add(messageBox);

        if(isRoomCurrentCollocutor){
            messageArea.getChildren().add(messageBox);
            messageAreaPane.setVvalue(1);
        }
    }

    private void addOwnMessageToConversation(String text) {
        Insets boxInsets;
        boolean drawBubbleIndicator = false;

        if(messageArea.getChildren().size() == 0 ||
                !((ChatMessageBox)messageArea.getChildren().get(messageArea.getChildren().size() - 1)).isMyOwn()){
            boxInsets = new Insets(15, 30, 0, 30);
            drawBubbleIndicator = true;
        }
        else{
            boxInsets = new Insets(0, 40, 0, 30);
        }

        ChatMessageBox messageBox = new ChatMessageBox(text, true, "", false, drawBubbleIndicator);
        messageBox.setPadding(boxInsets);

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
        messageAreaPane.setVvalue(1);
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

        Platform.runLater(() -> {
            mainChatController.getToolbarController().registerLastMessage(msg.getSenderName(), msg.getText());
        });

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

        Platform.runLater(() -> {
            mainChatController.getToolbarController().registerRoomLastMessage(sender, text);
        });

        AudioService.getInstance().playIncomingMessageSound();
        addRoomMessageToConversation(sender, text);
    }

    public void onClientDisconnected(Client c){
        if(currentCollocutor != null && currentCollocutor.getName().equals(c.getName())){
            currentCollocutor = null;
            isRoomCurrentCollocutor = false;
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

        currentCollocutor = null;
        messageArea.getChildren().clear();

        isRoomCurrentCollocutor = value;

        Platform.runLater(() -> {
            this.emptyChatBg.setVisible(false);
            setCurrentCollocutorAvatar(roomIcon);
            setCurrentCollocutorName(mainChatController.getToolbarController().getCurrentRoom().getName());

            messageArea.getChildren().addAll(roomConversation);
        });
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
