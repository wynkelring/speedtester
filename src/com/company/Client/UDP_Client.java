package com.company.Client;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDP_Client extends Thread {
    private DatagramSocket datagramSocket = new DatagramSocket();

    private boolean isSending = true;

    private InetAddress ipAddress;
    private final int portNumber;
    private final int messageSize;

    public UDP_Client(String ip, int port, int msgSize) throws SocketException {
        try {
            this.ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.portNumber = port;
        this.messageSize = msgSize;
    }


    public void sendMessage(String messageType) {
        DatagramPacket datagramPacket = null;
        if(messageType.equals("first")) {
            final byte[] message = ("SIZE:" + this.messageSize).getBytes();
            datagramPacket = new DatagramPacket(message, message.length, this.ipAddress, this.portNumber);
        }
        if(messageType.equals("normal")) {
            final byte[] message = new byte[this.messageSize];
            Arrays.fill(message, (byte) 1);
            datagramPacket = new DatagramPacket(message, message.length, this.ipAddress, this.portNumber);
            try {
                sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(messageType.equals("fine")) {
            final byte[] message = ("FINE").getBytes();
            datagramPacket = new DatagramPacket(message, message.length, this.ipAddress, this.portNumber);
        }
        try {
            assert datagramPacket != null;
            this.datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        this.isSending = false;
    }

    public void run() {
        System.out.println("=========== UDP CLIENT THREAD START ===========");
        sendMessage("first");
        while (this.isSending) {
            sendMessage("normal");
        }
        sendMessage("fine");
        System.out.println("=========== UDP CLIENT THREAD END ===========");
    }
}
