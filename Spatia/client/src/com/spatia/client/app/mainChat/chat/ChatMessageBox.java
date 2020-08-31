package com.spatia.client.app.mainChat.chat;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextFlow;

class ChatMessageBox extends HBox {
    private TextFlow textArea;
    private Text text;
    private String sender;
    private boolean isMyOwn;
    private SVGPath bubbleIndicator;

    ChatMessageBox(String text, boolean isMyOwn, String senderName, boolean showSender, boolean drawBubbleIndicator){
        textArea = new TextFlow();
        textArea.setLineSpacing(5);
        textArea.setPadding(new Insets(10, 15, 10, 10));
        textArea.setMaxWidth(500);
        textArea.setMinHeight(20);

        this.text = new Text(text);
        this.text.setWrappingWidth(500);
        this.text.setFill(Color.valueOf("#000000"));
        this.text.setBoundsType(TextBoundsType.VISUAL);

        this.isMyOwn = isMyOwn;
        this.sender = senderName;

        if(showSender){
            Text sender = new Text(senderName + "\n");
            sender.setFont(new Font(10));
            sender.setFill(Color.valueOf("#000000"));
            sender.setStyle("-fx-font-weight: bold");
            sender.setBoundsType(TextBoundsType.VISUAL);
            this.textArea.getChildren().add(sender);
        }
        this.textArea.getChildren().add(this.text);

        this.getChildren().add(textArea);

        String ownBoxPadding = "5 5 5 5";
        String incomingBoxPadding = "5 5 5 5";

        if(drawBubbleIndicator){
            bubbleIndicator = new SVGPath();
            this.getChildren().add(bubbleIndicator);

            if(isMyOwn) {
                bubbleIndicator.setContent("M10 0 L0 10 L0 0 Z");
                bubbleIndicator.setFill(Color.valueOf("#9FD3C7"));
                ownBoxPadding = " 5 0 5 5";
            }
            else{
                bubbleIndicator.setContent("M0 0 L10 0 L10 10 Z");
                bubbleIndicator.setFill(Color.valueOf("#ececec"));
                bubbleIndicator.toBack();
                incomingBoxPadding = "0 5 5 5";
            }
        }

        if(isMyOwn){
            this.setAlignment(Pos.TOP_RIGHT);
            textArea.setStyle("-fx-background-radius:" + ownBoxPadding + "; -fx-background-color: #9FD3C7");
        }
        else{
            this.setAlignment(Pos.TOP_LEFT);
            textArea.setStyle("-fx-background-radius:" + incomingBoxPadding + "; -fx-background-color: #ececec");
        }
    }

    public String getSender() {
        return sender;
    }

    public boolean isMyOwn() {
        return isMyOwn;
    }
}
