package com.spatia.server;

import com.gigaspaces.client.ChangeSet;
import com.spatia.common.*;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.EmbeddedSpaceConfigurer;
import org.openspaces.events.notify.SimpleNotifyContainerConfigurer;
import org.openspaces.events.notify.SimpleNotifyEventListenerContainer;
import org.openspaces.events.polling.SimplePollingContainerConfigurer;
import org.openspaces.events.polling.SimplePollingEventListenerContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

class Server {
    private GigaSpace applicationSpace;
    private List<Client> applicationClientList;
    private ChatRoomRegister chatRoomRegister;
    private SimplePollingEventListenerContainer connectionSolicitationListener;
    private SimpleNotifyEventListenerContainer chatRoomCreatedListener;

    Server(String spaceName){
        System.out.println("Iniciando aplicacao do servidor, criando espaco de nome " + spaceName);
        try {
            applicationSpace = new GigaSpaceConfigurer(new EmbeddedSpaceConfigurer(spaceName)).gigaSpace();
            applicationClientList = new ArrayList<>();

            System.out.println("Espaco " + spaceName + " criado com sucesso");

            applicationClientList = new ArrayList<>();
            chatRoomRegister = new ChatRoomRegister();
            chatRoomRegister.setRegisteredRoomList(new TreeSet<>());

            applicationSpace.write(chatRoomRegister);
        }
        catch (Exception e){
            System.out.println("Falha ao criar espaco " + spaceName + ".\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    void run(){
        registerConnectionSolicitationListener();
        registerChatRoomCreatedListener();

        connectionSolicitationListener.start();
        chatRoomCreatedListener.start();
    }

    private void registerConnectionSolicitationListener(){
        connectionSolicitationListener  = new SimplePollingContainerConfigurer(applicationSpace)
                .template(new ConnectionSolicitation())
                .eventListenerAnnotation(new ConnectionSolicitationListener(this))
                .pollingContainer();
    }

    private void registerChatRoomCreatedListener(){
        chatRoomCreatedListener = new SimpleNotifyContainerConfigurer(applicationSpace)
                .template(new ChatRoom())
                .eventListenerAnnotation(new ChatRoomCreationListener(this))
                .notifyContainer();
    }

    private void sendConnectionSolicitationResponse(String userName, boolean isAccepted){
        System.out.println("Enviando resposta de solicitacao de conexao para usuario " + userName + "" +
                "\nResposta " + (isAccepted? "Aceito": "Negado"));

        ConnectionSolicitationResponse response = new ConnectionSolicitationResponse(userName, isAccepted);

        applicationSpace.write(response);
    }

    void onConnectionSolicitationReceived(ConnectionSolicitation solicitation){
        System.out.println("Solicitacao de conexao recebida." +
                "\nUsuario: " + solicitation.getUserName() +
                "\nAvatar: " + solicitation.getAvatarName());

        String userName = solicitation.getUserName();
        String avatarImageName = solicitation.getAvatarName();

        Client c = applicationClientList.stream().filter(client -> client.getName().equals(userName))
                .findFirst().orElse(null);

        boolean isAccepted;

        if(c != null && c.getStatus().equals(Status.ONLINE)){ // ja existe cliente conectado ao servidor com esse nome
            System.out.println("Nome de usuario ja esta sendo usado");
            isAccepted = false;
        }
        else{
            if(c != null){ // usuario conhecido da aplicacao que estava offline e voltou a se conectar
                System.out.println("Usuario " + c.getName() + " se reconectou ao servidor");
                c.setStatus(Status.ONLINE);
                if(!c.getAvatarName().equals(avatarImageName)){ // perceber alteracao de avatar e comunicar demais contatos
                    c.setAvatarName(avatarImageName);
                }
            }
            else{ // primeiro usuario com esse nome se conectando ao servidor
                System.out.println("Novo usuario " + userName + " se registrando ao servidor");
                Client newClient = new Client();

                newClient.setName(userName);
                newClient.setAvatarName(avatarImageName);
                newClient.setStatus(Status.ONLINE);

                applicationClientList.add(newClient);
            }

            isAccepted = true;
        }

        sendConnectionSolicitationResponse(userName, isAccepted);
    }

    void onChatRoomCreated(ChatRoom chatRoom){
        System.out.println("Nova sala criada.\nNome: " + chatRoom.getName());

        chatRoomRegister.getRegisteredRoomList().add(chatRoom);

        applicationSpace.change(new ChatRoomRegister(), new ChangeSet().addToCollection("registeredRoomList", chatRoom));

        System.out.println("Sala " + chatRoom.getName() + " adicionada ao registro de salas no espaco");

    }
}
