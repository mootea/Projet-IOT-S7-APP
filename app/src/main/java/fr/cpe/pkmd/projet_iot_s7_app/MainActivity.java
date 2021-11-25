package fr.cpe.pkmd.projet_iot_s7_app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
    private InetAddress address;
    private DatagramSocket UDPSocket;
    private int PORT;
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
            UDPSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

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

        selectLum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLum.setBackgroundColor(Color.BLUE);
                selectTemp.setBackgroundColor(Color.BLACK);
                send_message.send("1");
            }
        });

        selectTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTemp.setBackgroundColor(Color.BLUE);
                selectLum.setBackgroundColor(Color.BLACK);
                send_message.send("2");
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initNetworking();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_message.send("3");
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initNetworking(){
        try { // Choix du port local laissé à la discrétion de la plateforme
            address = InetAddress.getByName(ip.getText().toString());
            PORT = Integer.parseInt(port.getText().toString());

            send_message = new SendMessage(UDPSocket, address, PORT);

            send_message.start();

            Listen listener = new Listen() {
                /*@Override
                public void listen(String lum, String temp) {
                    displayResult(lum, temp);
                }*/

                @Override
                public void listen(String lum) {
                    displayResult(lum, "ezez");
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
        Log.e("e","lalala");
        displayingLum.setText(lum);
        displayingTemp.setText(temp);

        displayingLum.setVisibility(View.VISIBLE);
        displayingTemp.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNetworking();
    }
}

