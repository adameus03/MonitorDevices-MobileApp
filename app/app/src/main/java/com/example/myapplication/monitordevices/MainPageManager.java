package com.example.myapplication.monitordevices;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainPageManager extends AppCompatActivity {
    static final String TAG = "MainPageManager";
    //ImageView imageView_syncCams;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main_page);

        context = this;
        //imageView_syncCams = findViewById(R.id.imageView_syncCams);

        String username = new SessionManager(getBaseContext()).getName();
        TextView welcomeText = findViewById(R.id.tvUserName);
        welcomeText.setText("Hello, " + username);

        List<Device> deviceList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // TODO for sure?

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

        Intent intent = new Intent(getBaseContext(), NetworksPageManager.class);
        intent.putExtra("isDeviceIntermediate", true);
        startActivityForResult(intent, 0);
    }

    public void networksButton_Clicked(View view) {
        Intent intent = new Intent(this, NetworksPageManager.class);
        startActivity(intent);
    }

    public void imageView_syncCams_Clicked(View view) {
        System.out.println("In imageView_syncCams_Clicked");
        finish();
        Intent intent = new Intent(this, MainPageManager.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == BLEController.BLE_CONTROLLER_RESULT_BT_NOT_SUPPORTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Bluetooth not supported")
                        .setMessage("Your device doesn't support Bluetooth, sorry!")
                        .show();
            } else if (resultCode == BLEController.BLE_CONTROLLER_RESULT_BT_OFF) {
                new AlertDialog.Builder(this)
                        .setTitle("Bluetooth turned off")
                        .setMessage("Please turn Bluetooth on!")
                        .show();
            } else if (resultCode == BLEController.BLE_CONTROLLER_RESULT_DEV_NOT_FOUND) {
                new AlertDialog.Builder(this)
                        .setTitle("Camera device not found")
                        .setMessage("Is the camera turned on? Is it close enough? Are you sure you aren't trying to register an already registered camera? (it has bluetooth LE turned off then, need to unregister it first to turn it into registration mode).")
                        .show();
                System.out.println("After alert");
            } else if (resultCode == BLEController.BLE_CONTROLLER_RESULT_DEV_WIFI_CONN_FAIL) {
                new AlertDialog.Builder(this)
                        .setTitle("Camera device failed to connect Wi-Fi network")
                        .setMessage("Try again? Possibly fix Wi-Fi SSID and/or password.")
                        .show();
            } else if (resultCode == BLEController.BLE_CONTROLLER_RESULT_DEV_WIFI_CONN_SUCCESS) {
                new AlertDialog.Builder(this)
                        .setTitle("Camera device connected to Wi-Fi network")
                        .setMessage("Registration will be completed soon with the server. You can close the app and after you reopen it, the added camera should be visible.") // TODO save the user from needing to close and reopen the app
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                System.out.println("Auto reload MainPageManager activity");
                                finish();
                                Intent intent = new Intent(context, MainPageManager.class);
                                startActivity(intent);
                            }
                        })
                        .show();
                System.out.println("After alert");
            } else if (resultCode == BLEController.BLE_CONTROLLER_RESULT_ERROR_NULL_SERVICE) {
                System.out.println("Unknown error while handling BLE");
                new AlertDialog.Builder(this)
                        .setTitle("Sorry, something went wrong (BLE_CONTROLLER_RESULT_ERROR_NULL_SERVICE)")
                        .setMessage("Please try again.")
                        .show();
            } else if (resultCode == NetworksPageManager.NETWORKS_PAGE_MANAGER_RESULT_NETWORK_ADDED) {
                System.out.println("Returning to network selector for device registration");
                Intent intent = new Intent(getBaseContext(), NetworksPageManager.class);
                intent.putExtra("isDeviceIntermediate", true);
                startActivityForResult(intent, 0);
            } else {
                Log.w(TAG, "onActivityResult: Unknown result code: " + resultCode);
            }
        } else {
            throw new RuntimeException("onActivityResult: Unknown request code");
        }
    }
}
