package com.heyclient.app.mainChat.chat;


import com.hey.common.ChatMessage;
import com.hey.common.Client;
import com.hey.common.Status;
import com.hey.common.TypingStatus;
import com.heyclient.app.mainChat.MainChatController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

import java.text.SimpleDateFormat;
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
    private ListView messageArea;
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

    private JFXSnackbar notificationSnack;
    private Client currentCollocutor;
    private Circle currentCollocutorStatusInfo;
    private HashMap<String, List<HBox>> conversationMap;

    public void init(MainChatController mainChatController) {
        sendMessageBt.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream("/assets/Images/send_message.png"))));
        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);
        sendMessageBt.setEffect(ds);

        conversationMap = new HashMap<>();

        this.mainChatController = mainChatController;
        currentCollocutorStatusInfo = new Circle(36);
        currentCollocutorStatusInfo.setStrokeWidth(3);
        currentCollocutorStatusInfo.setFill(Color.valueOf("#2f2f2f"));
        currentCollocutorStatusInfo.setTranslateX(5);
        currentCollocutorInfoRegion.getChildren().add(currentCollocutorStatusInfo);
        currentCollocutorStatusInfo.toBack();

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

        textInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue.length() > 0 && newValue.length() == 0){ //parou de digitar
                mainChatController.getMessageHandler()
                        .sendTypingStatusMessageToBroker(mainChatController.getCurrentClient(), currentCollocutor, TypingStatus.STOPED);
            }
            else if(oldValue.length() == 0 && newValue.length() > 0){ //comecou a digitar
                mainChatController.getMessageHandler()
                        .sendTypingStatusMessageToBroker(mainChatController.getCurrentClient(), currentCollocutor, TypingStatus.TYPING);
            }
        });
    }

    public void sendMessage() {
        String text = textInput.getText();
        if(!text.isEmpty()) {
            textInput.clear();
            mainChatController.getMessageHandler().sendMessageToBroker(currentCollocutor, text);
            addOwnMessageToConversation(text);
        }
    }

    public void showTypingStatus(String senderName, TypingStatus typingStatus){
        if(currentCollocutor != null && currentCollocutor.getName().equals(senderName)){
            if(typingStatus.equals(TypingStatus.TYPING)) {
                currentCollocutorStatus.setText("Digitando...");
                currentCollocutorStatus.setFill(Color.valueOf("#0ab9c2"));
            }
            else{
                currentCollocutorStatus.setText(currentCollocutor.getStatus().getValue().toLowerCase());
                currentCollocutorStatus.setFill(Color.valueOf("#fcfcfc"));
            }
        }
        else{
            mainChatController.getToolbarController().updateClientTypingStatus(senderName, typingStatus);
        }
    }

    private void addIncomingMessageToConversation(String receiverName, String text) {
        HBox messageBody = new HBox();

        Label ta = new Label();
        ta.setWrapText(true);
        ta.setBackground(new Background(new BackgroundFill(Color.valueOf("#fcfcfc"),
                null, null)));
        ta.setText(text);
        ta.setPrefSize(Label.USE_COMPUTED_SIZE, Label.USE_COMPUTED_SIZE);
        ta.setPadding(new Insets(10, 7, 10, 7));

        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);

        ta.setEffect(ds);

        messageBody.getChildren().add(ta);
        messageBody.setPadding(new Insets(5, 0, 5, 0));
        messageBody.setAlignment(Pos.TOP_LEFT);
        messageBody.setTranslateX(10);
        messageBody.setMaxWidth(650);

        if(!conversationMap.containsKey(receiverName)){
            conversationMap.put(receiverName, new ArrayList<>());
        }

        List<HBox> conversation = conversationMap.get(receiverName);
        conversation.add(messageBody);

        if(currentCollocutor != null && currentCollocutor.getName().equals(receiverName)){
            messageArea.getItems().add(messageBody);
            messageArea.scrollTo(messageArea.getItems().size());
        }
    }

    private void addOwnMessageToConversation(String text) {
        HBox messageBody = new HBox();

        Label ta = new Label();
        ta.setWrapText(true);
        ta.setBackground(new Background(new BackgroundFill(Color.valueOf("#0ab9c2"),
                null, null)));
        ta.setText(text);
        ta.setPrefSize(Label.USE_COMPUTED_SIZE, Label.USE_COMPUTED_SIZE);
        ta.setPadding(new Insets(10, 7, 10, 7));

        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);

        ta.setEffect(ds);

        messageBody.getChildren().add(ta);
        messageBody.setPadding(new Insets(5, 0, 5, 0));
        messageBody.setAlignment(Pos.TOP_RIGHT);
        messageBody.setTranslateX(300);
        messageBody.setMaxWidth(650);

        if(!conversationMap.containsKey(currentCollocutor.getName())){
            conversationMap.put(currentCollocutor.getName(), new ArrayList<>());
        }

        List<HBox> conversation = conversationMap.get(currentCollocutor.getName());
        conversation.add(messageBody);

        messageArea.getItems().add(messageBody);
        messageArea.scrollTo(messageArea.getItems().size());
    }

    public void removeNewLineEvent() {
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

        setCurrentCollocutorStatus(c);

        if(collocutorChanged) {
            messageArea.getItems().clear();
            loadCurrentCollocutorConversation();
        }
    }

    private void setCurrentCollocutorAvatar(Image avatar) {
        currentCollocutorAvatar.setImage(avatar);
    }

    private void setCurrentCollocutorName(String userName) {
        currentCollocutorName.setText(userName);
    }

    private void setCurrentCollocutorStatus(Client c){
        if(c.getStatus().equals(Status.ONLINE)){
            currentCollocutorStatus.setText("online");
            currentCollocutorStatus.setFill(Color.valueOf("#fcfcfc"));
            currentCollocutorStatusInfo.setStroke(Color.valueOf("#087e8b"));
        }
        else{
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm");
            currentCollocutorStatus.setText("Visto por último " + df.format(c.getLastSeen()));
            currentCollocutorStatus.setFill(Color.valueOf("#fcfcfc"));
            currentCollocutorStatusInfo.setStroke(Color.valueOf("#707070"));
        }
    }

    public void onMessageReceived(ChatMessage msg){
        if(currentCollocutor == null || !msg.getSender().getName().equals(currentCollocutor.getName())){
            System.out.println("Registrando mensagem nao lida para o cliente " + msg.getSender().getName());
            Platform.runLater(() -> {
                mainChatController.getToolbarController().registerUnreadMsg(msg.getSender());
            });
        }

        addIncomingMessageToConversation(msg.getSender().getName(), msg.getText());
    }

    public void showNewClientConnectedNotification(Client c){
        System.out.println("Novo cliente " + c.getName() + " conectado!");
        notificationSnack.enqueue(new JFXSnackbar.SnackbarEvent(new Text(c.getName() + " entrou no servidor")));
    }

    public void updateClientStatus(String clientName, Status newStatus, Date lastSeen){
        if(currentCollocutor != null && currentCollocutor.getName().equals(clientName)){
            currentCollocutor.setStatus(newStatus);
            currentCollocutor.setLastSeen(lastSeen);
            setCurrentCollocutor(currentCollocutor);
        }
    }

    public void updateClientAvatar(String clientName, String newAvatar){
        if(currentCollocutor != null && currentCollocutor.getName().equals(clientName)){
            currentCollocutor.setAvatarName(newAvatar);
            setCurrentCollocutor(currentCollocutor);
        }
    }

    private void loadCurrentCollocutorConversation(){
        if(conversationMap.containsKey(currentCollocutor.getName())) {
            List<HBox> conversation = conversationMap.get(currentCollocutor.getName());
            messageArea.getItems().addAll(conversation);
        }
    }
}
