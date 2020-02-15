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
    public boolean running = true;

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
            System.out.println("Cliente desconectado");
            e.printStackTrace();
        }
    }

    public void sendSelfMessage(Message msg) {
        try {
            own.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Cliente iniciado com socket: " + source);

        Message msg;
        while (running) {
            try {
                msg = (Message) input.readObject();
                if (msg != null) {
                    String source = msg.getContent().getSource();
                    MessageType type = msg.getType();
                    System.out.println("Mensagem do tipo " + type.getValue() + " recebida por " + source);
                    if(type == MessageType.QUIT){
                        System.out.println("Mensagem de desistencia recebida do cliente " +
                                this.source.getInetAddress().getHostAddress());
                        running = false;
                    }
                    else if (type == MessageType.TEXT) {
                        TextMessage txtMsg = (TextMessage) msg.getContent();
                        System.out.println("Texto: " + txtMsg.getText());
                    }
                    forwardMessage(msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Cliente " + source.getInetAddress().getHostAddress() + " desconectado. " +
                        "Notificando desistencia ao outro cliente");
                Message giveup = new Message(MessageType.QUIT, null);
                forwardMessage(giveup);
                running = false;
                e.printStackTrace();
            }
        }
    }
}
