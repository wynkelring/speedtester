package com.company.Client;

import com.company.All.UDP_Multicast_Receiver;
import com.company.All.UDP_Multicast_Sender;

import java.net.SocketException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        try {
            UDP_Multicast_Receiver multicastReceiver = new UDP_Multicast_Receiver("230.1.0.10", 7777);
            multicastReceiver.start();

            UDP_Multicast_Sender multicastSender = new UDP_Multicast_Sender("230.1.0.10", 7777, "DISCOVER");
            multicastSender.start();

            Scanner scanner = new Scanner(System.in);

            System.out.println("Wprowadź adres serwera [IPv4]: ");
            String ip = "localhost";// scanner.nextLine();

            System.out.println("Podaj port servera [numer]: ");
            int port = 6666; // scanner.nextInt();

            System.out.println("Wprowadź wielkość bufora danych [bajty]: ");
            int msgBufferSize = 1000; // scanner.nextInt();

            System.out.println("Czy używać algorytmu nagle'a [true/false]?");
            boolean nagle = true; // scanner.nextBoolean();

            TCP_Client tcpThread = new TCP_Client(ip, port, msgBufferSize, nagle);
            UDP_Client udpThread = new UDP_Client(ip, port, msgBufferSize);

            tcpThread.start();
            udpThread.start();

            System.out.println("W celu zamknięcia połączenia wpisz 'stop'");
            while(true) {
                if (scanner.nextLine().equals("stop")) {
                    tcpThread.stopThread();
                    udpThread.stopThread();
                    break;
                }
            }
        } catch (InputMismatchException | SocketException e) {
            System.out.println("[ERROR] Wprowadziłeś dane niezgodne z ich typem umieszczonym w nawiasie");
        }
    }
}
