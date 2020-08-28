package com.spatia.server;

import com.gigaspaces.client.ChangeSet;
import com.spatia.common.*;
import net.jini.core.lease.Lease;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.EmbeddedSpaceConfigurer;
import org.openspaces.events.notify.SimpleNotifyContainerConfigurer;
import org.openspaces.events.notify.SimpleNotifyEventListenerContainer;
import org.openspaces.events.polling.SimplePollingContainerConfigurer;
import org.openspaces.events.polling.SimplePollingEventListenerContainer;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

class Server {
    private final int TIME_MINUTES_TO_REMOVE_EMPTY_ROOM = 10;
    private GigaSpace applicationSpace;
    private List<Client> applicationClientList;
    private ChatRoomRegister chatRoomRegister;
    private SimplePollingEventListenerContainer connectionSolicitationListener;
    private SimplePollingEventListenerContainer closeConnectionSolicitationListener;
    private SimpleNotifyEventListenerContainer chatRoomCreatedListener;
    private SimplePollingEventListenerContainer chatRoomInteractionListener;
    private Map<String, Timer> removeEmptyRoomTaskMap;

    Server(String spaceName){
        System.out.println("Iniciando aplicacao do servidor, criando espaco de nome " + spaceName);
        try {
            applicationSpace = new GigaSpaceConfigurer(new EmbeddedSpaceConfigurer(spaceName)).gigaSpace();
            applicationClientList = new ArrayList<>();

            System.out.println("Espaco " + spaceName + " criado com sucesso");

            applicationClientList = new ArrayList<>();
            removeEmptyRoomTaskMap = new HashMap<>();
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
        registerChatRoomInteractionListener();
        registerCloseConnectionSolicitationListener();

        connectionSolicitationListener.start();
        chatRoomCreatedListener.start();
        chatRoomInteractionListener.start();
        closeConnectionSolicitationListener.start();
    }

    private List<Client> getNotifiableRoomConnectedClientList(String roomName, Client c){
        return chatRoomRegister.getRegisteredRoomList().stream()
                .filter(room -> room.getName().equals(roomName)).findFirst().orElse(new ChatRoom())
                .getConnectedClientList().stream().
                        filter((client -> !client.getName().equals(c.getName())
                                && client.getStatus().equals(Status.ONLINE))).collect(Collectors.toList());
    }

    private void registerConnectionSolicitationListener(){
        connectionSolicitationListener = new SimplePollingContainerConfigurer(applicationSpace)
                .template(new ConnectionSolicitation())
                .eventListenerAnnotation(new ConnectionSolicitationListener(this))
                .pollingContainer();
    }

    private void registerCloseConnectionSolicitationListener(){
        closeConnectionSolicitationListener = new SimplePollingContainerConfigurer(applicationSpace)
                .template(new CloseConnectionSolicitation())
                .eventListenerAnnotation(new CloseConnectionSolicitationListener(this))
                .pollingContainer();
    }

    private void registerChatRoomCreatedListener(){
        chatRoomCreatedListener = new SimpleNotifyContainerConfigurer(applicationSpace)
                .template(new ChatRoom())
                .eventListenerAnnotation(new ChatRoomCreationListener(this))
                .notifyContainer();
    }

    private void registerChatRoomInteractionListener(){
        ChatRoomInteraction template = new ChatRoomInteraction();
        template.setNotify(false);
        chatRoomInteractionListener = new SimplePollingContainerConfigurer(applicationSpace)
                .template(template)
                .eventListenerAnnotation(new ChatRoomInteractionListener(this))
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

    private void updateChatRoomRegisterInSpace(){
        SortedSet<ChatRoom> list = chatRoomRegister.getRegisteredRoomList();

        applicationSpace.change(new ChatRoomRegister(), new ChangeSet().set("registeredRoomList", (Serializable) list));

    }

    void onChatRoomCreated(ChatRoom chatRoom){
        System.out.println("Nova sala criada.\nNome: " + chatRoom.getName());

        chatRoomRegister.getRegisteredRoomList().add(chatRoom);

        applicationSpace.change(new ChatRoomRegister(), new ChangeSet().addToCollection("registeredRoomList", chatRoom));

        System.out.println("Sala " + chatRoom.getName() + " adicionada ao registro de salas no espaco");
    }

    private void registerClientInRoom(String roomName, Client client){
        System.out.println("Registrando cliente " + client.getName() + " na sala " + roomName);

        ChatRoom room = chatRoomRegister.getRegisteredRoomList().stream()
                .filter(r -> r.getName().equals(roomName)).findFirst().orElse(null);

        assert room != null;
        if(room.getConnectedClientList() == null){
            room.setConnectedClientList(new TreeSet<>());
        }

        room.getConnectedClientList().add(client);

        ChatRoom template = new ChatRoom();
        template.setName(roomName);
        applicationSpace.change(template, new ChangeSet().addToCollection("connectedClientList", client));

        updateChatRoomRegisterInSpace();

        applicationSpace.write(new ChatRoomInteraction(InteractionType.ENTER, roomName, client, true), 60000);

        if(removeEmptyRoomTaskMap.containsKey(room.getName())){ // sala estava vazia e marcada para ser removida
            System.out.println("Cancelando tarefa de remocao de sala " + room.getName());

            Timer timer = removeEmptyRoomTaskMap.get(room.getName());
            timer.cancel();

            removeEmptyRoomTaskMap.remove(room.getName());
        }
    }

    private void removeClientFromRoom(String roomName, Client client){
        System.out.println("Removendo cliente " + client.getName() + " da sala " + roomName);

        ChatRoom room = chatRoomRegister.getRegisteredRoomList().stream()
                .filter(r -> r.getName().equals(roomName)).findFirst().orElse(null);

        assert room != null;
        room.getConnectedClientList().removeIf(c -> c.getName().equals(client.getName()));

        ChatRoom template = new ChatRoom();
        template.setName(roomName);
        applicationSpace.change(template, new ChangeSet().removeFromCollection("connectedClientList", client));

        updateChatRoomRegisterInSpace();

        applicationSpace.write(new ChatRoomInteraction(InteractionType.LEAVE, roomName, client, true), 60000);

        if(room.getConnectedClientList().size() == 0){
            System.out.println("Sala " + room.getName() + " ficou vazia e sera removida em 10min.");

            Timer timer = new Timer();
            timer.schedule(new RemoveEmptyRoomTask(this, room.getName()),
                    TIME_MINUTES_TO_REMOVE_EMPTY_ROOM * 60 * 1000);

            removeEmptyRoomTaskMap.put(room.getName(), timer);
        }
    }

    void removeRoom(String roomName){
        System.out.println("Removendo sala " + roomName + " por inatividade apos"
                + TIME_MINUTES_TO_REMOVE_EMPTY_ROOM + " min");

        ChatRoom template = new ChatRoom();
        template.setName(roomName);

        applicationSpace.take(template); // removendo sala do espaco

        ChatRoom room = chatRoomRegister.getRegisteredRoomList().stream()
                .filter(r -> r.getName().equals(roomName)).findFirst().orElse(null);

        chatRoomRegister.getRegisteredRoomList().remove(room);

        updateChatRoomRegisterInSpace();
    }

    void onChatRoomInteractionCreated(ChatRoomInteraction interactionData){
        System.out.println("Nova interacao de usuario com sala reconhecida no espaco." +
                "\nUsuario: " + interactionData.getClient().getName() +
                "\nSala: " + interactionData.getRoomName() +
                "\nInteracao do tipo: " + (interactionData.getType().equals(InteractionType.ENTER)? "Entrar": "Sair"));

        InteractionType type = interactionData.getType();

        String roomName = interactionData.getRoomName();
        Client client = interactionData.getClient();

        if(type.equals(InteractionType.ENTER)){ // usuario solicitou entrar em uma sala
            registerClientInRoom(roomName, client);
        }
        else{ // usuario solicitou sair de uma sala
            removeClientFromRoom(roomName, client);
        }
    }

    void onCloseConnectionSolicitationReceived(CloseConnectionSolicitation solicitation){
        System.out.println("Cliente " + solicitation.getUserName() + " se desconectou");

        Client client = applicationClientList.stream().
                filter(c -> c.getName().equals(solicitation.getUserName()))
                .findFirst()
                .orElse(null);

        assert client != null;
        if(solicitation.getCurrentInRoomName() != null){
            removeClientFromRoom(solicitation.getCurrentInRoomName(), client);
        }

        client.setStatus(Status.OFFLINE);
    }
}
