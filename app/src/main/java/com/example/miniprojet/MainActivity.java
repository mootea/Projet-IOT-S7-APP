package com.example.miniprojet;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    private TextView ip;
    private TextView port;
    private InetAddress address;
    private DatagramSocket UDPSocket;
    private int PORT;
    private Button selectLum;
    private Button selectTemp;
    private TextView displayingLum;
    private TextView displayingTemp;

    private SendMessage send_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        displayingLum = findViewById(R.id.displayingLum);
        displayingTemp = findViewById(R.id.displayingTemp);

        selectLum = findViewById(R.id.selectLum);
        selectTemp = findViewById(R.id.selectTemp);
        selectLum.setBackgroundColor(Color.BLUE);
        selectTemp.setBackgroundColor(Color.BLUE);

        selectLum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLum.setBackgroundColor(Color.BLUE);
                selectTemp.setBackgroundColor(Color.BLACK);
                sendJForward("1");
            }
        });

        selectTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTemp.setBackgroundColor(Color.BLUE);
                selectLum.setBackgroundColor(Color.BLACK);
                sendJForward("2");
            }
        });

        PORT = Integer.parseInt(port.getText().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initNetworking(){
        try { // Choix du port local laissé à la discrétion de la plateforme
            UDPSocket = new DatagramSocket();
            address = InetAddress.getByName(ip.getText().toString());

            send_message = new SendMessage(UDPSocket, address, PORT);

            send_message.start();

            Listen listener = new Listen() {
                @Override
                public void listen(String lum, String temp) {
                    displayResult(lum, temp);
                }
            };
            (new ReceiverTask(UDPSocket, listener)).execute();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void displayResult(String lum, String temp){
        // Affiche et reset le jeu en cas de win / loose
        displayingLum.setText(lum);
        displayingTemp.setText(temp);

        displayingLum.setVisibility(View.VISIBLE);
        displayingTemp.setVisibility(View.VISIBLE);
    }

    private void sendJForward(String whichSensor){
        send_message.send(whichSensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNetworking();
    }
}

