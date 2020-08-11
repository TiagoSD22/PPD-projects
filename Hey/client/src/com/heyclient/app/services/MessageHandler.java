package com.heyclient.app.services;

import com.hey.common.*;
import com.heyclient.app.mainChat.MainChatController;
import com.heyclient.app.menu.MenuController;
import com.heyclient.app.utils.ConnectionConfig;
import com.rabbitmq.client.*;
import javafx.application.Platform;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

public class MessageHandler {
    private Socket clientSocket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private String clientQueueName;
    private Channel clientBrokerChannel;
    private Consumer clientConsumer;
    private MenuController menuController;
    private MainChatController mainChatController;

    public MessageHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
        try {
            clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            clientInput = new ObjectInputStream(clientSocket.getInputStream());

            listenSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainChatController(MainChatController mainChatController){
        this.mainChatController = mainChatController;
    }

    public void connectToMessageBroker(String queueName){
        this.clientQueueName = queueName;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ConnectionConfig.getHOST());
        try {
            Connection connection = factory.newConnection();
            clientBrokerChannel = connection.createChannel();
            clientBrokerChannel.queueDeclare(this.clientQueueName, false, false, false, null);

            clientConsumer = new DefaultConsumer(clientBrokerChannel) {

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] data)
                        throws IOException {

                    Message msg =    SerializationUtils.deserialize(data);

                    ChatMessage chatMsg = (ChatMessage) msg.getContent();

                    System.out.println("Mensagem recebida na fila do broker: " +
                            "\nTipo: " + msg.getType() + "\nRemetente: " + chatMsg.getSender().getName() +
                            "\nTexto: " + chatMsg.getText() + "\nEnviada em: " + chatMsg.getCreationDate());
                }
            };

            observeQueue();
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToBroker(Client receiver, String text){
        try {
            System.out.println("Enviando mensagem " + text + " para usuario " + receiver.getName());

            ChatMessage chatMsg = new ChatMessage(mainChatController.getCurrentClient(), receiver, text);
            Message msg = new Message(MessageType.CHAT, chatMsg);

            byte[] data = SerializationUtils.serialize(msg);

            clientBrokerChannel.basicPublish("", receiver.getName(), null, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMenuController(MenuController menuController){
        this.menuController = menuController;
    }

    private void listenSocket(){
        new Thread(() -> {
            while (true){
                try{
                    Message msg = (Message) clientInput.readObject();
                    if(msg != null){
                        MessageType type = msg.getType();
                        switch (type){
                            case CONNECTION_ACCEPTANCE:
                                ConnectionAcceptance response = (ConnectionAcceptance) msg.getContent();
                                System.out.println("Resposta de solicitacao de conexao recebida do servidor" +
                                        "\nStatus: " + (response.isAccepted()? "Aceita" : "Recusada") +
                                        "\nInfo: " + response.getMsg());

                                Platform.runLater(() -> {
                                    menuController.onConnectionSolicitationResponse(response.isAccepted());
                                });
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void observeQueue(){
        new Thread(() -> {
            while(true){
                try {
                    clientBrokerChannel.basicConsume(clientQueueName, true, clientConsumer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendMessageThroughSocket(Message msg){
        try {
            clientOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToQueue(String queueDestinationName){

    }

    public void sendConnectionSolicitation(String userName, String avatarImageName){
        Message msg = new Message();

        Handshake handshake = new Handshake(userName, avatarImageName);

        msg.setType(MessageType.CONNECTION_SOLICITATION);
        msg.setContent(handshake);

        sendMessageThroughSocket(msg);
    }
}
