package com.example.myapplication.monitordevices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainPageManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main_page);
        String username = new SessionManager(getBaseContext()).getName();
        TextView welcomeText = (TextView) findViewById(R.id.tvUserName);
        welcomeText.setText("Hello, " + username);
        new ServerManager().getUserDevices(username, new GetDevicesCallback() {
            @Override
            public void getDevices(ArrayList<Device> devices) {
                System.out.println("NUMBER OF DEVICES: " + devices.size());;
            }
        });
    }
}
