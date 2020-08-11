package com.heyclient.app.utils;

public class ConnectionConfig {
    private static final String HOST = "127.0.0.1";
    private static final int SOCKET_PORT = 5000;
    private static final int MESSAGE_BROKER_PORT = 5672;

    public static String getHOST() {
        return HOST;
    }

    public static int getSocketPort() {
        return SOCKET_PORT;
    }

    public static int getMessageBrokerPort() {
        return MESSAGE_BROKER_PORT;
    }
}
