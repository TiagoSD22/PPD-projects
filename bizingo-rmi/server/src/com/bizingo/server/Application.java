package com.bizingo.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Application {

    public static void main(String args[]) {
        System.out.println("Iniciando aplicacao");
        try {
            ServerStubInterface server = new ServerStub();
            ServerStubInterface stub = (ServerStubInterface) UnicastRemoteObject.exportObject((ServerStubInterface) server, 0);
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.bind("Bizingo-RMI-Server", stub);
            System.out.println("Servidor iniciado com sucesso!");
        } catch (RemoteException e) {
            System.out.println("Falha ao iniciar servidor");
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
