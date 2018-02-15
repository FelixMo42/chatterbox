package com.mosegames.chatterbox;

import java.io.*;
import java.net.*;

public class Client {
    interface func {
        String call(String s);
    }

    public static BufferedReader input = new BufferedReader( new InputStreamReader(System.in) );
    public static func inFunc = (String s) -> { return s; };
    public static func outFunc = (String s) -> { return s; };
    public static String enterText = "";
    public static String exitText = "";
    public static String status = "n";
    public static String ip = "10.137.41.82";
    public static int port = 6792;
    public static Socket socket;
    public static PrintStream out;
    public static BufferedReader in;

    public static void main(String[] args) throws IOException  {
        socket = new Socket(ip, port);
        out = new PrintStream(socket.getOutputStream());
        in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );

        out.println( status );
        if ( "n".equals(status) && !enterText.equals("") ) {
            out.println(enterText);
        }
        status = "o";

        while (true) {
            if ( input.ready() ) {
                String inpt = input.readLine();
                if ( inpt.startsWith("\\") ) {
                    if ( inpt.equals("\\quit") ) {
                        if ( !exitText.equals("") ) {
                            out.println(exitText);
                        }
                        out.println(inpt);
                        break;
                    }
                } else {
                    print( inpt );
                }
            }
            if ( in.ready() ) {
                String outpt = in.readLine();
                if ( outpt.startsWith("\\") ) {
                    if ( outpt.equals("\\host") ) {
                        Main.host();
                        return;
                    } else if ( outpt.equals("\\reconnect") ) {
                        if ( !Host.active ) {
                            Main.run();
                            return;
                        }
                    }
                } else {
                    System.out.println( outFunc.call( outpt ) );
                }
            }
        }

        in.close();
        out.close();
        socket.close();
    }

    public static void run() {
        try {
            main( new String[0] );
        } catch (IOException e) { }
    }

    public static void print(String msg) {
        out.println( inFunc.call(msg) );
    }
}