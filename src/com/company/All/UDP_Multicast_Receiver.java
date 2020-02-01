package com.company.All;

import java.io.IOException;
import java.net.*;

public class UDP_Multicast_Receiver extends Thread {
    private boolean isReceiving = true;

    private MulticastSocket multicastSocket;

    private String group;
    private int port;

    private static int serverPort = -1;

    public UDP_Multicast_Receiver(String group, int port){
        this.group = group;
        this.port = port;
    }

    public void run() {
        try {
            this.multicastSocket = new MulticastSocket(this.port);
            this.multicastSocket.setReuseAddress(true);
            this.multicastSocket.setSoTimeout(1000);
            this.multicastSocket.joinGroup(InetAddress.getByName(this.group));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buf = new byte[20];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

        while (this.isReceiving) {
            try {
                try {
                    this.multicastSocket.receive(datagramPacket);
                } catch (SocketException e) {
                    break;
                }
                String message = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());

                if(message.equals("DISCOVER")) {
                    UDP_Multicast_Sender multicastSender = new UDP_Multicast_Sender(this.group, this.port, "OFFER:" + serverPort);
                    multicastSender.start();
                }
                if(message.contains("OFFER:")) {
                    serverPort = Integer.parseInt(message.replaceAll("[\\D]", ""));

                    if(serverPort != -1) {
                        System.out.println("### Aktualnie dostępny server jest pod portem: " + serverPort);
                    }

                }
            } catch (IOException e) {
                 if (!e.getMessage().equalsIgnoreCase("Receive timed out")) {
                    e.printStackTrace();
                 }
            }
        }
    }

    public void setServerPort(int port) {
        serverPort = port;
    }

    public void stopReceiving() {
        this.isReceiving = false;
        try {
            this.multicastSocket.leaveGroup(InetAddress.getByName(this.group));
            this.multicastSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
