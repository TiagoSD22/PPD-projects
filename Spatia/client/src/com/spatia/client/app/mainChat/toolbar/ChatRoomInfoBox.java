package com.spatia.client.app.mainChat.toolbar;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

class ChatRoomInfoBox extends GridPane {

    private Background unfocusedBackground;
    private Background focusedBackground;

    public ChatRoomInfoBox(String roomName, int clientsConnected){

        this.setVgap(10);
        this.setHgap(5);

        unfocusedBackground = new Background(new BackgroundFill(Color.valueOf("#040A10"), null, null));
        focusedBackground = new Background(new BackgroundFill(Color.valueOf("#3C5E7C"), null, null));

        this.setBackground(unfocusedBackground);
        this.setMinWidth(282);
        this.setMinHeight(40);
        this.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(80);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20);
        col2.setHalignment(HPos.RIGHT);

        this.getColumnConstraints().addAll(col1, col2);

        Text roomNameText = new Text(roomName);
        roomNameText.setFill(Color.valueOf("#ececec"));
        roomNameText.setTranslateX(20);

        Label clientsConnectedInfoText = new Label(String.valueOf(clientsConnected));
        clientsConnectedInfoText.setTextFill(Color.valueOf("#ececec"));
        ImageView clientIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/Images/people.png")));
        clientIcon.setFitWidth(22);
        clientIcon.setFitHeight(22);
        clientsConnectedInfoText.setGraphic(clientIcon);
        clientsConnectedInfoText.setPadding(new Insets(0, 10, 0, 10));

        this.add(roomNameText, 0, 0, 1, 1);
        this.add(clientsConnectedInfoText, 1, 0, 1, 1);

        this.setPadding(new Insets(20, 0, 20, 0));
    }

    public void setFocus(boolean isFocused){
        if(isFocused){
            this.setBackground(focusedBackground);
        }
        else{
            this.setBackground(unfocusedBackground);
        }
    }
}
