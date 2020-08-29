package com.spatia.client.app.mainChat.chat;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextFlow;

class ChatMessageBox extends HBox {
    private TextFlow textArea;
    private Text text;

    ChatMessageBox(String text, boolean isMyOwn, String senderName, boolean showSender){
        textArea = new TextFlow();
        textArea.setLineSpacing(5);
        textArea.setPadding(new Insets(10, 15, 10, 10));
        textArea.setMaxWidth(500);
        textArea.setMinHeight(20);

        this.text = new Text(text);
        this.text.setWrappingWidth(500);
        this.text.setFill(Color.valueOf("#000000"));
        this.text.setBoundsType(TextBoundsType.VISUAL);

        if(showSender){
            Text sender = new Text(senderName + "\n");
            sender.setFont(new Font(10));
            sender.setFill(Color.valueOf("#000000"));
            sender.setStyle("-fx-font-weight: bold");
            sender.setBoundsType(TextBoundsType.VISUAL);
            this.textArea.getChildren().add(sender);
        }
        this.textArea.getChildren().add(this.text);

        this.setPadding(new Insets(10, 30, 10, 30));

        getChildren().addAll(textArea);

        if(isMyOwn){
            this.setAlignment(Pos.BASELINE_RIGHT);
            textArea.setBackground(new Background(new BackgroundFill(Color.valueOf("9FD3C7"),
                    null, null)));
        }
        else{
            this.setAlignment(Pos.BASELINE_LEFT);
            textArea.setBackground(new Background(new BackgroundFill(Color.valueOf("#ececec"),
                    null, null)));
        }
    }
}
