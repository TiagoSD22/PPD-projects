import bizingo.commons.CellColor;
import bizingo.commons.GameConfig;
import bizingo.commons.Message;
import bizingo.commons.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server {

    private static final int SERVER_PORT = 5005;
    private static final int MAX_CLIENTS_CONNECTED = 2;
    private ArrayList<ServerClientHandler> clients;
    private ServerSocket server;
    private int restartSolicitation;

    public Server(){};

    private void initServer() {
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Falha ao iniciar socket");
            e.printStackTrace();
        }
    }

    public void restartGame(){
        restartSolicitation++;
        if(restartSolicitation == 2){
            definePlayersColorsAndFirstToPlay();
            restartSolicitation = 0;
        }
    }

    private void definePlayersColorsAndFirstToPlay() {
        Random random = new Random();
        int playerToStart = random.nextBoolean() ? 0 : 1;
        int playerToBeDark = random.nextBoolean() ? 0 : 1;

        GameConfig client1GameConfig = new GameConfig();
        GameConfig client2GameConfig = new GameConfig();

        if (playerToStart == 0) {
            client1GameConfig.setFirstTurn(true);
            client2GameConfig.setFirstTurn(false);
        } else {
            client1GameConfig.setFirstTurn(false);
            client2GameConfig.setFirstTurn(true);
        }

        if (playerToBeDark == 0) {
            client1GameConfig.setPlayerPieceColor(CellColor.DARK);
            client2GameConfig.setPlayerPieceColor(CellColor.LIGHT);
        } else {
            client1GameConfig.setPlayerPieceColor(CellColor.LIGHT);
            client2GameConfig.setPlayerPieceColor(CellColor.DARK);
        }

        Message client1Message = new Message(MessageType.CONFIG, client1GameConfig);
        Message client2Message = new Message(MessageType.CONFIG, client2GameConfig);

        this.clients.get(0).forwardMessage(client2Message);
        this.clients.get(1).forwardMessage(client1Message);
    }

    public void run() {
        int numberOfConnections = 0;
        restartSolicitation = 0;
        initServer();
        clients = new ArrayList<>();
        ArrayList<Socket> clientsSocket = new ArrayList<>();

        if (server != null) {
            System.out.println("Server socket iniciado com sucesso.");
            while (true) {
                while (numberOfConnections != MAX_CLIENTS_CONNECTED) {
                    System.out.println("Aguardando conexao!");
                    try {
                        Socket clientSocket = server.accept();
                        System.out.println("Conexao recebida em: " + clientSocket.getInetAddress().getHostAddress());
                        numberOfConnections++;
                        clientsSocket.add(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("Par de clientes conectados, iniciando jogo.");
                ServerClientHandler sch1 = new ServerClientHandler(clientsSocket.get(0), clientsSocket.get(1), this);
                ServerClientHandler sch2 = new ServerClientHandler(clientsSocket.get(1), clientsSocket.get(0), this);

                clients.add(sch1);
                clients.add(sch2);

                definePlayersColorsAndFirstToPlay();

                while (sch1.running && sch2.running){}

                numberOfConnections = 0;
                restartSolicitation = 0;
                clients.clear();
                clientsSocket.clear();
            }
        }
    }
}

