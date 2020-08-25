package com.spatia.client.app.services;

import com.j_spaces.core.client.FinderException;
import com.spatia.client.app.menu.MenuController;
import com.spatia.client.app.utils.ConnectionConfig;
import com.spatia.common.ConnectionSolicitation;
import com.spatia.common.ConnectionSolicitationResponse;
import javafx.application.Platform;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.CannotFindSpaceException;
import org.openspaces.core.space.SpaceProxyConfigurer;

public class SpaceHandler {
    private static SpaceHandler instance;
    private GigaSpace applicationSpace;

    private MenuController menuController;

    private SpaceHandler(){
        try {
            applicationSpace = new GigaSpaceConfigurer(new SpaceProxyConfigurer(ConnectionConfig.getSpaceName()))
                    .gigaSpace();
        } catch (CannotFindSpaceException e){
            System.out.println("Nao foi possivel encontrar o espaco " + ConnectionConfig.getSpaceName() + "." +
                    "\nCertifique-se de que o espaco foi criado e tente novamente.");

            System.exit(-1);
        }
    }

    public static SpaceHandler getInstance() {
        if(instance == null){
            instance = new SpaceHandler();
        }
        return instance;
    }

    public void setMenuController(MenuController menuController){
        this.menuController = menuController;
    }

    public synchronized void startConnectionSolicitationResponseListener(){
        System.out.println("Iniciando listener para observar resposta de solicitacao de conexao");

        new Thread(() -> {
            ConnectionSolicitationResponse template = new ConnectionSolicitationResponse();

            boolean responseReceived = false;
            while (!responseReceived) {
                template.setUserName(menuController.getUserName());

                ConnectionSolicitationResponse res = applicationSpace.take(template);
                if (res != null) {
                    onConnectionSolicitationResponseReceived(res);
                    responseReceived = true;
                }
            }
        }).start();
    }

    public void writeConnectionSolicitation(String userName, String avatarName){
        ConnectionSolicitation solicitation = new ConnectionSolicitation(userName, avatarName);
        applicationSpace.write(solicitation);
    }

    private void onConnectionSolicitationResponseReceived(ConnectionSolicitationResponse response){
        Platform.runLater(() -> {
            menuController.onConnectionSolicitationResponseReceived(response.getIsAccepted());
        });
    }
}
