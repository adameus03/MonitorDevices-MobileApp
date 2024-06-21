package com.example.myapplication.monitordevices;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
}
