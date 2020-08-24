package com.hey.server;

import com.hey.common.Client;
import com.hey.common.Status;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

class Server {
    private static final int SERVER_PORT = 5000;
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private HashMap<ClientConnectionHandler, Future> clientHandlerTaskMap;
    private List<Client> applicationClientList;
    private BidiMap<Client, ClientConnectionHandler> clientHandlerMap;

    Server(){};

    private void initServer() {
        try {
            System.out.println("Iniciando servidor na porta " + SERVER_PORT);
            serverSocket = new ServerSocket(SERVER_PORT);
            pool = Executors.newFixedThreadPool(10);
            clientHandlerTaskMap = new HashMap<>();
            clientHandlerMap = new DualHashBidiMap<>();
            applicationClientList = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Falha ao iniciar servidor");
            e.printStackTrace();
        }
    }

    void run(){
        initServer();

        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Aguardando conexoes");
                    Socket client = serverSocket.accept();
                    System.out.println("Nova conexao recebida: " + client.getInetAddress().getHostAddress());
                    ClientConnectionHandler cch = new ClientConnectionHandler(this, client);
                    Future task = pool.submit(cch);

                    clientHandlerTaskMap.put(cch, task);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void stopClientHandler(ClientConnectionHandler clientConnectionHandler){
        Client c = clientHandlerMap.getKey(clientConnectionHandler);

        System.out.println("Parando tarefa para o socket do usuario " + c.getName());

        Future clientHandlerTask = clientHandlerTaskMap.get(clientConnectionHandler);
        clientHandlerTask.cancel(true);

        System.out.println("Tarefa para o cliente " + c.getName() + " finalizada com sucesso!");

        c.setStatus(Status.OFFLINE);
        c.setLastSeen(new Date());

        clientHandlerTaskMap.remove(clientConnectionHandler);

        notifyClientStatusUpdateToAll(c);
    }

    private void notifyClientStatusUpdateToAll(Client clientUpdated){
        List<Client> clientList = applicationClientList.stream().filter(c ->
                !c.getName().equals(clientUpdated.getName()) && c.getStatus().equals(Status.ONLINE)
        ).collect(Collectors.toList());

        for(Client c: clientList){
            ClientConnectionHandler cch = clientHandlerMap.get(c);
            cch.sendClientStatusUpdateMsg(clientUpdated.getName(), clientUpdated.getStatus(),
                    clientUpdated.getLastSeen()
            );
        }
    }

    private void notifyClientAvatarUpdateToAll(Client clientUpdated){
        List<Client> clientList = applicationClientList.stream().filter(c ->
                !c.getName().equals(clientUpdated.getName()) && c.getStatus().equals(Status.ONLINE)
        ).collect(Collectors.toList());

        for(Client c: clientList){
            ClientConnectionHandler cch = clientHandlerMap.get(c);
            cch.sendClientAvatarUpdate(clientUpdated.getName(), clientUpdated.getAvatarName());
        }
    }

    private void notifyNewClientToAll(Client newClient){
        List<Client> clientList = applicationClientList.stream().filter(c ->
                !c.getName().equals(newClient.getName()) && c.getStatus().equals(Status.ONLINE)
        ).collect(Collectors.toList());

        for(Client c: clientList){
            ClientConnectionHandler cch = clientHandlerMap.get(c);
            cch.sendNewClientConnectedMsg(newClient);
        }
    }

    void onConnectionSolicitation(String userName, String avatarImageName, ClientConnectionHandler cch){
        Client c = applicationClientList.stream().filter(client -> client.getName().equals(userName))
                .findFirst().orElse(null);

        boolean isAccepted;
        String text;

        if(c != null && c.getStatus().equals(Status.ONLINE)){ // ja existe cliente conectado ao servidor com esse nome
            System.out.println("Nome de usuario ja esta sendo usado");
            isAccepted = false;
            text = "Nome de usuario ja esta sendo usado!";
        }
        else{
            if(c != null){ // usuario conhecido da aplicacao que estava offline e voltou a se conectar
                System.out.println("Usuario " + c.getName() + " se reconectou ao servidor");
                c.setStatus(Status.ONLINE);
                if(!c.getAvatarName().equals(avatarImageName)){ // perceber alteracao de avatar e comunicar demais contatos
                    c.setAvatarName(avatarImageName);
                    notifyClientAvatarUpdateToAll(c);
                }
                clientHandlerMap.put(c, cch);

                notifyClientStatusUpdateToAll(c);
            }
            else{ // primeiro usuario com esse nome se conectando ao servidor
                System.out.println("Novo usuario " + userName + " se registrando ao servidor");
                Client newClient = new Client();

                newClient.setName(userName);
                newClient.setAvatarName(avatarImageName);
                newClient.setStatus(Status.ONLINE);

                applicationClientList.add(newClient);
                clientHandlerMap.put(newClient, cch);

                notifyNewClientToAll(newClient);
            }

            isAccepted = true;
            text = "OK";
        }

        cch.sendConnectionSolicitationResponse(isAccepted, text);
    }

    public void getContacts(ClientConnectionHandler cch){
        Client requestingClient = clientHandlerMap.getKey(cch);

        System.out.println("Retornando contatos para usuario " + requestingClient.getName());

        List<Client> contactList = applicationClientList.stream()
                .filter(client -> !client.getName().equals(requestingClient.getName())).collect(Collectors.toList());

        cch.sendContactList(contactList);
    }
}
