import bizingo.commons.Message;
import bizingo.commons.MessageType;

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

    private void forwardMessage(Message msg) {
        try {
            output.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendStartMessage() {
        Message msg = new Message(MessageType.TEXT.getValue(), "starting",
                server.getInetAddress().getHostAddress(), source.getInetAddress().getHostAddress());
        try {
            own.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Cliente iniciado com socket: " + source);
        //sendStartMessage();

        try {
            Message msg;
            while (true) {
                msg = (Message) input.readObject();
                if (msg != null) {
                    System.out.println("Mensagem recebida por " +
                            this.source.getInetAddress().getHostAddress() +
                            ": " + msg.getText());
                    forwardMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
