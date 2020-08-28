package com.spatia.client.app.services;

import com.gigaspaces.client.WriteModifiers;
import com.j_spaces.core.client.EntryAlreadyInSpaceException;
import com.spatia.client.app.mainChat.MainChatController;
import com.spatia.client.app.menu.MenuController;
import com.spatia.client.app.services.listeners.ChatRoomInteractionListener;
import com.spatia.client.app.utils.ConnectionConfig;
import com.spatia.common.*;
import javafx.application.Platform;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.CannotFindSpaceException;
import org.openspaces.core.space.SpaceProxyConfigurer;
import org.openspaces.events.notify.SimpleNotifyContainerConfigurer;
import org.openspaces.events.notify.SimpleNotifyEventListenerContainer;

import java.util.SortedSet;
import java.util.TreeSet;

public class SpaceHandler {
    private static SpaceHandler instance;
    private GigaSpace applicationSpace;
    private SimpleNotifyEventListenerContainer chatRoomInteractionListener;

    private MenuController menuController;
    private MainChatController mainChatController;

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

    public void startChatRoomInteractionListener(ChatRoom room){
        ChatRoomInteraction template = new ChatRoomInteraction();
        template.setRoomName(room.getName());
        template.setNotify(true);

        chatRoomInteractionListener = new SimpleNotifyContainerConfigurer(applicationSpace)
                .template(template)
                .eventListenerAnnotation(new ChatRoomInteractionListener())
                .notifyContainer();

        chatRoomInteractionListener.start();
    }

    public void stopChatRoomInteractionListener(){
        chatRoomInteractionListener.stop();
    }

    public void setMenuController(MenuController menuController){
        this.menuController = menuController;
    }

    public void setMainChatController(MainChatController mainChatController) {
        this.mainChatController = mainChatController;
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

    public void writeCloseConnectionSolicitation(String userName, String currentInRoomName){
        CloseConnectionSolicitation solicitation = new CloseConnectionSolicitation(userName, currentInRoomName);

        applicationSpace.write(solicitation);
    }

    public void writeChatRoom(String roomName) throws EntryAlreadyInSpaceException {
        System.out.println("Escrevendo sala de nome " + roomName + " no espaco");

        ChatRoom room = new ChatRoom(roomName, new TreeSet<>());

        try {
            applicationSpace.write(room, WriteModifiers.WRITE_ONLY);
            System.out.println("Sala criada" + room);
        }
        catch (Exception e){
            throw new EntryAlreadyInSpaceException("Erro", "Ja existe sala com o nome " + roomName + " no espaco");
        }
    }

    public SortedSet<ChatRoom> readChatRoomRegisteredList(){
        ChatRoomRegister register = applicationSpace.read(new ChatRoomRegister());
        return register.getRegisteredRoomList();
    }

    public void writeEnterRoomInteraction(Client client, String roomName){
        ChatRoomInteraction interaction = new ChatRoomInteraction(InteractionType.ENTER, roomName, client, false);

        writeChatRoomInteraction(interaction);
    }

    public void writeLeaveRoomInteraction(Client client, String roomName){
        ChatRoomInteraction interaction = new ChatRoomInteraction(InteractionType.LEAVE, roomName, client, false);

        writeChatRoomInteraction(interaction);
    }

    private void writeChatRoomInteraction(ChatRoomInteraction interaction){
        applicationSpace.write(interaction);
    }

    public void onChatRoomInteraction(ChatRoomInteraction interaction){
        if(!interaction.getClient().getName().equals(mainChatController.getCurrentClient().getName())) {
            if(interaction.getType().equals(InteractionType.ENTER)){
                System.out.println("Novo cliente " + interaction.getClient().getName() + " conectado a sala atual " +
                        interaction.getRoomName());
                mainChatController.getToolbarController().onNewClientEnteredRoom(interaction.getClient());
            }
            else{
                System.out.println("Cliente " + interaction.getClient().getName() + " se desconectou da sala atual " +
                        interaction.getRoomName());
                mainChatController.getToolbarController().onClientLeftRoom(interaction.getClient());
            }
        }
    }

    public ChatRoom readRoom(String roomName){
        ChatRoom template = new ChatRoom();
        template.setName(roomName);

        return applicationSpace.read(template);
    }
}
