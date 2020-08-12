package com.heyclient.app.mainChat.toolbar;


import com.hey.common.Client;
import com.heyclient.app.mainChat.MainChatController;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;


public class ToolbarController {
    @FXML
    public AnchorPane root;
    @FXML
    private ImageView currentUserAvatar;
    @FXML
    private Text currentUserName;
    @FXML
    private StackPane currentUserInfoRegion;

    private MainChatController mainChatController;

    public void init(MainChatController mainChatController) {
        this.mainChatController = mainChatController;

        Circle clip = new Circle(36);
        clip.setStroke(Color.valueOf("#087e8b"));
        clip.setStrokeWidth(3);
        clip.setFill(Color.valueOf("#2f2f2f"));
        clip.setTranslateX(-110);
        currentUserInfoRegion.getChildren().add(clip);
        clip.toBack();

        Client currentClient = mainChatController.getCurrentClient();

        Image avatar = new Image(getClass().getResourceAsStream("/assets/Images/avatars/" +
                currentClient.getAvatarName()));

        setCurrentUserAvatar(avatar);
        setCurrentUserName(currentClient.getName());
    }

    private void setCurrentUserName(String name) {
        currentUserName.setText(name);
    }

    private void setCurrentUserAvatar(Image avatar) {
        currentUserAvatar.setImage(avatar);
    }


}