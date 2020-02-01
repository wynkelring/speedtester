package com.company.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCP_Server extends Thread {
    private boolean isAvailable = true;

    private ServerSocket serverSocket;

    private int port;

    private long receivedBytes = 0;
    private long stopTimeMs = 0;

    public TCP_Server(int port) {
        this.port = port;
    }

    public void run() {
        System.out.println("=========== TCP SERVER THREAD START ===========");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isAvailable) {
            System.out.println("Oczekiwanie na klientów ...");
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket.getRemoteSocketAddress());

                final DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                byte[] firstMessage = new byte[15];
                int bytes = in.read(firstMessage);
                String firstMsg = new String(firstMessage, 0, bytes);

                int bufferSize = Integer.parseInt(firstMsg.replaceAll("[\\D]", ""));

                byte[] message = new byte[bufferSize];

                long startTimeMs = System.currentTimeMillis();
                boolean isStopTimeSet = false;

                while (isAvailable) {
                    try {
                        bytes = in.read(message);
                        if (bytes < 0) {
                            this.stopTimeMs = System.currentTimeMillis();
                            isStopTimeSet = true;
                            break;
                        }
                        this.receivedBytes += bufferSize;
                    } catch (SocketException e) {
                        System.out.println("[ERROR] Użytkownik TCP nieoczekiwanie rozłączył się z serverem");
                        try {
                            this.serverSocket.close();
                        } catch (IOException | NullPointerException ignored) {
                        }
                        isStopTimeSet = true;
                        this.stopTimeMs = System.currentTimeMillis();
                        break;
                    }
                }
                if(!isStopTimeSet){
                    this.stopTimeMs = System.currentTimeMillis();
                }
                System.out.println("Wątek (TCP): odebrano " + receivedBytes/1024.0 + "kb danych w czasie " + (stopTimeMs - startTimeMs)/1000.0 + "s z prędkością " + (receivedBytes/1024.0)/((stopTimeMs - startTimeMs)/1000.0) + "kb/sec");
                this.receivedBytes = 0;
            } catch (IOException e) {
                System.out.println("=========== TCP SOCKET CLOSED ===========");
            }
        }
        System.out.println("=========== TCP SERVER THREAD END ===========");
    }

    public void stopReceiving() {
        this.isAvailable = false;
        try {
            this.serverSocket.close();
        } catch (IOException | NullPointerException ignored) {
        }
    }
}
