package com.example.myapplication.monitordevices;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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


public class BLEController {
    private BluetoothAdapter bluetoothAdapter;
    //private BLEDeviceListAdapter bleDeviceListAdapter = new BLEDeviceListAdapter();
    private boolean scanning = false;
    private long SCAN_PERIOD = 10000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private Activity activity;

    private BluetoothLeScanner bluetoothLeScanner;

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

        scanForBLE();
    }

    public void scanForBLE() {
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        //if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        //    System.out.println("ASKING FOR PERMISSION...");
        //    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        //}
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("ASKING FOR ACCESS_FINE_LOCATION PERMISSION...");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        boolean hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hasPermissions) {
            if (!scanning) {
                // Stops scanning after a predefined scan period.
                handler.postDelayed(() -> {
                    scanning = false;
                    if (!hasPermissions) {
                        // Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        System.out.println("In postDelayed callback, but no permissions");
                        return;
                    }
                    System.out.println("In postDelayed callback, stopping scan");
                    bluetoothLeScanner.stopScan(bleScanCallback);
                    System.out.println("SCANNING STOPPED by postDelayed callback");
                }, SCAN_PERIOD);

                scanning = true;
                System.out.println("STARTING SCAN...");
                bluetoothLeScanner.startScan(bleScanCallback);
                System.out.println("SCANNING STARTED...");
            } else {
                scanning = false;
                bluetoothLeScanner.stopScan(bleScanCallback);
                System.out.println("SCANNING STOPPED...");
            }
        } else {
            System.out.println("No Permission - nothing to do");
        }

    }

    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            try {
                String deviceName = device.getName();
                //String deviceAlias = device.getAlias();
                System.out.println("DISCOVERED NEW DEVICE: " + result.getDevice().toString() + ", name: " + deviceName);

                if (deviceName != null && deviceName.equals("SAU Camera Device")) {
                    System.out.println("Found SAU camera device: " + device.toString());
                    bluetoothLeScanner.stopScan(bleScanCallback);
                    System.out.println("SCANNING STOPPED AS THE TARGET DEVICE WAS FOUND.");

                    setupDevice(device);
                }
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }

            //bleDeviceListAdapter.addDevice(result.getDevice());
        }
    };

    /**
     * Configures the device to operate after registration.
     * This is done by connecting to the GATT server running on the device.
     * @param device The BLE device to configure
     */
    private void setupDevice(BluetoothDevice device) {
        System.out.println("In setupDevice");
        // Communicate with GATT server on the device
        // ...
    }
}
