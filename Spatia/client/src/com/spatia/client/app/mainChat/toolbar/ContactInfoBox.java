package com.spatia.client.app.mainChat.toolbar;

import com.spatia.common.TypingStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class ContactInfoBox extends GridPane {
    private ImageView avatar;
    private Text userName;
    private Label userTypingStatus;
    private Label unreadMsgCounter;
    private StackPane unreadMsgPane;
    private String lastMessage = "";

    ContactInfoBox(Image avatar, String userName, boolean isRoom){
        this.setVgap(10);
        this.setHgap(5);
        this.setBackground(new Background(new BackgroundFill(Color.valueOf("30343F"),
                null, null)));
        this.setMinWidth(282);
        this.setMinHeight(74);
        this.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHgrow(Priority.NEVER);

        this.getColumnConstraints().add(constraints);

        for (int i = 0; i < 5; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / 5);
            this.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < 2; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / 3);
            this.getRowConstraints().add(rowConst);
        }

        this.avatar = new ImageView(avatar);
        this.avatar.setFitWidth(60);
        this.avatar.setFitHeight(60);

        this.userTypingStatus = new Label();

        this.userName = new Text(isRoom? "Sala " + userName: userName);
        this.userName.setTranslateY(0);
        this.userName.setFill(Color.valueOf("#fcfcfc"));
        this.userTypingStatus.setTextFill(Color.valueOf("#fcfcfc"));
        this.userTypingStatus.setMaxWidth(220);
        this.userTypingStatus.setWrapText(false);

        unreadMsgPane = new StackPane();
        unreadMsgCounter = new Label();
        unreadMsgCounter.setStyle("-fx-text-fill: #fcfcfc");

        Circle badge = new Circle(10, Color.valueOf("#A74482"));
        badge.setStrokeWidth(2.0);
        badge.setStyle("-fx-background-insets: 0 0 -1 0, 0, 1, 2;");
        badge.setSmooth(true);
        unreadMsgPane.getChildren().addAll(badge, unreadMsgCounter);
        unreadMsgPane.setAlignment(Pos.CENTER);
        unreadMsgPane.setTranslateX(30);
        unreadMsgPane.setTranslateY(-10);
        unreadMsgPane.setVisible(false);

        this.add(this.avatar, 0, 0, 1, 2);
        this.add(this.userName, 1, 0, 3, 1);
        this.add(this.userTypingStatus, 1, 1, 3, 1);
        this.add(unreadMsgPane, 4, 0, 1, 1);

        this.setPadding(new Insets(5, 0, 5, 5));
    }

    public void setAvatar(Image avatar){
        this.avatar.setImage(avatar);
    }

    public void setFocus(boolean focus){
        if(!focus){
            this.setBackground(new Background(new BackgroundFill(Color.valueOf("#30343F"),
                    null, null)));
        }
        else{
            this.setBackground(new Background(new BackgroundFill(Color.valueOf("#385170"),
                    null, null)));
        }
    }

    public void registerUnreadMsg(int qtd){
        if(qtd == 0){
            unreadMsgPane.setVisible(false);
        }
        else{
            unreadMsgPane.setVisible(true);
            unreadMsgCounter.setText(String.valueOf(qtd));
        }
    }

    public String getUserName(){
        return userName.getText();
    }

    public void setUserTypingStatus(TypingStatus typingStatus){
        if(typingStatus.equals(TypingStatus.TYPING)){
            this.userTypingStatus.setText("Digitando...");
            this.userTypingStatus.setTextFill(Color.valueOf("#9FD3C7"));
        }
        else{
            this.userTypingStatus.setTextFill(Color.valueOf("#fcfcfc"));
            this.userTypingStatus.setText(this.lastMessage);
        }
    }

    public void registerLastMessage(String msg){
        lastMessage = msg;
        if(!userTypingStatus.getText().equals("Digitando...")){
            this.userTypingStatus.setText(lastMessage);
            this.userTypingStatus.setTooltip(new Tooltip(lastMessage));
        }
    }

    public void registerRoomLastMessage(String text){
        lastMessage = text;
        this.userTypingStatus.setText(text);
        this.userTypingStatus.setTooltip(new Tooltip(lastMessage));
    }
}
