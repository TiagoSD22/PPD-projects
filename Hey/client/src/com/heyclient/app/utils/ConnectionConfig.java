package com.heyclient.app.utils;

public class ConnectionConfig {
    private static final String SOCKET_HOST = "2.tcp.ngrok.io"; // "127.0.0.1";
    private static final String BROKER_HOST = "1.tcp.ngrok.io"; // "127.0.0.1";
    private static final int SOCKET_PORT = 15580;  //5000;
    private static final int MESSAGE_BROKER_PORT = 19561; //5672;

    public static String getSocketHost() {
        return SOCKET_HOST;
    }

    public static String getBrokerHost(){ return BROKER_HOST;}

    public static int getSocketPort() {
        return SOCKET_PORT;
    }

    public static int getMessageBrokerPort() {
        return MESSAGE_BROKER_PORT;
    }
}
