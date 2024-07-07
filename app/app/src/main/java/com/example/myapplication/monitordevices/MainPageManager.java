package com.example.myapplication.monitordevices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainPageManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main_page);
        String username = new SessionManager(getBaseContext()).getName();
        TextView welcomeText = findViewById(R.id.tvUserName);
        welcomeText.setText("Hello, " + username);

        List<Device> deviceList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // Ask for permission now, so that when user wants to add a new device, we are ready to access BLE
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("ASKING FOR ACCESS_FINE_LOCATION PERMISSION...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        new ServerManager().getUserDevices(username, new GetDevicesCallback() {
            @Override
            public void getDevices(ArrayList<Device> devices) {
                DevicesListAdapter adapter = new DevicesListAdapter(deviceList, new DevicesListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Device device) {
                        Intent intent = new Intent(getBaseContext(), StreamPageManager.class);
                        intent.putExtra("device", device);
                        startActivity(intent);
                    }
                });

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                System.out.println("NUMBER OF DEVICES: " + devices.size());
                int camNumber = 1;
                for (Device device : devices) {
                    System.out.println("ADDING DEVICE...");
                    device.setNumber(camNumber);
                    adapter.addDevice(device);
                    camNumber++;
                }
            }
        });
    }

    public void findNewDeviceButton_Clicked(View view) {
        System.out.println("In findNewDeviceButton_Clicked");
        System.out.println("Constructing BLEController...");
        BLEController bleController = new BLEController(this, this);
        System.out.println("BLEController constructed");
    }

    public void networksButton_Clicked(View view) {
        Intent intent = new Intent(this, NetworksPageManager.class);
        startActivity(intent);
    }
}
