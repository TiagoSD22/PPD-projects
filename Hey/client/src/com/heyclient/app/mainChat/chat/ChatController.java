package com.heyclient.app.mainChat.chat;


import com.hey.common.ChatMessage;
import com.hey.common.Client;
import com.hey.common.Status;
import com.heyclient.app.mainChat.MainChatController;
import com.heyclient.app.services.AudioService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import java.util.Date;
import java.util.logging.SimpleFormatter;


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

    public void init(MainChatController mainChatController) {
        sendMessageBt.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream("/assets/Images/send_message.png"))));
        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);
        sendMessageBt.setEffect(ds);

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
            /*if(oldValue.length() > 0 && newValue.length() == 0){ //parou de digitar
                mainGameController.getClientStub().sendTypingStatusMessage(TypingStatus.STOPED);
            }
            else if(oldValue.length() == 0 && newValue.length() > 0){ //comecou a digitar
                mainGameController.getClientStub().sendTypingStatusMessage(TypingStatus.TYPING);
            }*/
        });
    }

    public void sendMessage() {
        String text = textInput.getText();
        if(!text.isEmpty()) {
            textInput.clear();

            mainChatController.getMessageHandler().sendMessageToBroker(currentCollocutor, text);
        }
    }

    /*public void showTypingStatus(TypingStatus status){
        if(status == TypingStatus.TYPING){
            typingStatus.setText("Digitando...");
        }
        else{
            typingStatus.setText("");
        }
    }*/

    public synchronized void displayIncomingMessage(Image avatar, String text) {
        Task<HBox> messageBox = new Task<HBox>() {

            @Override
            protected HBox call() throws Exception {
                HBox messageBody = new HBox();

                Label ta = new Label();
                ta.setWrapText(true);
                ta.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255),
                        null, null)));
                ta.setText(text);
                ta.setPrefSize(Label.USE_COMPUTED_SIZE, Label.USE_COMPUTED_SIZE);
                ta.setPadding(new Insets(10, 7, 10, 7));

                DropShadow ds = new DropShadow();
                ds.setOffsetX(1.3);
                ds.setOffsetY(1.3);
                ds.setColor(Color.BLACK);

                ta.setEffect(ds);

                ImageView playerAvatar = new ImageView(avatar);
                playerAvatar.setFitHeight(52);
                playerAvatar.setFitWidth(52);

                messageBody.getChildren().addAll(playerAvatar, ta);
                messageBody.setSpacing(5);
                messageBody.setPadding(new Insets(5, 0, 5, 0));
                messageBody.setAlignment(Pos.TOP_LEFT);
                messageBody.setTranslateX(5);
                messageBody.setMaxWidth(290);
                return messageBody;
            }
        };

        messageBox.setOnSucceeded(event -> {
            messageArea.getItems().add(messageBox.getValue());
            messageArea.scrollTo(messageArea.getItems().size());
            AudioService.getInstance().playIncomingMessageSound();
        });

        Thread t = new Thread(messageBox);
        t.setDaemon(true);
        t.start();
    }

    public synchronized void displayOwnMessage(Image avatar, String text) {
        Task<HBox> messageBox = new Task<HBox>() {

            @Override
            protected HBox call() throws Exception {
                HBox messageBody = new HBox();

                Label ta = new Label();
                ta.setWrapText(true);
                ta.setBackground(new Background(new BackgroundFill(Color.rgb(184, 213, 184),
                        null, null)));
                ta.setText(text);
                ta.setPrefSize(Label.USE_COMPUTED_SIZE, Label.USE_COMPUTED_SIZE);
                ta.setPadding(new Insets(10, 7, 10, 7));

                DropShadow ds = new DropShadow();
                ds.setOffsetX(1.3);
                ds.setOffsetY(1.3);
                ds.setColor(Color.BLACK);

                ta.setEffect(ds);

                ImageView playerAvatar = new ImageView(avatar);
                playerAvatar.setFitHeight(52);
                playerAvatar.setFitWidth(52);

                messageBody.getChildren().addAll(ta, playerAvatar);
                messageBody.setSpacing(5);
                messageBody.setPadding(new Insets(5, 15, 5, 0));
                messageBody.setAlignment(Pos.TOP_RIGHT);
                messageBody.setTranslateX(50);
                messageBody.setMaxWidth(290);
                return messageBody;
            }
        };

        messageBox.setOnSucceeded(event -> {
            messageArea.getItems().add(messageBox.getValue());
            messageArea.scrollTo(messageArea.getItems().size());
        });

        Thread t = new Thread(messageBox);
        t.setDaemon(true);
        t.start();
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
        this.emptyChatBg.setVisible(false);
        currentCollocutor = c;
        setCurrentCollocutorAvatar(new Image(getClass().getResourceAsStream("/assets/Images/avatars/"
                + c.getAvatarName())));
        setCurrentCollocutorName(c.getName());

        setCurrentCollocutorStatus(c);
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
            currentCollocutorStatusInfo.setStroke(Color.valueOf("#087e8b"));
        }
        else{
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm");
            currentCollocutorStatus.setText("Visto por último " + df.format(c.getLastSeen()));
            currentCollocutorStatusInfo.setStroke(Color.valueOf("#707070"));
        }
    }

    public void clearMessages(){
        messageArea.getItems().clear();
    }

    public void onMessageReceived(ChatMessage msg){
        if(currentCollocutor != null && msg.getSender().getName().equals(currentCollocutor.getName())){
            System.out.println("Recebida mensagem do cliente com conversa em aberta no momento");
            // exibir mensagem da conversa em aberto
        }
        else{
            System.out.println("Registrando mensagem nao lida para o cliente " + msg.getSender().getName());
            Platform.runLater(() -> {
                mainChatController.getToolbarController().registerUnreadMsg(msg.getSender());
            });
        }
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
}
