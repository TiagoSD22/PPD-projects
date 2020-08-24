package com.spatia.server;

public class Application {
    private static final String serverSpaceName = "spatiaSpace";

    public static void main(String[] args) {
        Server server = new Server(serverSpaceName);
        server.run();
    }
}
