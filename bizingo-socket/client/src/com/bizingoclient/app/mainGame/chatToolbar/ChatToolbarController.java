package com.bizingoclient.app.mainGame.chatToolbar;


import com.bizingoclient.app.mainGame.MainGameController;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
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


    public void init(MainGameController mainGameController) {
        sendMessageBt.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/assets/send_message.png"))));
        main = mainGameController;
        Circle clip = new Circle(36);
        clip.setStroke(Color.valueOf("#9CEAEF"));
        clip.setStrokeWidth(3);
        clip.setFill(Color.rgb(0, 0, 0, 0.7));
        otherPlayerInfoRegion.getChildren().add(clip);
        clip.toBack();
    }

    public void sendMessage() {
        String text = textInput.getText();
        main.getMessageHandler().sendMessage(text);
    }

    public void setOtherPlayerAvatar(Image avatar){
        otherPlayerAvatar.setImage(avatar);
    }

    public void setOtherPlayerNickname(String nickname){
        otherPlayerNickname.setText(nickname);
    }

}
