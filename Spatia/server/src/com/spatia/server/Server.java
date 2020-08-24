package com.spatia.server;

import com.spatia.common.ConnectionSolicitation;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.EmbeddedSpaceConfigurer;
import org.openspaces.events.polling.SimplePollingContainerConfigurer;
import org.openspaces.events.polling.SimplePollingEventListenerContainer;

class Server {
    private GigaSpace applicationSpace;
    private SimplePollingEventListenerContainer  connectionSolicitationListener;

    Server(String spaceName){
        System.out.println("Iniciando aplicacao do servidor, criando espaco de nome " + spaceName);
        try {
            applicationSpace = new GigaSpaceConfigurer(new EmbeddedSpaceConfigurer(spaceName)).gigaSpace();
        }
        catch (Exception e){
            System.out.println("Falha ao criar espaco " + spaceName + ".\n");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Espaco " + spaceName + " criado com sucesso");
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

    void onConnectionSolicitationReceived(ConnectionSolicitation solicitation){
        System.out.println("Solicitacao de conexao recebida.\nUsuario: " + solicitation.getUserName());
    }
}
