import bizingo.commons.CellColor;
import bizingo.commons.GameConfig;
import bizingo.commons.Message;
import bizingo.commons.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Application {

    private static final int SERVER_PORT = 5005;
    private static final int MAX_CLIENTS_CONNECTED = 2;

    private static ServerSocket initServer() {
        try {
            ServerSocket server = new ServerSocket(SERVER_PORT);
            return server;
        } catch (IOException e) {
            System.out.println("Falha ao iniciar socket");
            e.printStackTrace();
            return null;
        }
    }

    private static void definePlayersColorsAndFirstToPlay(ServerClientHandler sch1, ServerClientHandler sch2) {
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

        sch1.forwardMessage(client2Message);
        sch2.forwardMessage(client1Message);
    }

    public static void main(String args[]) {
        int numberOfConnections = 0;
        ServerSocket server = initServer();
        ArrayList<Socket> clients = new ArrayList<>();

        if (server != null) {
            while (true) {
                while (numberOfConnections != MAX_CLIENTS_CONNECTED) {
                    System.out.println("Aguardando conexao!");
                    try {
                        Socket clientSocket = server.accept();
                        System.out.println("Conexao recebida em: " + clientSocket.getInetAddress().getHostAddress());
                        numberOfConnections++;
                        clients.add(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("Par de clientes conectados, iniciando jogo.");
                ServerClientHandler sch1 = new ServerClientHandler(clients.get(0), clients.get(1), server);
                ServerClientHandler sch2 = new ServerClientHandler(clients.get(1), clients.get(0), server);

                definePlayersColorsAndFirstToPlay(sch1, sch2);

                while (sch1.running && sch2.running){}

                numberOfConnections = 0;
                clients.clear();
            }
        }
    }
}

