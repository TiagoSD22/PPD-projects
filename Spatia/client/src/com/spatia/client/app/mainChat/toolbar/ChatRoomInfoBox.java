package com.spatia.client.app.mainChat.toolbar;

import com.jfoenix.controls.JFXButton;
import com.spatia.common.ChatRoom;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class ChatRoomInfoBox extends GridPane {

    private Background unfocusedBackground;
    private Background focusedBackground;
    private ChatRoom room;
    private GridPane enterRoomPane;
    private ToolbarController toolbarController;

    ChatRoomInfoBox(ChatRoom room, ToolbarController toolbarController){
        this.toolbarController = toolbarController;

        this.setVgap(10);
        this.setHgap(5);

        unfocusedBackground = new Background(new BackgroundFill(Color.valueOf("#30343F"), null, null));
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

        this.room = room;

        Label roomNameText = new Label(room.getName());
        roomNameText.setTextFill(Color.valueOf("#ececec"));
        roomNameText.setPadding(new Insets(0, 10, 20, 10));
        roomNameText.setTranslateX(20);

        Label clientsConnectedInfoText = new Label(String.valueOf(room.getConnectedClientList().size()));
        clientsConnectedInfoText.setTextFill(Color.valueOf("#ececec"));
        ImageView clientIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/Images/people.png")));
        clientIcon.setFitWidth(22);
        clientIcon.setFitHeight(22);
        clientsConnectedInfoText.setGraphic(clientIcon);
        clientsConnectedInfoText.setPadding(new Insets(0, 10, 20, 10));

        enterRoomPane = new GridPane();
        enterRoomPane.setBackground(new Background(new BackgroundFill(Color.valueOf("#30343F"), null, null)));

        Text text = new Text("Entrar nesta sala?");
        text.setFill(Color.valueOf("#ececec"));

        JFXButton enterRoomBt = new JFXButton("Entrar");
        enterRoomBt.setButtonType(JFXButton.ButtonType.RAISED);
        enterRoomBt.setBackground(new Background(new BackgroundFill(Color.valueOf("#9FD3C7"), null, null)));
        enterRoomBt.setPadding(new Insets(5, 5, 5, 5));
        enterRoomBt.setTextFill(Color.valueOf("#040A10"));
        ImageView enterRoomIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/Images/enter.png")));
        enterRoomIcon.setFitWidth(18);
        enterRoomIcon.setFitHeight(18);
        enterRoomBt.setGraphic(enterRoomIcon);
        enterRoomBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hideEnterRoomPane();
                toolbarController.onEnterRoomConfirm(room);
            }
        });

        JFXButton cancelEnterRoomBt = new JFXButton("Cancelar");
        cancelEnterRoomBt.setButtonType(JFXButton.ButtonType.RAISED);
        cancelEnterRoomBt.setBackground(new Background(new BackgroundFill(Color.valueOf("#D69ABF"), null, null)));
        cancelEnterRoomBt.setPadding(new Insets(5, 5, 5, 5));
        cancelEnterRoomBt.setTextFill(Color.valueOf("#040A10"));
        ImageView cancelRoomIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/Images/cancel.png")));
        cancelRoomIcon.setFitWidth(18);
        cancelRoomIcon.setFitHeight(18);
        cancelEnterRoomBt.setGraphic(cancelRoomIcon);
        cancelEnterRoomBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hideEnterRoomPane();
            }
        });

        enterRoomPane.setAlignment(Pos.CENTER_RIGHT);
        enterRoomPane.setHgap(10);
        enterRoomPane.setPadding(new Insets(10, 5, 10, 5));

        enterRoomPane.add(text, 0, 0, 1, 1);
        enterRoomPane.add(enterRoomBt, 1, 0, 1, 1);
        enterRoomPane.add(cancelEnterRoomBt, 2, 0, 1, 1);
        enterRoomPane.setVisible(false);

        this.add(roomNameText, 0, 0, 1, 1);
        this.add(clientsConnectedInfoText, 1, 0, 1, 1);

        this.setPadding(new Insets(20, 0, 0, 0));
    }

    private void showEnterRoomPane(){
        enterRoomPane.setVisible(true);
        this.add(enterRoomPane, 0, 1, 2, 1);
    }

    private void hideEnterRoomPane(){
        enterRoomPane.setVisible(false);
        this.getChildren().removeIf(node -> getRowIndex(node) == 1);
    }

    void setFocus(boolean isFocused){
        if(isFocused){
            this.setBackground(focusedBackground);
            showEnterRoomPane();
        }
        else{
            this.setBackground(unfocusedBackground);
            hideEnterRoomPane();
        }
    }

    public String getRoomName() {
        return room.getName();
    }
}
