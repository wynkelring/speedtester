package com.company.All;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class UDP_Multicast_Sender extends Thread {
    private String group;
    private int port;
    private String message;

    public UDP_Multicast_Sender(String group, int port, String message){
        this.group = group;
        this.port = port;
        this.message = message;
    }

    public void run() {
        sendMessage();
    }

    public void sendMessage() {
        try {
            MulticastSocket multicastSocket  = new MulticastSocket();
            byte[] buf = this.message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(group), port);
            multicastSocket.send(pack);
            multicastSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
