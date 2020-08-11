package com.hey.common;

import java.util.Date;

public class ChatMessage extends MessageContent {
    private Client sender;
    private Client receiver;
    private String text;
    private Date creationDate;

    public ChatMessage(Client sender, Client receiver, String text){
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        creationDate = new Date();
    }

    public Client getSender() {
        return sender;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }

    public Client getReceiver() {
        return receiver;
    }

    public void setReceiver(Client receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
