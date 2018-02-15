package com.mosegames.chatterbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Host {
    private static class Client implements Runnable {
        public volatile Thread thread = new Thread(this, "Client");
        public volatile boolean occupy = false;
        public volatile Socket client;
        public volatile BufferedReader in;
        public volatile PrintStream out;

        public Client(Socket socket) {
            client = socket;
            thread.start();
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintStream(client.getOutputStream());
                connect(this, in, out);
                in.close();
                out.println( "quiting server" );
                if ( Host.active ) {
                    connections.remove(this);
                }
            } catch (IOException e) { }
        }
    }

    private static class Main implements Runnable {
        @Override
        public void run() {
            while ( true ) {
                try {
                    connections.add( new Client( socket.accept() ) );
                } catch (IOException ex) {
                    Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public volatile static boolean active = false;
    public static String ip;
    public static int port;

    private volatile static ArrayList<String> log;
    private static Thread mainLoop;
    private static ServerSocket socket;
    private static final ArrayList<Client> connections = new ArrayList<Client>();

    public static void main(String[] args) throws IOException {
        active = true;
        log = new ArrayList<>();
        socket = new ServerSocket(0);
        ip = Info.getLocalIp();
        port = socket.getLocalPort();
        mainLoop = new Thread(new Main(), "MainLoop");
        mainLoop.start();
    }

    public static void close() {
        if (!active) { return; }

        if (connections.size() >= 2) {
            connections.get(1).occupy = true;
            connections.get(1).out.println("\\host");
            try {
                connections.get(1).in.readLine();
            } catch (IOException e) { }
        }

        connections.forEach((client) -> {
            client.out.println("\\reconnect");
        });

        mainLoop.interrupt();

        active = false;

        connections.forEach((client) -> {
            try {
                client.thread.join();
            } catch (InterruptedException e) { }
        });

        try {
            socket.close();
        } catch (IOException e) { }
    }

    public static void run() {
        System.out.println("==~ Your hostring the server.");
        try {
            main( new String[0] );
        } catch (IOException e) { }
    }

    private static void connect(Client client, BufferedReader in, PrintStream out) throws IOException {
        int i = 10000;
        while ( !in.ready() ) {
            i--;
            if (i < 0) { return; }
        }
        String status = in.readLine();

        int pos = 0;
        if ( status.equals("n") ) {
            pos = Math.max(0, log.size() - 10);
            out.println( "==~ Welcome to ChatterBox!" );
        } else if ( status.equals("o") ) {
            pos = log.size();
        } else if ( !status.equals("a") ) {
            return;
        }

        while ( active ) {
            if ( !client.occupy && in.ready() ) {
                String inpt = in.readLine();
                if (inpt.equals("\\quit")) { break; }
                log.add( inpt );
            }
            if ( log.size() > pos ) {
                out.println( log.get(pos) );
                pos++;
            }
        }
    }
}