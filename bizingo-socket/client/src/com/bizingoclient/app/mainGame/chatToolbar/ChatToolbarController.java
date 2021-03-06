package com.bizingoclient.app.mainGame.chatToolbar;


import bizingo.commons.TypingStatus;
import com.bizingoclient.app.mainGame.MainGameController;
import com.bizingoclient.app.services.AudioService;
import com.jfoenix.controls.JFXButton;
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


public class ChatToolbarController {

    private MainGameController main;
    @FXML
    private JFXButton sendMessageBt;
    @FXML
    private TextArea textInput;
    @FXML
    private ListView messageArea;
    @FXML
    private Text otherPlayerNickname;
    @FXML
    private ImageView otherPlayerAvatar;
    @FXML
    private StackPane otherPlayerInfoRegion;
    @FXML
    private Text typingStatus;

    public void init(MainGameController mainGameController) {
        sendMessageBt.setGraphic(new ImageView(new Image(getClass()
                .getResourceAsStream("/assets/Images/send_message.png"))));
        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.BLACK);
        sendMessageBt.setEffect(ds);

        main = mainGameController;
        Circle clip = new Circle(36);
        clip.setStroke(Color.valueOf("#B8D5B8"));
        clip.setStrokeWidth(3);
        clip.setFill(Color.rgb(0, 0, 0, 0.7));
        otherPlayerInfoRegion.getChildren().add(clip);
        clip.toBack();

        Image image = new Image(getClass().getResourceAsStream("/assets/Images/chat_bg.jpg"));
        BackgroundSize backgroundSize = new BackgroundSize(380, 627, false,
                false, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, backgroundSize);
        Background background = new Background(backgroundImage);

        messageArea.setFocusTraversable(false);
        messageArea.setBackground(background);

        removeNewLineEvent();

        textInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue.length() > 0 && newValue.length() == 0){ //parou de digitar
                mainGameController.getMessageHandler().sendTypingStatusMessage(TypingStatus.STOPED);
            }
            else if(oldValue.length() == 0 && newValue.length() > 0){ //comecou a digitar
                mainGameController.getMessageHandler().sendTypingStatusMessage(TypingStatus.TYPING);
            }
        });
    }

    public void sendMessage() {
        String text = textInput.getText();
        if(!text.isEmpty()) {
            main.getMessageHandler().sendMessage(text);
            textInput.clear();
        }
    }

    public void showTypingStatus(TypingStatus status){
        if(status == TypingStatus.TYPING){
            typingStatus.setText("Digitando...");
        }
        else{
            typingStatus.setText("");
        }
    }

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

    public void setOtherPlayerAvatar(Image avatar) {
        otherPlayerAvatar.setImage(avatar);
    }

    public void setOtherPlayerNickname(String nickname) {
        otherPlayerNickname.setText(nickname);
    }

    public void clearMessages(){
        messageArea.getItems().clear();
    }

}
