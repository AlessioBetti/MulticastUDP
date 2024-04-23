/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package multicastudp;

import static multicastudp.MulticastClientUDP.GREEN_UNDERLINED;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.*;

/**
 *
 * @author Alessio Betti
 */
public class MulticastServerUDP {

    //colore del prompt del Server
    public static final String ANSI_BLUE = "\u001B[34m";

    //colore del prompt del Client
    public static final String RED_BOLD = "\033[1;31m";
    //colore reset
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {

        int port = 2000;//porta del Server
        DatagramSocket dSocket = null;//oggetto Socket UDP 
        DatagramPacket inPacket; //datagramma UDP ricevuto dal client
        DatagramPacket outPacket; //datagramma UDP di risposta da inviare
        byte[] inBuffer; //Buffer per il contenuto del segmento da ricevere       
        InetAddress groupAddress; //Indirizzo del gruppo Multicast UD 
        String messageIn; //messaggio ricevuto        
        String messageOut; //messaggio da inviare

        try {

            System.out.println(ANSI_BLUE + "SERVER UDP" + RESET);
            //1) SERVER IN ASCOLTO 
            //si crea il socket e si associa alla porta specifica
            dSocket = new DatagramSocket(port);
            System.out.println(GREEN_UNDERLINED + "Apertura porta in corso!" + RESET);
            System.out.println(GREEN_UNDERLINED + "Apertura porta Effettuata, Attendo connessioni!" + RESET);

            while (true) {
                //si prepara il buffer per il messaggio da ricevere
                inBuffer = new byte[256];

                //2) RICEZIONE MESSAGGIO DEL CLIENT
                //si crea un datagramma UDP in cui trasportare il buffer per tutta la sua lunghezza
                inPacket = new DatagramPacket(inBuffer, inBuffer.length);
                //attesa del pacchetto da parte del client
                dSocket.receive(inPacket);

                //si recupera l'indirizzo IP e la porta UDP del client
                InetAddress clientAddress = inPacket.getAddress();
                int clientPort = inPacket.getPort();

                //si stampa a video il messaggio ricevuto dal client
                messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println(RED_BOLD + "Messaggio ricevuto dal client " + clientAddress
                        + ":" + clientPort + "\n\t" + messageIn + RESET);

                //3)RISPOSTA AL CLIENT
                //si prepara il datagramma con i dati da inviare
                messageOut = "Ricevuta richiesta!";
                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), clientAddress, clientPort);

                //si inviano i dati
                dSocket.send(outPacket);
                System.out.println(ANSI_BLUE + "Spedito messaggio al client: " + messageOut + RESET);

                //4)INVIO MESSAGGIO AL GRUPPO DOPO UNA SOSPENSIONE 
                //si recupera l'IP gruppo
                groupAddress = InetAddress.getByName("239.255.255.250");
                //si inizializza la porta del gruppo
                int groupPort = 1900;

                //si prepara il datagramma con i dati da inviare al gruppo
                messageOut = "Benvenuti a tutti!";

                outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), groupAddress, groupPort);

                //si inviano i dati
                dSocket.send(outPacket);
                System.out.println(ANSI_BLUE + "Spedito messaggio al gruppo: " + messageOut + RESET);
            }
        } catch (BindException ex) {
            Logger.getLogger(MulticastServerUDP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Porta già in uso");
        } catch (SocketException ex) {
            Logger.getLogger(MulticastServerUDP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Errore di creazione del socket e apertura del server");
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastServerUDP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Errore di risoluzione");
        } catch (IOException ex) {
            Logger.getLogger(MulticastServerUDP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Errore di I/O");
        } finally {
            if (dSocket != null) {
                dSocket.close();
            }
        }
    }

}
