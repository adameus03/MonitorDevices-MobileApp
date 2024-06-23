package com.example.myapplication.monitordevices;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;
import java.net.URISyntaxException;

public class StreamPageManager extends AppCompatActivity {
    private ImageView streamView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_page);
        streamView = findViewById(R.id.streamView);
        Device device = getIntent().getParcelableExtra("device");
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = findViewById(R.id.streamPageCameraName);
        textView.setText("Camera " + device.getNumber());
        if (device != null) {
            System.out.println(device.toString());
            try {
                URI serverUri = new URI("ws://" + ServerManager.SERVER_SOCKET_STRING
                        + "/web-socket?username=" + new SessionManager(getBaseContext()).getName());
                MyWebSocketClient client = new MyWebSocketClient(serverUri,
                                                                 device,
                                                                 streamView,
                                                          this);
                client.connectBlocking(); // Wait for the connection to be established
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ImageView getStreamView() {
        return streamView;
    }
}
