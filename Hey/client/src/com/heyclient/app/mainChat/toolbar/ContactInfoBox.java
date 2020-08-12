package com.heyclient.app.mainChat.toolbar;

import com.hey.common.Status;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class ContactInfoBox extends GridPane {
    private ImageView avatar;
    private Text userName;
    private Text userStatus;
    private Circle statusClip;
    private Circle statusIndicator;
    private Label unreadMsgCounter;
    private StackPane unreadMsgPane;

    ContactInfoBox(Image avatar, String userName, Status status){
        this.setVgap(10);
        this.setHgap(5);
        this.setBackground(new Background(new BackgroundFill(Color.valueOf("#292929"),
                null, null)));
        this.setMinWidth(282);
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
        this.avatar.setFitWidth(64);
        this.avatar.setFitHeight(64);

        this.statusClip = new Circle(32);
        this.statusIndicator = new Circle(7);
        this.userStatus = new Text();

        setUserStatus(status);
        this.statusClip.setStrokeWidth(2);
        this.statusClip.setFill(Color.valueOf("#2f2f2f"));

        this.statusIndicator.setStrokeWidth(1);
        this.statusIndicator.setTranslateX(50);
        this.statusIndicator.setTranslateY(10);

        this.userName = new Text(userName);
        this.userName.setTranslateY(15);
        this.userName.setFill(Color.valueOf("#fcfcfc"));
        this.userStatus.setFill(Color.valueOf("#fcfcfc"));

        unreadMsgPane = new StackPane();
        unreadMsgCounter = new Label();
        unreadMsgCounter.setStyle("-fx-text-fill: #fcfcfc");

        Circle badge = new Circle(10, Color.valueOf("#f7717d"));
        badge.setStrokeWidth(2.0);
        badge.setStyle("-fx-background-insets: 0 0 -1 0, 0, 1, 2;");
        badge.setSmooth(true);
        unreadMsgPane.getChildren().addAll(badge, unreadMsgCounter);
        unreadMsgPane.setAlignment(Pos.CENTER);
        unreadMsgPane.setTranslateX(-5);
        unreadMsgPane.setTranslateY(-10);
        unreadMsgPane.setVisible(false);

        this.add(this.statusClip, 0, 0, 1, 2);
        this.add(this.avatar, 0, 0, 1, 2);
        this.add(this.userName, 1, 0, 3, 1);
        this.add(this.statusIndicator, 0, 1, 1, 1);
        this.add(unreadMsgPane, 4, 0, 1, 1);
    }

    public void setAvatar(Image avatar){
        this.avatar.setImage(avatar);
    }

    public void setUserStatus(Status status){
        String statusColor = "";

        if(status.equals(Status.ONLINE)){
            statusColor = "#087e8b";
        }
        else{
            statusColor = "#707070";
        }

        this.statusClip.setStroke(Color.valueOf(statusColor));
        this.userStatus.setText(status.getValue());
        this.statusIndicator.setStroke(Color.valueOf(statusColor));
        this.statusIndicator.setFill(Color.valueOf(statusColor));
    }

    public void setFocus(boolean focus){
        if(!focus){
            this.setBackground(new Background(new BackgroundFill(Color.valueOf("#292929"),
                    null, null)));
        }
        else{
            this.setBackground(new Background(new BackgroundFill(Color.valueOf("#03203a"),
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
}
