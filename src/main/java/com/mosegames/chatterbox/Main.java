package com.mosegames.chatterbox;

import java.io.BufferedReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try (BufferedReader user = Client.input) {
            System.out.println("Please enter your user name.");
            
            while (true) { if (user.ready()) { break; } }
            String name = user.readLine();

            Client.inFunc = (String s) -> { return name + ": " + s; };
            Client.enterText = "==~ " + name + " has joined the chat.";
            Client.exitText = "==~ " + name + " has left the chat.";

            run();
            
            Host.close();
        }
    }

    public static void host() {
        Host.run();
        Client.ip = Host.ip;
        Client.port = Host.port;
        Info.request("http://www.mosegames.com/school.php", "PUT", Info.getWebIp() + "\n" + Host.ip + "\n" + Host.port);
        connect();
    }

    public static void run() {
        String[] data = Info.request("http://www.mosegames.com/school.php", "GET", Info.getWebIp() ).split("[\\r\\n]+");
        Client.ip = data[0];
        Client.port = Integer.parseInt( data[1] );

        if ( !Info.portOpen(Client.ip, Client.port) ) {
            host();
        } else {
            connect();
        }
    }
    
    public static void close() {
        if ( Client.socket != null && Client.socket.isConnected() ) {
            Client.out.println("\\quit");
            try {
                Client.in.close();
                Client.out.close();
                Client.socket.close();
            } catch (IOException e) {}
        }
        Host.close();
    }

    public static void connect() {
        if ( Client.socket != null && Client.socket.isConnected() ) {
            Client.out.println("\\quit");
            try {
                Client.in.close();
                Client.out.close();
                Client.socket.close();
            } catch (IOException e) { }
        }
        Client.run();
    }
}