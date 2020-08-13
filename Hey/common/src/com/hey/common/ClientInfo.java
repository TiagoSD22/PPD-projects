package com.hey.common;

public class ClientInfo extends MessageContent {
    private Client client;

    public ClientInfo(Client client){
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
