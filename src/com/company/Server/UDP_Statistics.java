package com.company.Server;

import java.util.Arrays;

public class UDP_Statistics extends Thread {
    private boolean isSending = true;
    private final int messageSize;
    private long receivedBytes = 0;

    public UDP_Statistics(int messageSize) {
        this.messageSize = messageSize;
    }

    public void run() {
        try {
            sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        do {
            final byte[] message = new byte[this.messageSize];
            Arrays.fill(message, (byte) 1);
            receivedBytes += messageSize;
            try {
                sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (isSending);
    }

    public Double getProprietyInPercent(long bytesToCompare){
        return (double) bytesToCompare/receivedBytes * 100;
    }


    public void stopThread() {
        this.isSending = false;
    }
}
