import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Application {

    private static final int SERVER_PORT = 5002;

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

    public static void main(String args[]) {
        int number_of_connections = 0;
        ServerSocket server = initServer();
        ArrayList<Socket> clients = new ArrayList<>();
        if (server != null) {
            while (number_of_connections != 2) {
                System.out.println("Aguardando conexao!");
                try {
                    Socket clientSocket = server.accept();
                    System.out.println("Conexao recebida em: " + clientSocket.getInetAddress().getHostAddress());
                    number_of_connections++;
                    clients.add(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ServerClientHandler sch1 = new ServerClientHandler(clients.get(0), clients.get(1));
            ServerClientHandler sch2 = new ServerClientHandler(clients.get(1), clients.get(0));
            sch1.start();
            sch2.start();
        }
    }
}
