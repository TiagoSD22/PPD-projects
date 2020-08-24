package com.heyclient.app.utils;

public class ConnectionConfig {
    private static final String REMOTE_SOCKET_HOST = "2.tcp.ngrok.io";
    private static final String REMOTE_BROKER_HOST = "1.tcp.ngrok.io";
    private static final int REMOTE_SOCKET_PORT = 15580;
    private static final int REMOTE_MESSAGE_BROKER_PORT = 19561;
    private static final String LOCAL_SOCKET_HOST = "127.0.0.1";
    private static final String LOCAL_BROKER_HOST = "127.0.0.1";
    private static final int LOCAL_SOCKET_PORT = 5000;
    private static final int LOCAL_MESSAGE_BROKER_PORT = 5672;

    private static boolean deployLocal = false;

    public static String getSocketHost() {
        return deployLocal? LOCAL_SOCKET_HOST: REMOTE_SOCKET_HOST;
    }

    public static String getBrokerHost(){
        return deployLocal? LOCAL_BROKER_HOST: REMOTE_BROKER_HOST;
    }

    public static int getSocketPort() {
        return deployLocal? LOCAL_SOCKET_PORT: REMOTE_SOCKET_PORT;
    }

    public static int getMessageBrokerPort() {
        return deployLocal? LOCAL_MESSAGE_BROKER_PORT: REMOTE_MESSAGE_BROKER_PORT;
    }
}
