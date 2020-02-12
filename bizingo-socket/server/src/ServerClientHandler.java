import bizingo.commons.Message;
import bizingo.commons.MessageType;
import bizingo.commons.TextMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClientHandler extends Thread {
    private Socket source, destination;
    private ObjectOutputStream own;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;

    public ServerClientHandler(Socket source, Socket destination, ServerSocket server) {
        try {
            this.source = source;
            this.destination = destination;
            this.server = server;
            output = new ObjectOutputStream(this.destination.getOutputStream());
            input = new ObjectInputStream(this.source.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }

    public void forwardMessage(Message msg) {
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSelfMessage(Message msg){
        try {
            own.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Cliente iniciado com socket: " + source);

        try {
            Message msg;
            while (true) {
                msg = (Message) input.readObject();
                if (msg != null) {
                    String source = msg.getContent().getSource();
                    MessageType type = msg.getType();
                    System.out.println("Mensagem do tipo " + type.getValue() + " recebida por " + source);
                    if(type == MessageType.TEXT){
                        TextMessage txtMsg = (TextMessage) msg.getContent();
                        System.out.println("Texto: " + txtMsg.getText());
                    }
                    forwardMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
