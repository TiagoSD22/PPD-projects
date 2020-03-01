package com.bizingo.server;

import bizingo.commons.*;
import com.bizingoclient.app.services.ClientStubInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class ServerStub implements ServerStubInterface {
    protected ArrayList<ClientStubInterface> clients;
    private int restartSolicitation = 0;
    private int clientReady = 0;

    public ServerStub() throws RemoteException {
        super();
        clients = new ArrayList<>();
    }

    @Override
    public void registerClient(ClientStubInterface client) throws RemoteException {
        System.out.println("Registrando novo cliente: " + client.getNickname());
        clients.add(client);
        if (clients.size() == 2) {
            System.out.println("2 jogadores conectados");
            startGame();
        }
    }

    @Override
    public void removeClient(ClientStubInterface client) throws RemoteException {
        clients.remove(client);
    }

    private void forwardMessage(ClientStubInterface from, Message msg) {
        int index = clients.indexOf(from);
        try {
            clients.get(1 - index).receiveMessage(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startGame(){
        System.out.println("Iniciando partida");
        Message msg = new Message(MessageType.START, null);
        try {
            clients.get(0).receiveMessage(msg);
            clients.get(1).receiveMessage(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onClientReady(){
        clientReady++;
        if(clientReady == 2){
            clientReady = 0;
            definePlayersColorsAndFirstToPlay();
        }
    }

    @Override
    public void handleClientMessage(ClientStubInterface client, Message msg) throws RemoteException {
        MessageType type = msg.getType();
        System.out.println("Mensagem do tipo " + type.getValue() + " enviada por " + client.getNickname());
        switch (type) {
            case QUIT:
                System.out.println("Mensagem de desistencia recebida do cliente " + client.getNickname());
                forwardMessage(client, msg);
                break;
            case TEXT:
                TextMessage txtMsg = (TextMessage) msg.getContent();
                System.out.println("Texto: " + txtMsg.getText());
                forwardMessage(client, msg);
                break;
            case RESTART:
                System.out.println("Mensagem de solicitacao de reinicio de partida recebida pelo cliente "
                        + client.getNickname());
                forwardMessage(client, msg);
                restartGame();
                break;
            case HANDSHAKE:
                onClientReady();
                forwardMessage(client, msg);
                break;
            case MOVEMENT:
            case TYPING_STATUS:
                forwardMessage(client, msg);
                break;
            default:
                break;
        }
    }

    private void restartGame() {
        restartSolicitation++;
        if (restartSolicitation == 2) {
            definePlayersColorsAndFirstToPlay();
            restartSolicitation = 0;
        }
    }

    private void definePlayersColorsAndFirstToPlay() {
        System.out.println("Definindo cores dos jogadores e primeiro a jogar");

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

        Message client1MessageConfig = new Message(MessageType.CONFIG, client1GameConfig);
        Message client2MessageConfig = new Message(MessageType.CONFIG, client2GameConfig);

        try {
            System.out.println("Configuracao do jogador 1(" + clients.get(0).getNickname() + "):" +
                            "\nCor de Peca: " + client1GameConfig.getPlayerPieceColor().getValue() +
                            "Configuracao do jogador 2(" + clients.get(1).getNickname() + "):" +
                            "\nCor de Peca: " + client2GameConfig.getPlayerPieceColor().getValue() +
                            "\nPrimeiro a jogar: " + (
                            playerToStart == 0 ? clients.get(0).getNickname() : clients.get(1).getNickname()
                    )
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            this.clients.get(0).receiveMessage(client1MessageConfig);
            this.clients.get(1).receiveMessage(client2MessageConfig);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
