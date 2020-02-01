package com.company.Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class TCP_Client extends Thread {
    private Socket clientSocket;
    private DataOutputStream out;

    private boolean isSending = true;

    private String ipAddress;
    private int portNumber;
    private final int messageSize;
    private final boolean isNagle;

    public TCP_Client(String ip, final int port, final int msgSize, final boolean isNagle) {
        this.ipAddress = ip;
        this.portNumber = port;
        this.messageSize = msgSize;
        this.isNagle = isNagle;
    }

    public void startConnection() throws IOException {
        this.clientSocket = new Socket(this.ipAddress, this.portNumber);
        this.clientSocket.setTcpNoDelay(this.isNagle);

        this.out = new DataOutputStream(this.clientSocket.getOutputStream());
    }

    public void stopConnection() throws IOException {
        this.out.close();
        this.clientSocket.close();
    }

    public void sendMessage(boolean isFirst) throws IOException, InterruptedException {
        if(isFirst){
            final byte[] firstMsg = ("SIZE:" + this.messageSize).getBytes();
            this.out.write(firstMsg, 0, firstMsg.length);
            this.out.flush();
        } else {
            final byte[] message = new byte[this.messageSize];
            Arrays.fill(message, (byte) 1);

            this.out.write(message, 0, message.length);
            this.out.flush();

            sleep(200L);
        }
    }

    public void run() {
        try {
            System.out.println("=========== TCP CLIENT THREAD START ===========");
            startConnection();
            sendMessage(true);
            while (this.isSending) {
                sendMessage(false);
            }
            stopConnection();
            System.out.println("=========== TCP CLIENT THREAD END ===========");
        } catch (IOException | InterruptedException e) {
            System.out.println("[ERROR] Połączenie odrzucone. Sprawdź działanie servera i poprawność danych.");
        }
    }

    public void stopThread() {
        this.isSending = false;
    }
}
