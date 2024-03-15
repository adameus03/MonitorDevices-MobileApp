package com.example.myapplication.monitordevices;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;


public class BLEController {
    private BluetoothAdapter bluetoothAdapter;
    private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();
    private boolean scanning = false;
    private long SCAN_PERIOD = 10000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private Activity activity;

    public BLEController(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        BluetoothManager bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            throw new RuntimeException("Device doesn't support Bluetooth");
        }

        if (!bluetoothAdapter.isEnabled()) {
            throw new RuntimeException("Bluetooth is turned off");
        }

        scanForLE();
    }

    public void scanForLE() {
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanLeDevice(bluetoothLeScanner);
    }


    private void scanLeDevice(BluetoothLeScanner bluetoothLeScanner) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("ASKING FOR PERMISSION...");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        } else {
            if (!scanning) {
                // Stops scanning after a predefined scan period.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanning = false;
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            System.out.println("No Permission!");
                            return;
                        }
                        bluetoothLeScanner.stopScan(leScanCallback);
                        System.out.println("SCANNING STOPPED...");
                    }
                }, SCAN_PERIOD);

                scanning = true;
                bluetoothLeScanner.startScan(leScanCallback);
                System.out.println("SCANNING STARTED...");
            } else {
                scanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
                System.out.println("SCANNING STOPPED...");
            }
        }
    }

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    System.out.println("DISCOVERED NEW DEVICE: " + result.getDevice().toString());
                    leDeviceListAdapter.addDevice(result.getDevice());
                }

            };

}
