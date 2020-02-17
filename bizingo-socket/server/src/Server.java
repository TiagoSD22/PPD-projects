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
    private ArrayList<Socket> clientsSocket;
    private ServerSocket server;
    private int restartSolicitation;
    private int numberOfConnections;
    private boolean running;

    public Server() {
    }

    ;

    private void initServer() {
        try {
            server = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Falha ao iniciar socket");
            e.printStackTrace();
        }
    }

    public void restartGame() {
        restartSolicitation++;
        if (restartSolicitation == 2) {
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

    public void waitConnections() {
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
            running = false;
        }
    }

    public void clientDisconnected(){
        System.out.println("Partida encerrada, voltando a receber novas conexoes");
        running = false;
        numberOfConnections = 0;
        restartSolicitation = 0;
        clients.clear();
        clientsSocket.clear();
    }

    public void run() {
        numberOfConnections = 0;
        restartSolicitation = 0;
        initServer();
        clients = new ArrayList<>();
        clientsSocket = new ArrayList<>();
        running = false;

        if (server != null) {

            System.out.println("Server socket iniciado com sucesso.");
            while (true) {
                System.out.println("Em laco principal");
                waitConnections();

                if (!running) {
                    running = true;

                    System.out.println("Par de clientes conectados, iniciando jogo.");
                    ServerClientHandler sch1 = new ServerClientHandler(clientsSocket.get(0), clientsSocket.get(1), this);
                    ServerClientHandler sch2 = new ServerClientHandler(clientsSocket.get(1), clientsSocket.get(0), this);

                    clients.add(sch1);
                    clients.add(sch2);

                    definePlayersColorsAndFirstToPlay();
                }
            }
        }

    }
}

