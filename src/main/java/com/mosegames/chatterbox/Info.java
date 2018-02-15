package com.mosegames.chatterbox;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class Info {
    private static String webip = "";
    
    public static String getLocalIp() {
        try {
            InetAddress iAddress;
            iAddress = InetAddress.getLocalHost();
            return iAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    public static InetAddress getIPv4LocalNetMask(InetAddress ip, int netPrefix) {
        try {
            int shiftby = (1<<31);
            for (int i=netPrefix-1; i>0; i--) {
                shiftby = (shiftby >> 1);
            }
            String maskString = Integer.toString((shiftby >> 24) & 255) + "." + Integer.toString((shiftby >> 16) & 255) + "." + Integer.toString((shiftby >> 8) & 255) + "." + Integer.toString(shiftby & 255);
            return InetAddress.getByName(maskString);
        } catch(UnknownHostException e) {
            return null;
        }
    }

    public static String getWebIp() {
        if (webip.length() == 0) {
            try {
                URL whatismyip;
                whatismyip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = new BufferedReader(new InputStreamReader( whatismyip.openStream()));
                InetAddress web_ip = InetAddress.getByName( in.readLine() );
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress( Inet4Address.getLocalHost() );
                web_ip = getIPv4LocalNetMask( web_ip , networkInterface.getInterfaceAddresses().get(1).getNetworkPrefixLength() );
                webip = web_ip.getHostAddress();
            } catch (IOException e) { }
        }
        return webip;
    }

    public static boolean portOpen(String host, int port) {
        try {
            Socket s = new Socket();
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(host, port);
            s.connect(sa, 3000);
            s.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String request(String targetURL, String method, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            try ( DataOutputStream wr = new DataOutputStream (connection.getOutputStream())) {
                wr.writeBytes(urlParameters);
            }

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}