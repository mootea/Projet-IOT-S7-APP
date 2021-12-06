package fr.cpe.pkmd.projet_iot_s7_app;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SendMessage extends Thread {

    private DatagramSocket UDPSocket;
    private InetAddress IP;
    private int PORT;

    private List<String> pile = new ArrayList<>();

    //Sémaphore qui va gérer la synchronisation des threads
    private Semaphore sem = new Semaphore(0);

    //Initialise la classe à l'aide d'un socket à l'aide d'une IP et d'un port donné
    public SendMessage(DatagramSocket socket, InetAddress address, int port){
        IP = address;
        PORT = port;
        UDPSocket = socket;
    }

    //Envoie le message (ajout dans la pile d'envoie et synchronisation à l'aide du sémaphore)
    public void send(String data){
        Log.e("message_internal", data);
        pile.add(data);
        sem.release();
    }

    @Override
    public void run() {
        while(true){
            try {
                sem.acquire();

                //Envoie les paquets
                byte[] data = pile.get(0).getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(data, data.length, IP, PORT);
                UDPSocket.send(packet);

                pile.remove(0);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
