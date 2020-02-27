import bizingo.commons.Message;
import bizingo.commons.MessageType;
import bizingo.commons.TextMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerClientHandler extends Thread {
    public boolean running;
    private Socket source, destination;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Server server;
    private int id;

    public ServerClientHandler(Socket source, Socket destination, Server server, int id) {
        try {
            this.source = source;
            this.destination = destination;
            this.server = server;
            output = new ObjectOutputStream(this.destination.getOutputStream());
            input = new ObjectInputStream(this.source.getInputStream());
            this.id = id;
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

    public void sendStartMessage(){
        TextMessage txtMsg = new TextMessage("start", "server", destination.getInetAddress().getHostAddress());
        Message startMsg = new Message(MessageType.START, txtMsg);
        forwardMessage(startMsg);
    }

    public synchronized void run() {
        System.out.println("Cliente iniciado com socket: " + source);
        sendStartMessage();
        running = true;
        Message msg;
        while (running) {
            try {
                msg = (Message) input.readObject();
                if (msg != null) {
                    String source = msg.getContent().getSource();
                    MessageType type = msg.getType();
                    System.out.println("Mensagem do tipo " + type.getValue() + " recebida por " + source);
                    switch (type) {
                        case QUIT:
                            System.out.println("Mensagem de desistencia recebida do cliente " +
                                    this.source.getInetAddress().getHostAddress());
                            running = false;
                            forwardMessage(msg);
                            break;
                        case START:
                            System.out.println("Enviando mensagem de configuracao de jogo para o cliente " + id);
                            server.sendMessageConfig();
                            break;
                        case TEXT:
                            TextMessage txtMsg = (TextMessage) msg.getContent();
                            System.out.println("Texto: " + txtMsg.getText());
                            forwardMessage(msg);
                            break;
                        case RESTART:
                            System.out.println("Mensagem de solicitacao de reinicio de partida recebida pelo cliente "
                                    + source);
                            forwardMessage(msg);
                            server.restartGame();
                            break;
                        case HANDSHAKE:
                        case MOVEMENT:
                        case TYPING_STATUS:
                            forwardMessage(msg);
                            break;
                        case CLOSE:
                            System.out.println("Mensagem de close recebida, fechando conexao do cliente " + source);
                            running = false;
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Cliente " + source.getInetAddress().getHostAddress() + " desconectado. " +
                        "Notificando desistencia ao outro cliente");
                Message giveup = new Message(MessageType.QUIT, null);
                forwardMessage(giveup);
                running = false;
                server.clientDisconnected();
                e.printStackTrace();
            }
        }
    }
}
