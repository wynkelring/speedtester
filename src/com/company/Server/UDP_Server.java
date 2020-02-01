package com.company.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDP_Server extends Thread {
    private boolean isAvailable = true;

    private DatagramSocket datagramSocket;

    private int port;

    private long receivedBytes = 0;

    public UDP_Server(int port) {
        this.port = port;
    }

    public void run() {
        System.out.println("=========== TCP SERVER THREAD START ===========");
        try {
            datagramSocket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        int bufferSize = 15;
        byte[] receive = new byte[bufferSize];
        long startTimeMs = 0;

        UDP_Statistics statistics = null;

        while (isAvailable) {
            DatagramPacket datagramPacket = new DatagramPacket(receive, receive.length);
            try {
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                System.out.println("=========== UDP SOCKET CLOSED ===========");
            }

            String message = new String(receive);

            if (message.contains("FINE")) {
                assert statistics != null;
                statistics.stopThread();

                long stopTimeMs = System.currentTimeMillis();
                System.out.println("Użytkownik się rozłączył: " + datagramSocket.getRemoteSocketAddress());
                System.out.println("Wątek (UDP): odebrano " + receivedBytes/1024.0 + "kb danych w czasie " + (stopTimeMs - startTimeMs)/1000.0 + "s z prędkością " + (receivedBytes/1024.0)/((stopTimeMs - startTimeMs)/1000.0) + "kb/sec");
                System.out.println("Wątek (UDP): poprawność otrzymanych danych: " + statistics.getProprietyInPercent(receivedBytes) + "%");
                this.receivedBytes = 0;
            } else if (message.contains("SIZE:")) {
                bufferSize = Integer.parseInt(message.replaceAll("[\\D]", ""));
                receive = new byte[bufferSize];
                startTimeMs = System.currentTimeMillis();
                this.receivedBytes = 0;
                statistics = new UDP_Statistics(bufferSize);
                statistics.start();
            } else {
                this.receivedBytes += bufferSize;
            }
        }
        System.out.println("=========== UDP SERVER THREAD END ===========");
    }

    public void stopReceiving() {
        this.isAvailable = false;
        this.datagramSocket.close();
    }
}