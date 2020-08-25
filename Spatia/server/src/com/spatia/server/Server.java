package com.spatia.server;

import com.spatia.common.Client;
import com.spatia.common.ConnectionSolicitation;
import com.spatia.common.ConnectionSolicitationResponse;
import com.spatia.common.Status;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.EmbeddedSpaceConfigurer;
import org.openspaces.events.polling.SimplePollingContainerConfigurer;
import org.openspaces.events.polling.SimplePollingEventListenerContainer;

import java.util.ArrayList;
import java.util.List;

class Server {
    private GigaSpace applicationSpace;
    private List<Client> applicationClientList;
    private SimplePollingEventListenerContainer  connectionSolicitationListener;

    Server(String spaceName){
        System.out.println("Iniciando aplicacao do servidor, criando espaco de nome " + spaceName);
        try {
            applicationSpace = new GigaSpaceConfigurer(new EmbeddedSpaceConfigurer(spaceName)).gigaSpace();
            applicationClientList = new ArrayList<>();

            System.out.println("Espaco " + spaceName + " criado com sucesso");
        }
        catch (Exception e){
            System.out.println("Falha ao criar espaco " + spaceName + ".\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    void run(){
        registerConnectionSolicitationListener();
        connectionSolicitationListener.start();
    }

    private void registerConnectionSolicitationListener(){
        connectionSolicitationListener  = new SimplePollingContainerConfigurer(applicationSpace)
                .template(new ConnectionSolicitation())
                .eventListenerAnnotation(new ConnectionSolicitationListener(this))
                .pollingContainer();
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
}
