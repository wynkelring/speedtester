package com.company.Server;

import com.company.All.UDP_Multicast_Receiver;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Podaj port servera [numer]: ");
            int port = 6666; //scanner.nextInt();

            TCP_Server serverTCP = new TCP_Server(port);
            UDP_Server serverUDP = new UDP_Server(port);
            UDP_Multicast_Receiver multicastReceiver = new UDP_Multicast_Receiver("230.1.0.10", 7777);
            multicastReceiver.setServerPort(port);

            serverTCP.start();
            serverUDP.start();
            multicastReceiver.start();

            System.out.println("W celu zamknięcia połączenia wpisz 'stop'");
            while(true) {
                if (scanner.nextLine().equals("stop")) {
                    serverUDP.stopReceiving();
                    serverTCP.stopReceiving();
                    multicastReceiver.setServerPort(-1);
                    multicastReceiver.stopReceiving();
                    break;
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("[ERROR] Wprowadziłeś dane niezgodne z ich typem umieszczonym w nawiasie");
        }
    }
}
