/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package multicastudp;

import static multicastudp.MulticastServerUDP.ANSI_BLUE;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alessio Betti
 * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control
 */
public class MulticastClientUDP {

    //colore del prompt del Server
    public static final String ANSI_BLUE = "\u001B[34m";
    //colore del prompt del Client
    public static final String RED_BOLD = "\033[1;31m";
    //colore del prompt del gruppo
    public static final String GREEN_UNDERLINED = "\033[4;32m";
    //colore reset
    public static final String RESET = "\033[0m";

    public static void main(String[] args) {
        
        int port = 2000; //numero di porta server        
        int portGroup = 1900; //numero di porta del gruppo       
        InetAddress serverAddress; //indirizzo del server       
        DatagramSocket dSocket = null; //socket UDP       
        MulticastSocket mSocket = null; //socket multicast UDP       
        InetAddress group; //indirizzo gruppo multicast UDP       
        DatagramPacket outPacket;  //Datagramma UDP con la richiesta da inviare al server        
        DatagramPacket inPacket;//Datagramma UDP di risposta ricevuto dal server

        //si creano i buffer di lettura, uno per le comunicazioni del server e l'altro per i messaggi del gruppo
        byte[] inBuffer = new byte[256];
        byte[] inBufferG = new byte[1024];

        //messaggio di richiesta verso il server
        String messageOut = "Richiesta comunicazione";
        //messaggio di risposta dal server
        String messageIn;

        try {
            System.out.println(RED_BOLD + "CLIENT UDP" + RESET);
            //1) RICHIESTA AL SERVER
            //si recupera l'IP del server UDP attraverso il metodo getLoclaHost()
            serverAddress = InetAddress.getLocalHost();
            System.out.println(RED_BOLD + "Indirizzo del server trovato!" + RESET);

            //istanza del socket UDP per la prima comunicazione con il server
            dSocket = new DatagramSocket();

            /*si prepara il datagramma, costituito da:
                - messaggio
                - lunghezza del messaggio
                - indirizzo del server
                - porta del server
            */             
            outPacket = new DatagramPacket(messageOut.getBytes(), messageOut.length(), serverAddress, port);

            //si inviano i dati al server con il metodo send()
            dSocket.send(outPacket);
            System.out.println(RED_BOLD + "Richiesta al server inviata!" + RESET);

            //2) RISPOSTA DEL SERVER
            //si prepara il datagramma per ricevere dati dal server
            inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            dSocket.receive(inPacket);

            //lettura del messaggio ricevuto e sua visualizzazione 
            messageOut = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(ANSI_BLUE + "Lettura dei dati ricevuti dal server" + RESET);

            messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(ANSI_BLUE + "Messaggio ricevuto dal server " + serverAddress
                    + ":" + port + "\n\t" + messageIn + RESET);

            //3) RICEZIONE MESSAGGIO DEL GRUPPO
            //si istanzia l'oggetto MulticastSocket
            mSocket = new MulticastSocket(portGroup);
            
            //si ricava l'indirizzo del gruppo con il metodo getByName()
            group = InetAddress.getByName("239.255.255.250");
            
            //si effettua l'unione al gruppo con il metodo joinGroup()
            mSocket.joinGroup(group);

            /*si prepara il datagramma per ricevere dati dal gruppo,
              specificando il buffer dove inserire i dati e la sua lunghezza
            */
            inPacket = new DatagramPacket(inBufferG, inBufferG.length);
            mSocket.receive(inPacket);

            /*lettura del messaggio ricevuto, inizializzando una stringa con:
                - payload
                - l'indice del byte dal quale partire a decodificare
                - la lunghezza del pacchetto
            */
            messageIn = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(GREEN_UNDERLINED + "Lettura dei dati ricevuti dai partecipanti al gruppo" + RESET);
            System.out.println(GREEN_UNDERLINED + "Messaggio ricevuto dal gruppo " + group
                    + ":" + portGroup + "\n\t" + messageIn + RESET);

            //uscita dal gruppo tramite il metodo leaveGroup
            mSocket.leaveGroup(group);
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastClientUDP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Errore di risoluzione");
        } catch (SocketException ex) {
            Logger.getLogger(MulticastClientUDP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Errore di creazione socket");
        } catch (IOException ex) {
            Logger.getLogger(MulticastClientUDP.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Errore di I/O");
        } finally {
            if (dSocket != null) {
                dSocket.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        }
    }
}
