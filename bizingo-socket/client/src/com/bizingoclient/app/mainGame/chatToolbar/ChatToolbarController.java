package com.bizingoclient.app.mainGame.chatToolbar;


import com.bizingoclient.app.mainGame.MainGameController;
import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
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
    private ScrollPane messageArea;
    @FXML
    private Text otherPlayerNickname;
    @FXML
    private ImageView otherPlayerAvatar;
    @FXML
    private StackPane otherPlayerInfoRegion;
    private VBox messageAreaRoot;

    private ImageView teste = new ImageView(new Image(getClass().getResourceAsStream("/assets/avatars/logan.png")));


    public void init(MainGameController mainGameController) {
        sendMessageBt.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/assets/send_message.png"))));
        main = mainGameController;
        Circle clip = new Circle(36);
        clip.setStroke(Color.valueOf("#9CEAEF"));
        clip.setStrokeWidth(3);
        clip.setFill(Color.rgb(0, 0, 0, 0.7));
        otherPlayerInfoRegion.getChildren().add(clip);
        clip.toBack();

        messageAreaRoot = new VBox();
        messageAreaRoot.setPrefWidth(380);
        messageAreaRoot.setMaxWidth(380);
        messageAreaRoot.setMinWidth(380);
        messageAreaRoot.setSpacing(15);
        messageArea.setContent(messageAreaRoot);
        messageArea.setPannable(true);

        addShortcutSendMessageBt();
    }

    private void addShortcutSendMessageBt(){
        Scene scene = sendMessageBt.getScene();
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.ENTER),
                new Runnable() {
                    @FXML public void run() {
                        sendMessageBt.fire();
                    }
                }
        );
    }

    public void sendMessage() {
        String text = textInput.getText();
        //main.getMessageHandler().sendMessage(text);
        displayOwnMessage(null, text);
        textInput.clear();
    }

    public synchronized void displayOwnMessage(Image avatar, String text){
        Task<HBox> messageBox = new Task<HBox>(){

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

                ImageView playerAvatar = new ImageView(new Image(getClass().getResourceAsStream("/assets/avatars/logan.png")));
                playerAvatar.setFitHeight(52);
                playerAvatar.setFitWidth(52);

                messageBody.getChildren().addAll(ta, playerAvatar);
                messageBody.setSpacing(5);
                messageBody.setPadding(new Insets(5, 0, 0, 0));
                messageBody.setAlignment(Pos.TOP_RIGHT);
                messageBody.setTranslateX(65);
                messageBody.setMaxWidth(300);
                return messageBody;
            }
        };

        messageBox.setOnSucceeded(event -> messageAreaRoot.getChildren().add(messageBox.getValue()));

        Thread t = new Thread(messageBox);
        t.setDaemon(true);
        t.start();
    }

    public void setOtherPlayerAvatar(Image avatar){
        otherPlayerAvatar.setImage(avatar);
    }

    public void setOtherPlayerNickname(String nickname){
        otherPlayerNickname.setText(nickname);
    }

}
