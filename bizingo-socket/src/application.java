import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class application {

    private static ServerSocket initServer(){
        try {
            ServerSocket server = new ServerSocket(5432);
            return server;
        } catch (IOException e) {
            System.out.println("Falha ao iniciar socket");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]){
        int number_of_connections = 0;
        ServerSocket server = initServer();
        ArrayList<Socket> clients = new ArrayList<>();
        if(server != null) {
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

        }
    }
}
