package fr.cpe.pkmd.projet_iot_s7_app;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

//Reçoit les informations du socket et les redirige
public class ReceiverTask extends AsyncTask<Void, byte[], Void> {
    private DatagramSocket UDPSocket;
    private Listen listen;

    //Constructeur
    public ReceiverTask(DatagramSocket socket, Listen listener){
        listen = listener;
        UDPSocket = socket;
    }

    //Publie les données reçues
    @Override
    protected Void doInBackground(Void... voids) {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                UDPSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int size = packet.getLength();
            publishProgress(Arrays.copyOf(packet.getData(), size));
        }
    }

    // Espace de réception des données
    protected void onProgressUpdate(byte[]... data) {
        String[] response = new String(data[0]).split(";");

        if (response.length == 3) {
            listen.listen(response[0], response[1]);
        }

    }
}
