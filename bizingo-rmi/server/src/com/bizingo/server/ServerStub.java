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

    public ServerStub() {
        super();
        clients = new ArrayList<>();
    }

    @Override
    public void registerClient(ClientStubInterface client) {
        if (clients.size() < 2) {
            try {
                System.out.println("Registrando novo cliente: " + client.getNickname());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            clients.add(client);
            if (clients.size() == 2) {
                System.out.println("2 jogadores conectados");
                startGame();
            }
        }
    }

    @Override
    public void onClientReady() {
        clientReady++;
        if (clientReady == 2) {
            clientReady = 0;
            definePlayersColorsAndFirstToPlay();
        }
    }

    @Override
    public void onClientQuit(ClientStubInterface client) {
        try {
            System.out.println("Mensagem de desistencia recebida do cliente " + client.getNickname());
            clients.get(1 - clients.indexOf(client)).otherPlayerDisconnected();
            System.out.println("Removendo cliente " + client.getNickname());
            clients.remove(client);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeClientConnection(ClientStubInterface client) {
        try {
            System.out.println("Removendo conexao do cliente " + client.getNickname());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        clients.remove(client);
    }

    @Override
    public void receiveClientTextMessage(ClientStubInterface client, String text) {
        System.out.println("Texto: " + text);
        try {
            clients.get(1 - clients.indexOf(client)).receiveTextMessage(text);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRestartSolicitation(ClientStubInterface client) {
        try {
            System.out.println("Mensagem de solicitacao de reinicio de partida recebida do cliente "
                    + client.getNickname());
            clients.get(1 - clients.indexOf(client)).onRestartSolicitation();
            restartGame();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRestartSolicitationDenied(ClientStubInterface client) {
        try {
            System.out.println("Mensagem de recuso de reinicio de partida recebida do cliente  " +
                    client.getNickname());
            clients.get(1 - clients.indexOf(client)).onRestartSolicitationDenied();
            restartSolicitation--;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveClientHandshake(ClientStubInterface client, Handshake handshake){
        System.out.println("Recebendo handshake de cliente.\nNickname: " + handshake.getNickname() +
                "\nAvatar: " + handshake.getAvatar());
        try {
            clients.get(1 -clients.indexOf(client)).receiveOtherPlayerHandshake(handshake);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClientMovePiece(ClientStubInterface client, PlayerMovement mov){
        try {
            System.out.println("Cliente " + client.getNickname() + " moveu peca. Movimento: \n" +
                    "De: " + mov.getCoordSource() + "\nPara: " + mov.getCoordDest());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            clients.get(1 -clients.indexOf(client)).movePiece(mov);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateClientTypingStatus(ClientStubInterface client, TypingStatus status){
        try {
            System.out.println("Atualizacao de status de digitacao do cliente " + client.getNickname() +
                    "Status atual: " + status.getValue());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            clients.get(1 -clients.indexOf(client)).updateTypingStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        System.out.println("Iniciando partida");
        restartSolicitation = 0;
        try {
            clients.get(0).startGame();
        } catch (RemoteException e) {
            try {
                System.out.println("Falha ao enviar mensagem de inicio de partida para o cliente " +
                        clients.get(0).getNickname());
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        try {
            clients.get(1).startGame();
        } catch (RemoteException e) {
            try {
                System.out.println("Falha ao enviar mensagem de inicio de partida para o cliente " +
                        clients.get(1).getNickname());
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
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

        try {
            System.out.println("Configuracao do jogador 1(" + clients.get(0).getNickname() + "):" +
                            "\nCor de Peca: " + client1GameConfig.getPlayerPieceColor().getValue() +
                            "\nConfiguracao do jogador 2(" + clients.get(1).getNickname() + "):" +
                            "\nCor de Peca: " + client2GameConfig.getPlayerPieceColor().getValue() +
                            "\nPrimeiro a jogar: " + (
                            playerToStart == 0 ? clients.get(0).getNickname() : clients.get(1).getNickname()
                    )
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            this.clients.get(0).receiveGameConfig(client1GameConfig);
            this.clients.get(1).receiveGameConfig(client2GameConfig);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
