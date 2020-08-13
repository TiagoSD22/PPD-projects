package com.hey.server;

import com.hey.common.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientConnectionHandler implements Runnable{
    private Socket clientSocket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private Server server;

    ClientConnectionHandler(Server server, Socket clientSocket){
        this.server = server;
        this.clientSocket = clientSocket;
        try {
            clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            clientInput = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        Message msg;
        boolean clientConnected = true;
        while(clientConnected) {
            try {
                msg = (Message) clientInput.readObject();
                if (msg != null) {
                    MessageType type = msg.getType();
                    MessageContent content = msg.getContent();
                    handleMessage(type, content);
                }
            } catch (EOFException | ClassNotFoundException e) {
                System.out.println("Cliente " + clientSocket.getInetAddress().getHostAddress() +
                        " desconectou, parando tarefa");
                clientConnected = false;
                server.stopClientHandler(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(MessageType type, MessageContent content){
        switch (type){
            case CONNECTION_SOLICITATION:
                Handshake handshake = (Handshake) content;
                System.out.println("Solicitacao de conexao para o servico de chat recebida do usuario: " +
                        handshake.getUserName());
                server.onConnectionSolicitation(handshake.getUserName(), handshake.getAvatarImageName(), this);
                break;
            case GET_CONTACTS:
                System.out.println("Mensagem de solicitacao de busca de contatos recebida");
                server.getContacts(this);
                break;
            default:
                break;
        }
    }

    void sendConnectionSolicitationResponse(boolean isAccepted, String text){
        System.out.println("Enviando resposta de solicitacao de conexao." +
                "\nSolicitacao: " + (isAccepted? "Aceita": "Rejeitada") + "\nInfo: " + text);
        ConnectionAcceptance response = new ConnectionAcceptance(isAccepted, text);

        Message msg = new Message(MessageType.CONNECTION_ACCEPTANCE, response);
        sendMessageThroughSocket(msg);
    }

    void sendContactList(List<Client> contactList){
        ContactList contactListMsg = new ContactList(contactList);
        Message msg = new Message(MessageType.CONTACT_LIST, contactListMsg);

        sendMessageThroughSocket(msg);
    }

    void sendClientStatusUpdateMsg(String clientName, Status newStatus, Date lastSeen){
        StatusUpdate statusUpdateMsg = new StatusUpdate(clientName, newStatus, lastSeen);

        Message msg = new Message(MessageType.STATUS_UPDATE, statusUpdateMsg);

        sendMessageThroughSocket(msg);
    }

    void sendClientAvatarUpdate(String clientName, String newAvatarName){
        AvatarUpdate avatarUpdateMsg = new AvatarUpdate(clientName, newAvatarName);

        Message msg = new Message(MessageType.AVATAR_UPDATE, avatarUpdateMsg);

        sendMessageThroughSocket(msg);
    }

    void sendNewClientConnectedMsg(Client newClient){
        ClientInfo clientInfo = new ClientInfo(newClient);

        Message msg = new Message(MessageType.NEW_CLIENT, clientInfo);

        sendMessageThroughSocket(msg);
    }

    private void sendMessageThroughSocket(Message msg){
        try {
            clientOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
