
package fr.cpe.pkmd.projet_iot_s7_app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    private TextView ip;
    private TextView port;
    private DatagramSocket UDPSocket;
    private Button selectLum;
    private Button selectTemp;
    private TextView displayingLum;
    private TextView displayingTemp;
    private Button connect;
    private Button update;

    private SendMessage send_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            UDPSocket = new DatagramSocket();   //Ouverture du socket UDP pour communiquer avec les microcontrolleurs
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Récupération des identifiants graphiques
        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        displayingLum = findViewById(R.id.displayingLum);
        displayingTemp = findViewById(R.id.displayingTemp);
        connect = findViewById(R.id.connect);
        update = findViewById(R.id.update);
        selectLum = findViewById(R.id.selectLum);
        selectTemp = findViewById(R.id.selectTemp);
        selectLum.setBackgroundColor(Color.BLUE);
        selectTemp.setBackgroundColor(Color.BLUE);

        //On sélectionne la luminosité à afficher en premier
        selectLum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLum.setBackgroundColor(Color.BLUE);
                selectTemp.setBackgroundColor(Color.BLACK);
                send_message.send("1");
            }
        });

        //On sélectionne la température à afficher en premier
        selectTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTemp.setBackgroundColor(Color.BLUE);
                selectLum.setBackgroundColor(Color.BLACK);
                send_message.send("2");
            }
        });

        //Initialise la connexion
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNetworking();
            }
        });

        //Bouton qui permet d'aller chercher sur les controlleurs les informations de luminosité à afficher sur le téléphone
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayingLum.setText("Chargement...");
                displayingLum.setVisibility(View.VISIBLE);
                displayingTemp.setVisibility(View.INVISIBLE);
                send_message.send("3");
            }
        });

        //Réinitialise l'IHM
        ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_ihm();
            }
        });

        //Réinitialise l'IHM
        port.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_ihm();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initNetworking(){
        try { // Choix du port local laissé à la discrétion de la plateforme
            InetAddress address = InetAddress.getByName(ip.getText().toString());   //Récupère adresse IP écrite par l'utilisateur
            int PORT = Integer.parseInt(port.getText().toString()); //Récupère le port écrite par l'utilisateur

            send_message = new SendMessage(UDPSocket, address, PORT);   //Init le socket d'envoie de messages avec les infos ci-dessus

            send_message.start();   //Ouvre le socket

            connect.setEnabled(false);
            selectLum.setEnabled(true);
            selectTemp.setEnabled(true);
            update.setEnabled(true);

            //Listener quu écoute le changement de résultat et qui les importe dans ce fichier
            Listen listener = new Listen() {
                @Override
                public void listen(String temp, String lum) {
                    displayResult(temp, lum);
                }
            };
            (new ReceiverTask(UDPSocket, listener)).execute();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Affiche sur le téléphone les résultats de retour donnés par les controlleurs
    private void displayResult(String temp, String lum){

        String display_lum = lum + " lux";
        String display_temp = temp + "°C";

        displayingLum.setText(display_lum);
        displayingTemp.setText(display_temp);

        displayingLum.setVisibility(View.VISIBLE);
        displayingTemp.setVisibility(View.VISIBLE);
    }

    //Réinitialise l'IHM comme à son début
    private void reset_ihm(){
        connect.setEnabled(true);

        selectTemp.setBackgroundColor(Color.BLUE);
        selectLum.setBackgroundColor(Color.BLUE);

        selectLum.setEnabled(false);
        selectTemp.setEnabled(false);
        update.setEnabled(false);

        displayingLum.setText(null);
        displayingTemp.setText(null);

        displayingLum.setVisibility(View.INVISIBLE);
        displayingTemp.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        reset_ihm();
    }
}

